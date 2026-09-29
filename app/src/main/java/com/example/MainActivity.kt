package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DownloadViewModel
import com.example.ui.FilterTab
import com.example.ui.NavigationSection
import com.example.ui.components.AddBookmarkDialog
import com.example.ui.components.BookmarksSection
import com.example.ui.components.DownloadCard
import com.example.ui.components.EngineHeader
import com.example.ui.components.FlowDiagramCard
import com.example.ui.components.LinkAnalyzerCard
import com.example.ui.components.ManualOverrideBottomSheet
import com.example.ui.components.NewDownloadDialog
import com.example.ui.components.RenewalLogsSheet
import com.example.ui.components.SiteViewerSheet
import com.example.ui.theme.AlertRed
import com.example.ui.theme.DeepSlateGrey
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.OLEDBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TealBlur
import com.example.ui.theme.VidmateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VidmateTheme {
                RehmanApp()
            }
        }
    }
}

@Composable
fun RehmanApp(
    viewModel: DownloadViewModel = viewModel()
) {
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val activeCount by viewModel.activeCount.collectAsStateWithLifecycle()
    val expiredCount by viewModel.expiredCount.collectAsStateWithLifecycle()
    val isAutoRenewingTaskId by viewModel.isAutoRenewingTaskId.collectAsStateWithLifecycle()
    val manualOverrideState by viewModel.manualOverrideState.collectAsStateWithLifecycle()
    val showNewDialog by viewModel.showNewDownloadDialog.collectAsStateWithLifecycle()
    val showLogsSheet by viewModel.showLogsSheet.collectAsStateWithLifecycle()
    val showAddBookmark by viewModel.showAddBookmarkDialog.collectAsStateWithLifecycle()
    val selectedBookmarkForViewing by viewModel.selectedBookmarkForViewing.collectAsStateWithLifecycle()
    val bookmarks by viewModel.allBookmarks.collectAsStateWithLifecycle()
    val logs by viewModel.allLogs.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = OLEDBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EngineHeader(
                selectedFilter = selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) },
                activeCount = activeCount,
                expiredCount = expiredCount,
                onOpenLogs = { viewModel.openLogsSheet() },
                onNewDownload = { viewModel.openNewDownloadDialog() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepSlateGrey,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(1.dp, SurfaceBorder)
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentSection == NavigationSection.DOWNLOADS,
                    onClick = { viewModel.setSection(NavigationSection.DOWNLOADS) },
                    icon = {
                        BadgedBox(badge = {
                            if (expiredCount > 0) {
                                Badge(containerColor = AlertRed) { Text("$expiredCount", color = OLEDBlack) }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = "Downloader")
                        }
                    },
                    label = { Text("Downloader", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navBarColors()
                )

                NavigationBarItem(
                    selected = currentSection == NavigationSection.BOOKMARKS,
                    onClick = { viewModel.setSection(NavigationSection.BOOKMARKS) },
                    icon = {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Bookmarks")
                    },
                    label = { Text("Bookmarks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navBarColors()
                )

                NavigationBarItem(
                    selected = currentSection == NavigationSection.DIAGNOSTICS,
                    onClick = { viewModel.setSection(NavigationSection.DIAGNOSTICS) },
                    icon = {
                        Icon(imageVector = Icons.Default.Hub, contentDescription = "Engine Logs")
                    },
                    label = { Text("Protocol Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navBarColors()
                )
            }
        },
        floatingActionButton = {
            if (currentSection == NavigationSection.DOWNLOADS) {
                FloatingActionButton(
                    onClick = { viewModel.openNewDownloadDialog() },
                    containerColor = NeonAmber,
                    contentColor = OLEDBlack,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_new_download")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Download Task",
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (currentSection == NavigationSection.BOOKMARKS) {
                FloatingActionButton(
                    onClick = { viewModel.openAddBookmarkDialog() },
                    containerColor = NeonAmber,
                    contentColor = OLEDBlack,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_add_bookmark")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Bookmark",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(OLEDBlack)
        ) {
            when (currentSection) {
                NavigationSection.DOWNLOADS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("downloads_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Smart Link Analyzer Card with Auto Platform Recognition & 4K/HD options
                        item {
                            LinkAnalyzerCard(
                                onDownloadQualitySelected = { title, source, direct, mime, size ->
                                    viewModel.queueDownloadFromLink(title, source, direct, mime, size)
                                }
                            )
                        }

                        // Quick Bookmarks Carousel banner
                        item {
                            BookmarksSection(
                                bookmarks = bookmarks.take(6),
                                onLaunchSite = { viewModel.openSiteViewer(it) },
                                onAddBookmark = { viewModel.openAddBookmarkDialog() },
                                onDeleteBookmark = { viewModel.deleteBookmark(it) }
                            )
                        }

                        // Expired Alert Notice Banner
                        if (expiredCount > 0 && selectedFilter != FilterTab.EXPIRED) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF2A0D0B))
                                        .border(1.dp, AlertRed.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setFilter(FilterTab.EXPIRED) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "$expiredCount task(s) require link renewal!",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AlertRed
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "View", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HighContrastWhite)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = HighContrastWhite, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Downloads list header
                        item {
                            Text(
                                text = "ACTIVE & FINISHED DOWNLOADS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedGrey,
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (tasks.isEmpty()) {
                            item {
                                EmptyTasksView(
                                    filter = selectedFilter,
                                    onAddNew = { viewModel.openNewDownloadDialog() }
                                )
                            }
                        } else {
                            items(tasks, key = { it.id }) { task ->
                                DownloadCard(
                                    task = task,
                                    isAutoRenewing = isAutoRenewingTaskId == task.id,
                                    onTogglePlayPause = { viewModel.onToggleDownload(task) },
                                    onManualOverride = { viewModel.openManualOverride(task) },
                                    onSimulate403 = { viewModel.simulate403(task.id) },
                                    onDelete = { viewModel.deleteTask(task.id) }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }

                NavigationSection.BOOKMARKS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("bookmarks_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            BookmarksSection(
                                bookmarks = bookmarks,
                                onLaunchSite = { viewModel.openSiteViewer(it) },
                                onAddBookmark = { viewModel.openAddBookmarkDialog() },
                                onDeleteBookmark = { viewModel.deleteBookmark(it) }
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceElevated)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "1-Click Direct Access Supported Portals",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonAmber
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "• WorldFree4u (worldfree4u.expert)\n• BollyZilla (bollyzilla.xyz)\n• MoviesSpace Punjabi Cinema (moviesspace.net)\n• FilmVup Web Series (filmvup.com)\n• Watch Movies PK (watch-movies.com.pk)\n• Plus YouTube, Instagram, TikTok, Facebook, X",
                                        fontSize = 11.sp,
                                        color = HighContrastWhite,
                                        lineHeight = 18.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }

                NavigationSection.DIAGNOSTICS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("diagnostics_list"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            FlowDiagramCard()
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LIVE PROTOCOL AUDIT LOGS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonAmber,
                                    letterSpacing = 0.5.sp
                                )
                                androidx.compose.material3.OutlinedButton(
                                    onClick = { viewModel.clearLogs() },
                                    modifier = Modifier.height(28.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = MutedGrey),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                                ) {
                                    Text("Clear", fontSize = 10.sp)
                                }
                            }
                        }

                        if (logs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DeepSlateGrey),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No renewal events yet. Simulate a 403 error to inspect live logs.", color = MutedGrey, fontSize = 12.sp)
                                }
                            }
                        } else {
                            items(logs, key = { it.id }) { log ->
                                val badgeColor = when (log.eventType) {
                                    "403_DETECTED", "EXPIRY_DETECTED" -> AlertRed
                                    "EXTRACTOR_TRIGGERED" -> NeonAmber
                                    "TOKEN_REFRESHED", "RESUMED_OFFSET" -> com.example.ui.theme.SuccessGreen
                                    "MANUAL_OVERRIDE" -> TealBlur
                                    else -> MutedGrey
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DeepSlateGrey)
                                        .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(badgeColor.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = log.eventType,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = badgeColor,
                                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                )
                                            }

                                            Text(
                                                text = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.US).format(java.util.Date(log.timestamp)),
                                                fontSize = 10.sp,
                                                color = MutedGrey,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = log.description, fontSize = 11.sp, color = HighContrastWhite)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "Task: ${log.taskTitle}", fontSize = 10.sp, color = MutedGrey)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }
    }

    // Site Viewer Sheet for 1-Click portal browsing
    if (selectedBookmarkForViewing != null) {
        SiteViewerSheet(
            bookmark = selectedBookmarkForViewing!!,
            onGrabLink = { url ->
                val detected = com.example.engine.PlatformDetector.detect(url)
                val topQuality = detected.availableQualities.firstOrNull { it.isRecommended }
                    ?: detected.availableQualities.first()
                viewModel.queueDownloadFromLink(
                    title = detected.estimatedTitle,
                    sourceUrl = url,
                    directUrl = detected.directCdnStreamUrl,
                    mimeType = topQuality.mimeType,
                    sizeBytes = topQuality.sizeBytes
                )
            },
            onDismiss = { viewModel.closeSiteViewer() }
        )
    }

    // Add Bookmark Dialog
    if (showAddBookmark) {
        AddBookmarkDialog(
            onDismiss = { viewModel.closeAddBookmarkDialog() },
            onSave = { title, url, category, colorHex ->
                viewModel.addCustomBookmark(title, url, category, colorHex)
            }
        )
    }

    // Manual Link Override Bottom Sheet
    if (manualOverrideState.task != null) {
        ManualOverrideBottomSheet(
            state = manualOverrideState,
            onUrlChange = { viewModel.updateManualOverrideInput(it) },
            onValidate = { viewModel.validateManualOverride() },
            onApplyAndResume = { viewModel.applyManualOverrideAndResume() },
            onDismiss = { viewModel.closeManualOverride() }
        )
    }

    // New Download Task Dialog / Sheet
    if (showNewDialog) {
        NewDownloadDialog(
            onDismiss = { viewModel.closeNewDownloadDialog() },
            onAdd = { title, source, direct, mime, size, autoRenew ->
                viewModel.createDownloadTask(title, source, direct, mime, size, autoRenew)
            }
        )
    }

    // Renewal Logs Audit Sheet
    if (showLogsSheet) {
        RenewalLogsSheet(
            logs = logs,
            onClearLogs = { viewModel.clearLogs() },
            onDismiss = { viewModel.closeLogsSheet() }
        )
    }
}

@Composable
private fun navBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = OLEDBlack,
    selectedTextColor = NeonAmber,
    indicatorColor = NeonAmber,
    unselectedIconColor = MutedGrey,
    unselectedTextColor = MutedGrey
)

@Composable
private fun EmptyTasksView(
    filter: FilterTab,
    onAddNew: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSlateGrey)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = MutedGrey,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = when (filter) {
                    FilterTab.ALL -> "No download tasks"
                    FilterTab.ACTIVE -> "No active downloads"
                    FilterTab.EXPIRED -> "No expired links"
                    FilterTab.COMPLETED -> "No completed downloads"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HighContrastWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when (filter) {
                    FilterTab.EXPIRED -> "Great! All streaming tokens are currently valid."
                    else -> "Paste a video link above or tap the + button to queue an HD/4K download."
                },
                fontSize = 12.sp,
                color = MutedGrey,
                textAlign = TextAlign.Center
            )
        }
    }
}
