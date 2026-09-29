package com.example.ui.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.DeepSlateGrey
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.OLEDBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TealBlur

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDownloadDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, sourceUrl: String, directUrl: String, mimeType: String, totalBytesMb: Long, autoRenew: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var sourceUrl by remember { mutableStateOf("") }
    var directUrl by remember { mutableStateOf("") }
    var mimeType by remember { mutableStateOf("video/mp4") }
    var sizeMb by remember { mutableStateOf(140L) }
    var autoRenew by remember { mutableStateOf(true) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepSlateGrey,
        dragHandle = null,
        modifier = modifier.testTag("new_download_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NeonAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = OLEDBlack,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "New Download Task",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighContrastWhite
                        )
                        Text(
                            text = "Vidmate Advanced Engine v2.0",
                            fontSize = 11.sp,
                            color = MutedGrey
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MutedGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Presets
            Text(
                text = "QUICK TEST PRESETS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeonAmber,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(
                    label = "Big Buck Bunny (1080p)",
                    onClick = {
                        title = "Big_Buck_Bunny_1080p.mp4"
                        sourceUrl = "https://peach.blender.org/download/"
                        directUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                        mimeType = "video/mp4"
                        sizeMb = 158L
                    }
                )
                PresetChip(
                    label = "Simulate 403 Expiry Task",
                    onClick = {
                        title = "Expiring_CDN_Stream_Test.mp4"
                        sourceUrl = "https://youtube.com/watch?v=dQw4w9WgXcQ"
                        directUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4?simulate_403=true"
                        mimeType = "video/mp4"
                        sizeMb = 210L
                    }
                )
                PresetChip(
                    label = "Synthwave Audio Track",
                    onClick = {
                        title = "Midnight_Drive_Synthwave.mp3"
                        sourceUrl = "https://soundcloud.com/retrowave-special/midnight-drive"
                        directUrl = "https://cdn.vidmate-engine.stream/audio/synthwave.mp3"
                        mimeType = "audio/mp3"
                        sizeMb = 35L
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // File Name Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("File Name") },
                placeholder = { Text("e.g. Inception_Trailer_4K.mp4") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_download_title_input"),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Original Source Page URL
            OutlinedTextField(
                value = sourceUrl,
                onValueChange = { sourceUrl = it },
                label = { Text("Original Source Page URL (Fallback)") },
                placeholder = { Text("https://youtube.com/watch?v=... or https://vimeo.com/...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_download_source_input"),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Direct CDN URL (Optional)
            OutlinedTextField(
                value = directUrl,
                onValueChange = { directUrl = it },
                label = { Text("Direct CDN Stream URL (Optional)") },
                placeholder = { Text("Leave blank to auto-generate signed stream link") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_download_direct_input"),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // MIME Type Selector
            Text(
                text = "MIME TYPE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MutedGrey
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("video/mp4", "audio/mp4", "video/webm", "audio/mp3").forEach { type ->
                    val isSelected = mimeType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF261800) else SurfaceElevated)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonAmber else SurfaceBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { mimeType = type }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = type,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonAmber else HighContrastWhite,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Auto-Renew Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-Renew Anti-Expiration",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighContrastWhite
                        )
                        Text(
                            text = "Automatically scrape new CDN token when HTTP 403/410 is detected",
                            fontSize = 10.sp,
                            color = MutedGrey
                        )
                    }
                    Switch(
                        checked = autoRenew,
                        onCheckedChange = { autoRenew = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OLEDBlack,
                            checkedTrackColor = NeonAmber,
                            uncheckedThumbColor = MutedGrey,
                            uncheckedTrackColor = OLEDBlack
                        ),
                        modifier = Modifier.testTag("auto_renew_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Start Download Button
            Button(
                onClick = {
                    onAdd(
                        title.ifBlank { "Sample_Stream_Download.mp4" },
                        sourceUrl.ifBlank { "https://media-stream.hub/video/1080" },
                        directUrl,
                        mimeType,
                        sizeMb,
                        autoRenew
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_download_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonAmber,
                    contentColor = OLEDBlack
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Queue Download Task",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceElevated)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = NeonAmber,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = HighContrastWhite
            )
        }
    }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NeonAmber,
    unfocusedBorderColor = SurfaceBorder,
    focusedLabelColor = NeonAmber,
    unfocusedLabelColor = MutedGrey,
    focusedTextColor = HighContrastWhite,
    unfocusedTextColor = HighContrastWhite,
    focusedContainerColor = OLEDBlack,
    unfocusedContainerColor = OLEDBlack
)
