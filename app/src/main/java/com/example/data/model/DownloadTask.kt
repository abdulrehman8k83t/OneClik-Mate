package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

object ExecutionState {
    const val QUEUED = "QUEUED"
    const val DOWNLOADING = "DOWNLOADING"
    const val PAUSED = "PAUSED"
    const val LINK_EXPIRED = "LINK_EXPIRED"
    const val COMPLETED = "COMPLETED"
    const val FAILED = "FAILED"
}

@Entity(tableName = "download_tasks")
data class DownloadTask(
    @PrimaryKey val id: String,
    var currentDownloadUrl: String,    // Dynamic CDN URL (Changes on refresh)
    val originalSourcePageUrl: String, // Static fallback page URL (e.g., https://youtube.com/...)
    var executionState: String,        // DOWNLOADING, PAUSED, LINK_EXPIRED, COMPLETED, FAILED
    val downloadedBytes: Long,
    val totalBytes: Long,
    val fileMimeType: String,          // e.g., video/mp4, audio/mp4 (For manual verification)
    val fileName: String = "media_download.mp4",
    val downloadSpeedBps: Long = 0L,
    val etaSeconds: Long = 0L,
    val lastErrorCode: Int? = null,    // 403, 410, 401
    val lastErrorMessage: String? = null,
    val autoRenewEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val tokenExpiryTimestamp: Long? = null,
    val localFilePath: String? = null,
    val renewalAttempts: Int = 0
) {
    val progressPercent: Float
        get() = if (totalBytes > 0) {
            (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val isExpired: Boolean
        get() = executionState == ExecutionState.LINK_EXPIRED
}

@Entity(tableName = "renewal_logs")
data class RenewalLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: String,
    val taskTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // "403_DETECTED", "EXTRACTOR_TRIGGERED", "TOKEN_REFRESHED", "MANUAL_OVERRIDE", "RESUMED_OFFSET"
    val description: String,
    val isSuccess: Boolean = true
)
