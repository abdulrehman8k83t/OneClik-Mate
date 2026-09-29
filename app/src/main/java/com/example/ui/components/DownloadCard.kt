package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadTask
import com.example.data.model.ExecutionState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.DeepSlateGrey
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.OLEDBlack
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TealBlur
import java.util.Locale

@Composable
fun DownloadCard(
    task: DownloadTask,
    isAutoRenewing: Boolean,
    onTogglePlayPause: () -> Unit,
    onManualOverride: () -> Unit,
    onSimulate403: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedDetails by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val isExpired = task.executionState == ExecutionState.LINK_EXPIRED
    val isDownloading = task.executionState == ExecutionState.DOWNLOADING
    val isCompleted = task.executionState == ExecutionState.COMPLETED
    val isPaused = task.executionState == ExecutionState.PAUSED

    val cardBorderColor = when {
        isExpired -> AlertRed
        isDownloading -> TealBlur.copy(alpha = 0.6f)
        else -> SurfaceBorder
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isExpired) 1.5.dp else 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("download_card_${task.id}"),
        colors = CardDefaults.cardColors(containerColor = DeepSlateGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: File Name + MIME badge + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.fileName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighContrastWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // MIME type chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.fileMimeType,
                                fontSize = 10.sp,
                                color = MutedGrey,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Auto-Renew badge
                        if (task.autoRenewEnabled) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF14271B))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Autorenew,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "AUTO-RENEW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge
                StatusBadge(
                    state = task.executionState,
                    pulseAlpha = if (isExpired || isDownloading) pulseAlpha else 1f
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Auto-renewal in-progress alert banner
            if (isAutoRenewing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF261800))
                        .border(1.dp, NeonAmber, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = NeonAmber,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ExtractorEngine: Scraping Fresh CDN Token...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber
                            )
                            Text(
                                text = "Resuming from exact byte offset (${formatBytes(task.downloadedBytes)})",
                                fontSize = 10.sp,
                                color = HighContrastWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Expired Alert Warning Box if LINK_EXPIRED
            if (isExpired && !isAutoRenewing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2A0D0B))
                        .border(1.dp, AlertRed.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Expired warning",
                            tint = AlertRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "STREAM TOKEN EXPIRED (HTTP ${task.lastErrorCode ?: 403})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                            Text(
                                text = task.lastErrorMessage ?: "CDN signature invalidated. Renew link to resume from offset.",
                                fontSize = 10.sp,
                                color = HighContrastWhite.copy(alpha = 0.85f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Progress Bar (Teal Blur for normal, Alert Red for expired)
            val progressColor = when {
                isCompleted -> SuccessGreen
                isExpired -> AlertRed
                else -> TealBlur
            }

            LinearProgressIndicator(
                progress = { task.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = SurfaceElevated
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Download Stats Row: Bytes + Speed & ETA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bytes downloaded
                Text(
                    text = "${formatBytes(task.downloadedBytes)} / ${formatBytes(task.totalBytes)} (${(task.progressPercent * 100).toInt()}%)",
                    fontSize = 11.sp,
                    color = HighContrastWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )

                // Speed / ETA / State
                val secondaryStatText = when {
                    isCompleted -> "Verified & Ready"
                    isExpired -> "Paused at byte offset"
                    isDownloading -> "${formatBytes(task.downloadSpeedBps)}/s • ETA: ${task.etaSeconds}s"
                    isPaused -> "Paused"
                    else -> "Queued"
                }

                Text(
                    text = secondaryStatText,
                    fontSize = 11.sp,
                    color = if (isExpired) AlertRed else MutedGrey,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable details (Source Page & Dynamic CDN URL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedDetails = !expandedDetails }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = MutedGrey,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (expandedDetails) "Hide Stream Metadata" else "View Stream URL & Source",
                        fontSize = 11.sp,
                        color = MutedGrey
                    )
                }
                Text(
                    text = if (expandedDetails) "▲" else "▼",
                    fontSize = 10.sp,
                    color = MutedGrey
                )
            }

            AnimatedVisibility(visible = expandedDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevated)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Original Fallback Page:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                    Text(
                        text = task.originalSourcePageUrl,
                        fontSize = 10.sp,
                        color = HighContrastWhite,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Active Dynamic CDN URL (Room DB):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealBlur
                    )
                    Text(
                        text = task.currentDownloadUrl,
                        fontSize = 10.sp,
                        color = MutedGrey,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause toggle
                if (!isCompleted) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .testTag("toggle_play_pause_${task.id}")
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                    ) {
                        Icon(
                            imageVector = if (isDownloading) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isDownloading) "Pause" else "Resume",
                            tint = if (isDownloading) TealBlur else NeonAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF14271B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // "Renew Link / Update URL" button (Manual Override / Renewal)
                Button(
                    onClick = onManualOverride,
                    modifier = Modifier
                        .testTag("renew_link_button_${task.id}")
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExpired) AlertRed else Color(0xFF261800),
                        contentColor = if (isExpired) OLEDBlack else NeonAmber
                    ),
                    border = if (!isExpired) androidx.compose.foundation.BorderStroke(1.dp, NeonAmber) else null
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Renew Link",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isExpired) "Renew Link / Update URL" else "Update URL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Simulate 403 test button (only during download)
                if (isDownloading) {
                    IconButton(
                        onClick = onSimulate403,
                        modifier = Modifier
                            .testTag("simulate_403_button_${task.id}")
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A0D0B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Simulate 403 Expiry",
                            tint = AlertRed,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .testTag("delete_button_${task.id}")
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = MutedGrey,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    state: String,
    pulseAlpha: Float
) {
    val (bgColor, textColor, label) = when (state) {
        ExecutionState.DOWNLOADING -> Triple(
            Color(0xFF00274D),
            TealBlur,
            "DOWNLOADING"
        )
        ExecutionState.LINK_EXPIRED -> Triple(
            AlertRed,
            OLEDBlack,
            "LINK EXPIRED"
        )
        ExecutionState.PAUSED -> Triple(
            SurfaceElevated,
            MutedGrey,
            "PAUSED"
        )
        ExecutionState.COMPLETED -> Triple(
            Color(0xFF14271B),
            SuccessGreen,
            "COMPLETED"
        )
        else -> Triple(
            SurfaceElevated,
            MutedGrey,
            state
        )
    }

    Box(
        modifier = Modifier
            .alpha(if (state == ExecutionState.LINK_EXPIRED || state == ExecutionState.DOWNLOADING) pulseAlpha else 1f)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
        mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
        kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
        else -> "$bytes B"
    }
}
