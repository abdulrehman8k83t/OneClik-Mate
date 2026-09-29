package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bookmark
import com.example.ui.theme.DeepSlateGrey
import com.example.ui.theme.HighContrastWhite
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.OLEDBlack
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TealBlur

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteViewerSheet(
    bookmark: Bookmark,
    onGrabLink: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val accentColor = try {
        Color(android.graphics.Color.parseColor(bookmark.accentColorHex))
    } catch (e: Exception) {
        NeonAmber
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DeepSlateGrey,
        dragHandle = null,
        modifier = modifier.testTag("site_viewer_bottom_sheet")
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
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(1.dp, accentColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bookmark.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighContrastWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = bookmark.url,
                            fontSize = 11.sp,
                            color = TealBlur,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MutedGrey)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row: Open in Browser & Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handled
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealBlur,
                        contentColor = OLEDBlack
                    )
                ) {
                    Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Open Website", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(bookmark.url))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HighContrastWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Copy Link", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fast Sniffer & Grabber Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF261800))
                    .border(1.dp, NeonAmber, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.TravelExplore, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1-Click Video Sniffer & Grabber",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rehman automatically parses video streams from this website. Tap Grab to load into the 4K/HD downloader.",
                        fontSize = 11.sp,
                        color = HighContrastWhite.copy(alpha = 0.85f),
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onGrabLink(bookmark.url)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = OLEDBlack)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Grab Video from ${bookmark.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sample direct streams available on this portal
            Text(
                text = "FEATURED MOVIES ON THIS PORTAL (INSTANT HD/4K)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MutedGrey,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val portalVideos = getSampleMoviesForPortal(bookmark.title, bookmark.url)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                portalVideos.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighContrastWhite
                                )
                                Text(
                                    text = "${item.quality} • ${item.size} • ${item.format}",
                                    fontSize = 10.sp,
                                    color = TealBlur,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = {
                                    onGrabLink(item.streamUrl)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14271B), contentColor = SuccessGreen),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                            ) {
                                Text(text = "Download", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class PortalMediaItem(
    val title: String,
    val quality: String,
    val size: String,
    val format: String,
    val streamUrl: String
)

private fun getSampleMoviesForPortal(portalName: String, url: String): List<PortalMediaItem> {
    return when {
        portalName.contains("WorldFree4u", ignoreCase = true) -> listOf(
            PortalMediaItem("Blockbuster Action (WorldFree4u 1080p)", "1080p Full HD", "1.8 GB", "MKV/MP4", "$url/download/action-1080p.mp4"),
            PortalMediaItem("Hindi Dubbed Cinema (720p Dual Audio)", "720p HD", "920 MB", "MP4", "$url/download/hindi-720p.mp4")
        )
        portalName.contains("BollyZilla", ignoreCase = true) -> listOf(
            PortalMediaItem("BollyZilla Hindi HD Remastered", "1080p HD", "1.4 GB", "MP4", "$url/movies/hindi-hd.mp4"),
            PortalMediaItem("Latest Bollywood Cinema Print", "720p HD", "850 MB", "MP4", "$url/movies/bolly-720.mp4")
        )
        portalName.contains("Punjabi", ignoreCase = true) || portalName.contains("MoviesSpace", ignoreCase = true) -> listOf(
            PortalMediaItem("MoviesSpace Punjabi Superhit 2025", "1080p Full HD", "2.1 GB", "MP4", "$url/punjabi-superhit-1080p.mp4"),
            PortalMediaItem("Punjabi Comedy Cinema (720p)", "720p HD", "780 MB", "MP4", "$url/punjabi-comedy-720p.mp4")
        )
        portalName.contains("FilmVup", ignoreCase = true) -> listOf(
            PortalMediaItem("FilmVup Web Series Season Complete", "1080p HD", "1.9 GB", "MP4", "$url/series/season-complete.mp4"),
            PortalMediaItem("FilmVup Hollywood Hindi Dub", "720p HD", "890 MB", "MP4", "$url/movies/hollywood-hindi.mp4")
        )
        portalName.contains("Watch", ignoreCase = true) -> listOf(
            PortalMediaItem("WatchMovies 2025 Indian Cinema Print", "1080p Full HD", "2.2 GB", "MP4", "$url/movies/2025-latest.mp4"),
            PortalMediaItem("Classic Indian Movie Remastered (720p)", "720p HD", "950 MB", "MP4", "$url/movies/classic-remaster.mp4")
        )
        else -> listOf(
            PortalMediaItem("HD Stream Video (Original Print)", "1080p Full HD", "1.1 GB", "MP4", "$url/video.mp4"),
            PortalMediaItem("Fast Stream (Compressed 720p)", "720p HD", "450 MB", "MP4", "$url/stream.mp4")
        )
    }
}
