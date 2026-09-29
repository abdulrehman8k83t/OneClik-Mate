package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FilterTab
import com.example.ui.theme.AlertRed
import com.example.ui.theme.DeepSlateGrey
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.OLEDBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TealBlur

@Composable
fun EngineHeader(
    selectedFilter: FilterTab,
    onFilterSelected: (FilterTab) -> Unit,
    activeCount: Int,
    expiredCount: Int,
    onOpenLogs: () -> Unit,
    onNewDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(OLEDBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Branding Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Vidmate Logo",
                            tint = OLEDBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REHMAN",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = HighContrastWhite,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF261800))
                            .border(1.dp, NeonAmber, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HD / 4K PRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ALL-IN-ONE 4K/HD • OLED PROTOCOL",
                        fontSize = 11.sp,
                        color = MutedGrey,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Audit Logs Button
                IconButton(
                    onClick = onOpenLogs,
                    modifier = Modifier
                        .testTag("logs_button")
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Renewal Audit Logs",
                        tint = TealBlur,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // New Download FAB Mini
                IconButton(
                    onClick = onNewDownload,
                    modifier = Modifier
                        .testTag("new_download_header_button")
                        .clip(CircleShape)
                        .background(NeonAmber)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Download Task",
                        tint = OLEDBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTab.values().forEach { tab ->
                val isSelected = selectedFilter == tab
                val chipLabel = when (tab) {
                    FilterTab.ALL -> "All Tasks"
                    FilterTab.ACTIVE -> "Active ($activeCount)"
                    FilterTab.EXPIRED -> "Link Expired ($expiredCount)"
                    FilterTab.COMPLETED -> "Completed"
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelected(tab) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (tab == FilterTab.EXPIRED && expiredCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(AlertRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = chipLabel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) {
                                    if (tab == FilterTab.EXPIRED) AlertRed else NeonAmber
                                } else MutedGrey
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DeepSlateGrey,
                        selectedContainerColor = if (tab == FilterTab.EXPIRED) Color(0xFF2A0D0B) else Color(0xFF221600),
                        labelColor = MutedGrey,
                        selectedLabelColor = if (tab == FilterTab.EXPIRED) AlertRed else NeonAmber
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = SurfaceBorder,
                        selectedBorderColor = if (tab == FilterTab.EXPIRED) AlertRed else NeonAmber,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("filter_chip_${tab.name.lowercase()}")
                )
            }
        }
    }
}
