package com.example.data.repository

import com.example.data.local.BookmarkDao
import com.example.data.local.DownloadTaskDao
import com.example.data.model.Bookmark
import com.example.data.model.DownloadTask
import com.example.data.model.ExecutionState
import com.example.data.model.RenewalLogEntry
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DownloadRepository(
    private val dao: DownloadTaskDao,
    private val bookmarkDao: BookmarkDao
) {

    val allTasks: Flow<List<DownloadTask>> = dao.getAllTasks()
    val allLogs: Flow<List<RenewalLogEntry>> = dao.getAllLogs()
    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()

    suspend fun getTask(id: String): DownloadTask? = dao.getTaskById(id)

    suspend fun insertTask(task: DownloadTask) = dao.insertTask(task)

    suspend fun updateTask(task: DownloadTask) = dao.updateTask(task)

    suspend fun deleteTask(task: DownloadTask) = dao.deleteTask(task)

    suspend fun deleteTaskById(id: String) = dao.deleteTaskById(id)

    suspend fun updateState(id: String, state: String, errorCode: Int? = null, errorMessage: String? = null) {
        dao.updateTaskState(id, state, errorCode, errorMessage)
    }

    suspend fun updateProgress(id: String, downloaded: Long, total: Long, speed: Long, eta: Long) {
        dao.updateProgress(id, downloaded, total, speed, eta)
    }

    suspend fun updateTaskUrlAndResume(id: String, newUrl: String, newState: String = ExecutionState.DOWNLOADING) {
        dao.updateTaskUrlAndResume(id, newUrl, newState)
    }

    suspend fun logEvent(
        taskId: String,
        taskTitle: String,
        eventType: String,
        description: String,
        isSuccess: Boolean = true
    ) {
        dao.insertLog(
            RenewalLogEntry(
                taskId = taskId,
                taskTitle = taskTitle,
                eventType = eventType,
                description = description,
                isSuccess = isSuccess
            )
        )
    }

    suspend fun clearLogs() = dao.clearLogs()

    // Bookmark Operations
    suspend fun insertBookmark(bookmark: Bookmark) = bookmarkDao.insertBookmark(bookmark)

    suspend fun deleteBookmarkById(id: String) = bookmarkDao.deleteBookmarkById(id)

    suspend fun incrementBookmarkVisit(id: String) = bookmarkDao.incrementVisitCount(id)

    suspend fun seedBookmarksIfEmpty() {
        if (bookmarkDao.countBookmarks() == 0) {
            val defaultBookmarks = listOf(
                // User Requested Movie Sites
                Bookmark(
                    id = "bm-worldfree4u",
                    title = "WorldFree4u",
                    url = "https://worldfree4u.expert/",
                    category = "Movies",
                    iconType = "movie",
                    accentColorHex = "#FFB703",
                    visitCount = 12
                ),
                Bookmark(
                    id = "bm-bollyzilla",
                    title = "BollyZilla",
                    url = "https://bollyzilla.xyz/",
                    category = "Movies",
                    iconType = "movie",
                    accentColorHex = "#FB8500",
                    visitCount = 10
                ),
                Bookmark(
                    id = "bm-moviesspace",
                    title = "MoviesSpace Punjabi",
                    url = "https://www.moviesspace.net/browse/category/punjabi",
                    category = "Movies",
                    iconType = "movie",
                    accentColorHex = "#06D6A0",
                    visitCount = 9
                ),
                Bookmark(
                    id = "bm-filmvup",
                    title = "FilmVup",
                    url = "https://filmvup.com/",
                    category = "Movies",
                    iconType = "movie",
                    accentColorHex = "#8338EC",
                    visitCount = 8
                ),
                Bookmark(
                    id = "bm-watchmovies",
                    title = "Watch Movies PK",
                    url = "https://www.watch-movies.com.pk/category/free-indian-movies-watch/2025-movies/",
                    category = "Movies",
                    iconType = "movie",
                    accentColorHex = "#E63946",
                    visitCount = 7
                ),
                // Top Social Media Platforms
                Bookmark(
                    id = "bm-youtube",
                    title = "YouTube",
                    url = "https://www.youtube.com",
                    category = "Social",
                    iconType = "youtube",
                    accentColorHex = "#FF0000",
                    visitCount = 25
                ),
                Bookmark(
                    id = "bm-instagram",
                    title = "Instagram",
                    url = "https://www.instagram.com",
                    category = "Social",
                    iconType = "instagram",
                    accentColorHex = "#E1306C",
                    visitCount = 20
                ),
                Bookmark(
                    id = "bm-tiktok",
                    title = "TikTok",
                    url = "https://www.tiktok.com",
                    category = "Social",
                    iconType = "tiktok",
                    accentColorHex = "#00F2FE",
                    visitCount = 16
                ),
                Bookmark(
                    id = "bm-facebook",
                    title = "Facebook",
                    url = "https://www.facebook.com",
                    category = "Social",
                    iconType = "facebook",
                    accentColorHex = "#1877F2",
                    visitCount = 14
                ),
                Bookmark(
                    id = "bm-x",
                    title = "X (Twitter)",
                    url = "https://x.com",
                    category = "Social",
                    iconType = "twitter",
                    accentColorHex = "#FFFFFF",
                    visitCount = 11
                )
            )
            bookmarkDao.insertBookmarks(defaultBookmarks)
        }
    }

    suspend fun seedDemoTasksIfEmpty() {
        val now = System.currentTimeMillis()
        val demoTasks = listOf(
            DownloadTask(
                id = "task-vm-01",
                fileName = "Cyberpunk_NightCity_4K_60FPS.mp4",
                currentDownloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4?token=7b92f0a1&expire=1738290000",
                originalSourcePageUrl = "https://www.youtube.com/watch?v=LembwKDo1Dk",
                executionState = ExecutionState.DOWNLOADING,
                downloadedBytes = 54_200_000L,
                totalBytes = 158_000_000L,
                fileMimeType = "video/mp4",
                downloadSpeedBps = 3_840_000L,
                etaSeconds = 27L,
                autoRenewEnabled = true,
                createdAt = now - 120_000L
            ),
            DownloadTask(
                id = "task-vm-02",
                fileName = "Elephants_Dream_1080p_Remastered.mp4",
                currentDownloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4?token=exp_999a4c&simulate_403=true",
                originalSourcePageUrl = "https://vimeo.com/channels/staffpicks/89123847",
                executionState = ExecutionState.LINK_EXPIRED,
                downloadedBytes = 82_400_000L,
                totalBytes = 210_000_000L,
                fileMimeType = "video/mp4",
                downloadSpeedBps = 0L,
                etaSeconds = 0L,
                lastErrorCode = 403,
                lastErrorMessage = "HTTP 403 Forbidden: CDN Stream signature expired (Timestamp mismatch)",
                autoRenewEnabled = true,
                createdAt = now - 360_000L
            ),
            DownloadTask(
                id = "task-vm-03",
                fileName = "Synthwave_Radio_2026_Live_Audio.m4a",
                currentDownloadUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4?token=audio_88a9",
                originalSourcePageUrl = "https://soundcloud.com/stream/synthwave-dark-session",
                executionState = ExecutionState.COMPLETED,
                downloadedBytes = 48_300_000L,
                totalBytes = 48_300_000L,
                fileMimeType = "audio/mp4",
                downloadSpeedBps = 0L,
                etaSeconds = 0L,
                autoRenewEnabled = true,
                createdAt = now - 860_000L
            )
        )
        dao.insertTasks(demoTasks)

        dao.insertLog(
            RenewalLogEntry(
                taskId = "task-vm-02",
                taskTitle = "Elephants_Dream_1080p_Remastered.mp4",
                eventType = "403_DETECTED",
                description = "HTTP 403 Forbidden received. CDN token expired. Auto-renewal queued or manual override available.",
                isSuccess = false
            )
        )
    }
}
