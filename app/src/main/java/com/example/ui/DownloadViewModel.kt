package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Bookmark
import com.example.data.model.DownloadTask
import com.example.data.model.ExecutionState
import com.example.data.model.RenewalLogEntry
import com.example.data.repository.DownloadRepository
import com.example.engine.DownloadEngine
import com.example.engine.ExtractorEngine
import com.example.engine.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class FilterTab {
    ALL,
    ACTIVE,
    EXPIRED,
    COMPLETED
}

enum class NavigationSection {
    DOWNLOADS,
    BOOKMARKS,
    DIAGNOSTICS
}

data class ManualOverrideUiState(
    val task: DownloadTask? = null,
    val inputUrl: String = "",
    val isValidating: Boolean = false,
    val validationResult: ValidationResult? = null,
    val errorMessage: String? = null
)

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = DownloadRepository(database.downloadTaskDao(), database.bookmarkDao())
    val extractorEngine = ExtractorEngine()
    val downloadEngine = DownloadEngine(application, repository, extractorEngine)

    private val _currentSection = MutableStateFlow(NavigationSection.DOWNLOADS)
    val currentSection: StateFlow<NavigationSection> = _currentSection.asStateFlow()

    private val _selectedFilter = MutableStateFlow(FilterTab.ALL)
    val selectedFilter: StateFlow<FilterTab> = _selectedFilter.asStateFlow()

    private val _showNewDownloadDialog = MutableStateFlow(false)
    val showNewDownloadDialog: StateFlow<Boolean> = _showNewDownloadDialog.asStateFlow()

    private val _showLogsSheet = MutableStateFlow(false)
    val showLogsSheet: StateFlow<Boolean> = _showLogsSheet.asStateFlow()

    private val _showAddBookmarkDialog = MutableStateFlow(false)
    val showAddBookmarkDialog: StateFlow<Boolean> = _showAddBookmarkDialog.asStateFlow()

    private val _selectedBookmarkForViewing = MutableStateFlow<Bookmark?>(null)
    val selectedBookmarkForViewing: StateFlow<Bookmark?> = _selectedBookmarkForViewing.asStateFlow()

    private val _manualOverrideState = MutableStateFlow(ManualOverrideUiState())
    val manualOverrideState: StateFlow<ManualOverrideUiState> = _manualOverrideState.asStateFlow()

    private val _isAutoRenewingTaskId = MutableStateFlow<String?>(null)
    val isAutoRenewingTaskId: StateFlow<String?> = _isAutoRenewingTaskId.asStateFlow()

    val allTasks: StateFlow<List<DownloadTask>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<RenewalLogEntry>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTasks: StateFlow<List<DownloadTask>> = combine(allTasks, selectedFilter) { tasks, filter ->
        when (filter) {
            FilterTab.ALL -> tasks
            FilterTab.ACTIVE -> tasks.filter { it.executionState == ExecutionState.DOWNLOADING || it.executionState == ExecutionState.PAUSED }
            FilterTab.EXPIRED -> tasks.filter { it.executionState == ExecutionState.LINK_EXPIRED }
            FilterTab.COMPLETED -> tasks.filter { it.executionState == ExecutionState.COMPLETED }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expiredCount: StateFlow<Int> = allTasks.combine(_selectedFilter) { tasks, _ ->
        tasks.count { it.executionState == ExecutionState.LINK_EXPIRED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeCount: StateFlow<Int> = allTasks.combine(_selectedFilter) { tasks, _ ->
        tasks.count { it.executionState == ExecutionState.DOWNLOADING }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.seedBookmarksIfEmpty()
            repository.seedDemoTasksIfEmpty()
            // Start downloading active tasks
            val initial = repository.getTask("task-vm-01")
            if (initial != null && initial.executionState == ExecutionState.DOWNLOADING) {
                downloadEngine.startDownload(initial.id)
            }
        }
    }

    fun setSection(section: NavigationSection) {
        _currentSection.value = section
    }

    fun setFilter(filter: FilterTab) {
        _selectedFilter.value = filter
    }

    fun onToggleDownload(task: DownloadTask) {
        if (task.executionState == ExecutionState.DOWNLOADING) {
            downloadEngine.pauseDownload(task.id)
        } else if (task.executionState == ExecutionState.PAUSED) {
            downloadEngine.resumeDownload(task.id)
        } else if (task.executionState == ExecutionState.LINK_EXPIRED) {
            triggerAutoRenewal(task)
        }
    }

    fun simulate403(taskId: String) {
        downloadEngine.simulateExpiryError(taskId, 403)
    }

    fun triggerAutoRenewal(task: DownloadTask) {
        viewModelScope.launch {
            _isAutoRenewingTaskId.value = task.id
            downloadEngine.triggerAutoRenewal(task)
            _isAutoRenewingTaskId.value = null
        }
    }

    fun openManualOverride(task: DownloadTask) {
        _manualOverrideState.value = ManualOverrideUiState(
            task = task,
            inputUrl = "",
            isValidating = false,
            validationResult = null,
            errorMessage = null
        )
    }

    fun closeManualOverride() {
        _manualOverrideState.value = ManualOverrideUiState()
    }

    fun updateManualOverrideInput(url: String) {
        _manualOverrideState.value = _manualOverrideState.value.copy(
            inputUrl = url,
            validationResult = null,
            errorMessage = null
        )
    }

    fun validateManualOverride() {
        val current = _manualOverrideState.value
        val task = current.task ?: return
        val url = current.inputUrl.trim()

        if (url.isEmpty()) {
            _manualOverrideState.value = current.copy(errorMessage = "Please enter a valid URL")
            return
        }

        viewModelScope.launch {
            _manualOverrideState.value = current.copy(isValidating = true, errorMessage = null)
            val result = extractorEngine.validateManualUrl(
                newUrl = url,
                expectedMimeType = task.fileMimeType,
                downloadedBytes = task.downloadedBytes
            )
            _manualOverrideState.value = _manualOverrideState.value.copy(
                isValidating = false,
                validationResult = result,
                errorMessage = if (result is ValidationResult.Invalid) result.reason else null
            )
        }
    }

    fun applyManualOverrideAndResume() {
        val current = _manualOverrideState.value
        val task = current.task ?: return
        val url = current.inputUrl.trim()

        viewModelScope.launch {
            downloadEngine.applyManualLinkOverride(task.id, url)
            closeManualOverride()
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            downloadEngine.pauseDownload(taskId)
            repository.deleteTaskById(taskId)
        }
    }

    fun openNewDownloadDialog() {
        _showNewDownloadDialog.value = true
    }

    fun closeNewDownloadDialog() {
        _showNewDownloadDialog.value = false
    }

    fun openLogsSheet() {
        _showLogsSheet.value = true
    }

    fun closeLogsSheet() {
        _showLogsSheet.value = false
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    // Bookmark actions
    fun openAddBookmarkDialog() {
        _showAddBookmarkDialog.value = true
    }

    fun closeAddBookmarkDialog() {
        _showAddBookmarkDialog.value = false
    }

    fun openSiteViewer(bookmark: Bookmark) {
        _selectedBookmarkForViewing.value = bookmark
        viewModelScope.launch {
            repository.incrementBookmarkVisit(bookmark.id)
        }
    }

    fun closeSiteViewer() {
        _selectedBookmarkForViewing.value = null
    }

    fun addCustomBookmark(title: String, url: String, category: String, colorHex: String) {
        viewModelScope.launch {
            val bm = Bookmark(
                id = "bm-" + UUID.randomUUID().toString().take(8),
                title = title.ifBlank { "Custom Portal" },
                url = url.trim(),
                category = category,
                accentColorHex = colorHex,
                isFavorite = true
            )
            repository.insertBookmark(bm)
            closeAddBookmarkDialog()
        }
    }

    fun deleteBookmark(id: String) {
        viewModelScope.launch {
            repository.deleteBookmarkById(id)
        }
    }

    fun queueDownloadFromLink(
        title: String,
        sourceUrl: String,
        directUrl: String,
        mimeType: String,
        sizeBytes: Long
    ) {
        viewModelScope.launch {
            val taskId = "task-" + UUID.randomUUID().toString().take(8)
            val task = DownloadTask(
                id = taskId,
                fileName = title,
                currentDownloadUrl = directUrl,
                originalSourcePageUrl = sourceUrl,
                executionState = ExecutionState.DOWNLOADING,
                downloadedBytes = 0L,
                totalBytes = sizeBytes,
                fileMimeType = mimeType,
                downloadSpeedBps = 0L,
                etaSeconds = 0L,
                autoRenewEnabled = true,
                createdAt = System.currentTimeMillis()
            )

            repository.insertTask(task)
            repository.logEvent(
                taskId = taskId,
                taskTitle = title,
                eventType = "TASK_CREATED",
                description = "4K/HD Video stream queued. Anti-Expiration Auto-Renewal active.",
                isSuccess = true
            )

            _currentSection.value = NavigationSection.DOWNLOADS
            downloadEngine.startDownload(taskId)
        }
    }

    fun createDownloadTask(
        title: String,
        sourcePageUrl: String,
        directCdnUrl: String,
        mimeType: String,
        totalBytesMb: Long,
        autoRenew: Boolean
    ) {
        queueDownloadFromLink(
            title = title.ifBlank { "download_media.mp4" },
            sourceUrl = sourcePageUrl.ifBlank { "https://media-hub.stream/watch?v=sample" },
            directUrl = directCdnUrl.ifBlank {
                "https://cdn.vidmate-engine.stream/media/$title?token=${UUID.randomUUID().toString().take(12)}&expire=${(System.currentTimeMillis() / 1000) + 14400}"
            },
            mimeType = mimeType,
            sizeBytes = totalBytesMb * 1024 * 1024
        )
        closeNewDownloadDialog()
    }
}
