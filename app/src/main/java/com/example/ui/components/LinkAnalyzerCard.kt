package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.DetectedPlatformInfo
import com.example.engine.MediaQualityOption
import com.example.engine.PlatformDetector
import com.example.ui.theme.DeepSlateGrey
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.OLEDBlack
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TealBlur

@Composable
fun LinkAnalyzerCard(
    onDownloadQualitySelected: (title: String, sourceUrl: String, directUrl: String, mimeType: String, sizeBytes: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputUrl by remember { mutableStateOf("") }
    var detectedInfo by remember { mutableStateOf<DetectedPlatformInfo?>(null) }
    var selectedQuality by remember { mutableStateOf<MediaQualityOption?>(null) }
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepSlateGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF261800)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Smart Social & Video Downloader",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighContrastWhite
                        )
                        Text(
                            text = "Auto-detects platform & extracts 4K/HD options",
                            fontSize = 10.sp,
                            color = MutedGrey
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // URL input with Paste & Clear
            OutlinedTextField(
                value = inputUrl,
                onValueChange = {
                    inputUrl = it
                    if (it.isNotBlank()) {
                        val detected = PlatformDetector.detect(it)
                        detectedInfo = detected
                        selectedQuality = detected.availableQualities.firstOrNull { q -> q.isRecommended }
                            ?: detected.availableQualities.firstOrNull()
                    } else {
                        detectedInfo = null
                        selectedQuality = null
                    }
                },
                placeholder = {
                    Text(
                        text = "Paste link from YouTube, Instagram, TikTok, Movies...",
                        fontSize = 12.sp,
                        color = MutedGrey
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("smart_link_input"),
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (inputUrl.isNotBlank()) {
                            IconButton(onClick = {
                                inputUrl = ""
                                detectedInfo = null
                                selectedQuality = null
                            }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = MutedGrey, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            IconButton(onClick = {
                                val text = clipboardManager.getText()?.text ?: ""
                                if (text.isNotBlank()) {
                                    inputUrl = text
                                    val detected = PlatformDetector.detect(text)
                                    detectedInfo = detected
                                    selectedQuality = detected.availableQualities.firstOrNull { q -> q.isRecommended }
                                        ?: detected.availableQualities.firstOrNull()
                                }
                            }) {
                                Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste", tint = NeonAmber, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonAmber,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = HighContrastWhite,
                    unfocusedTextColor = HighContrastWhite,
                    focusedContainerColor = OLEDBlack,
                    unfocusedContainerColor = OLEDBlack
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 2
            )

            // Platform Detection Result Card
            AnimatedVisibility(visible = detectedInfo != null) {
                val info = detectedInfo ?: return@AnimatedVisibility
                val brandColor = try {
                    Color(android.graphics.Color.parseColor(info.brandColorHex))
                } catch (e: Exception) {
                    NeonAmber
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    // Platform Recognition Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(brandColor.copy(alpha = 0.15f))
                            .border(1.dp, brandColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(brandColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Detected: ${info.platformName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = brandColor
                                )
                            }
                            Text(
                                text = "Auto-Recognized",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighContrastWhite.copy(alpha = 0.7f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "SELECT DOWNLOAD RESOLUTION & FORMAT:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedGrey,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Resolutions List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        info.availableQualities.forEach { quality ->
                            val isSelected = selectedQuality == quality

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF261800) else SurfaceElevated)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) NeonAmber else SurfaceBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedQuality = quality }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Badge
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (quality.badge.contains("4K")) Color(0xFF8338EC)
                                                    else if (quality.isAudioOnly) TealBlur
                                                    else NeonAmber
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = quality.badge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = OLEDBlack
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = quality.resolutionLabel,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = HighContrastWhite
                                            )
                                            Text(
                                                text = "${quality.mimeType} • ${quality.sizeFormatted}",
                                                fontSize = 10.sp,
                                                color = MutedGrey,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = NeonAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Download Button
                    Button(
                        onClick = {
                            val quality = selectedQuality ?: info.availableQualities.first()
                            onDownloadQualitySelected(
                                info.estimatedTitle,
                                inputUrl,
                                info.directCdnStreamUrl,
                                quality.mimeType,
                                quality.sizeBytes
                            )
                            inputUrl = ""
                            detectedInfo = null
                            selectedQuality = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("start_resolution_download_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonAmber,
                            contentColor = OLEDBlack
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Download in ${selectedQuality?.badge ?: "HD"} (${selectedQuality?.sizeFormatted ?: "Fast"})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
