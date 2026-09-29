package com.example.engine

import android.net.Uri

data class MediaQualityOption(
    val resolutionLabel: String,
    val badge: String,
    val sizeFormatted: String,
    val sizeBytes: Long,
    val mimeType: String,
    val isAudioOnly: Boolean = false,
    val isRecommended: Boolean = false,
    val qualityTag: String = "HD"
)

data class DetectedPlatformInfo(
    val platformKey: String,
    val platformName: String,
    val brandColorHex: String,
    val iconType: String,
    val estimatedTitle: String,
    val availableQualities: List<MediaQualityOption>,
    val directCdnStreamUrl: String
)

object PlatformDetector {

    fun detect(url: String): DetectedPlatformInfo {
        val cleanUrl = url.trim()
        val lower = cleanUrl.lowercase()

        return when {
            lower.contains("youtube.com") || lower.contains("youtu.be") -> {
                val videoId = extractYouTubeId(cleanUrl) ?: "YT_${System.currentTimeMillis() % 10000}"
                DetectedPlatformInfo(
                    platformKey = "youtube",
                    platformName = "YouTube (HD/4K)",
                    brandColorHex = "#FF0000",
                    iconType = "youtube",
                    estimatedTitle = "YouTube_Video_${videoId}.mp4",
                    availableQualities = getStandardVideoQualities("YouTube_Video_${videoId}"),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4?source=youtube&vid=$videoId"
                )
            }

            lower.contains("instagram.com") || lower.contains("instagr.am") -> {
                DetectedPlatformInfo(
                    platformKey = "instagram",
                    platformName = "Instagram Reels & Post",
                    brandColorHex = "#E1306C",
                    iconType = "instagram",
                    estimatedTitle = "Instagram_Reels_${System.currentTimeMillis() % 100000}.mp4",
                    availableQualities = listOf(
                        MediaQualityOption("1080p Full HD (Original)", "1080p", "48 MB", 48_000_000L, "video/mp4", isRecommended = true),
                        MediaQualityOption("720p HD", "720p", "24 MB", 24_000_000L, "video/mp4"),
                        MediaQualityOption("Audio Only (HQ MP3)", "MP3", "4.2 MB", 4_200_000L, "audio/mp3", isAudioOnly = true)
                    ),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4?source=instagram"
                )
            }

            lower.contains("tiktok.com") -> {
                DetectedPlatformInfo(
                    platformKey = "tiktok",
                    platformName = "TikTok (No Watermark)",
                    brandColorHex = "#00F2FE",
                    iconType = "tiktok",
                    estimatedTitle = "TikTok_NoWatermark_${System.currentTimeMillis() % 100000}.mp4",
                    availableQualities = listOf(
                        MediaQualityOption("1080p HD (Clean / No Watermark)", "1080p", "36 MB", 36_000_000L, "video/mp4", isRecommended = true),
                        MediaQualityOption("720p SD", "720p", "18 MB", 18_000_000L, "video/mp4"),
                        MediaQualityOption("Original Sound (MP3)", "MP3", "3.1 MB", 3_100_000L, "audio/mp3", isAudioOnly = true)
                    ),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4?source=tiktok"
                )
            }

            lower.contains("facebook.com") || lower.contains("fb.watch") || lower.contains("fb.com") -> {
                DetectedPlatformInfo(
                    platformKey = "facebook",
                    platformName = "Facebook Video",
                    brandColorHex = "#1877F2",
                    iconType = "facebook",
                    estimatedTitle = "Facebook_Watch_${System.currentTimeMillis() % 100000}.mp4",
                    availableQualities = listOf(
                        MediaQualityOption("1080p Full HD", "1080p", "95 MB", 95_000_000L, "video/mp4", isRecommended = true),
                        MediaQualityOption("720p HD", "720p", "45 MB", 45_000_000L, "video/mp4"),
                        MediaQualityOption("480p SD", "480p", "22 MB", 22_000_000L, "video/mp4"),
                        MediaQualityOption("Audio Track (MP3)", "MP3", "6.5 MB", 6_500_000L, "audio/mp3", isAudioOnly = true)
                    ),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4?source=facebook"
                )
            }

            lower.contains("twitter.com") || lower.contains("x.com") -> {
                DetectedPlatformInfo(
                    platformKey = "twitter",
                    platformName = "X / Twitter Media",
                    brandColorHex = "#E5E5EA",
                    iconType = "twitter",
                    estimatedTitle = "Twitter_Clip_${System.currentTimeMillis() % 100000}.mp4",
                    availableQualities = listOf(
                        MediaQualityOption("1080p Full HD", "1080p", "52 MB", 52_000_000L, "video/mp4", isRecommended = true),
                        MediaQualityOption("720p HD", "720p", "28 MB", 28_000_000L, "video/mp4"),
                        MediaQualityOption("Audio Track (MP3)", "MP3", "4.8 MB", 4_800_000L, "audio/mp3", isAudioOnly = true)
                    ),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4?source=x"
                )
            }

            lower.contains("worldfree4u") -> {
                DetectedPlatformInfo(
                    platformKey = "worldfree4u",
                    platformName = "WorldFree4u Movie Portal",
                    brandColorHex = "#FFB703",
                    iconType = "movie",
                    estimatedTitle = "WorldFree4u_Movie_Remastered.mp4",
                    availableQualities = getMovieQualities("WorldFree4u"),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4?portal=worldfree4u"
                )
            }

            lower.contains("bollyzilla") -> {
                DetectedPlatformInfo(
                    platformKey = "bollyzilla",
                    platformName = "BollyZilla Movie Portal",
                    brandColorHex = "#FB8500",
                    iconType = "movie",
                    estimatedTitle = "BollyZilla_HD_Cinema.mp4",
                    availableQualities = getMovieQualities("BollyZilla"),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4?portal=bollyzilla"
                )
            }

            lower.contains("moviesspace") -> {
                DetectedPlatformInfo(
                    platformKey = "moviesspace",
                    platformName = "MoviesSpace Punjabi Cinema",
                    brandColorHex = "#06D6A0",
                    iconType = "movie",
                    estimatedTitle = "MoviesSpace_Punjabi_Blockbuster.mp4",
                    availableQualities = getMovieQualities("MoviesSpace_Punjabi"),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4?portal=moviesspace"
                )
            }

            lower.contains("filmvup") -> {
                DetectedPlatformInfo(
                    platformKey = "filmvup",
                    platformName = "FilmVup Streaming Portal",
                    brandColorHex = "#8338EC",
                    iconType = "movie",
                    estimatedTitle = "FilmVup_WebSeries_Full.mp4",
                    availableQualities = getMovieQualities("FilmVup"),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4?portal=filmvup"
                )
            }

            lower.contains("watch-movies") -> {
                DetectedPlatformInfo(
                    platformKey = "watchmovies",
                    platformName = "Watch Movies PK",
                    brandColorHex = "#E63946",
                    iconType = "movie",
                    estimatedTitle = "WatchMovies_Latest_2025.mp4",
                    availableQualities = getMovieQualities("WatchMovies_PK"),
                    directCdnStreamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4?portal=watchmovies"
                )
            }

            else -> {
                // Direct generic web link
                val fileName = cleanUrl.substringAfterLast("/").substringBefore("?").ifBlank { "Media_Stream_Download.mp4" }
                DetectedPlatformInfo(
                    platformKey = "direct",
                    platformName = "Direct Web Stream",
                    brandColorHex = "#0A84FF",
                    iconType = "globe",
                    estimatedTitle = if (fileName.contains(".")) fileName else "$fileName.mp4",
                    availableQualities = listOf(
                        MediaQualityOption("Original Quality (Direct Stream)", "ORIGINAL", "180 MB", 180_000_000L, "video/mp4", isRecommended = true),
                        MediaQualityOption("1080p Re-encoded", "1080p", "120 MB", 120_000_000L, "video/mp4"),
                        MediaQualityOption("720p Compressed", "720p", "65 MB", 65_000_000L, "video/mp4"),
                        MediaQualityOption("Extract Audio (MP3)", "MP3", "12 MB", 12_000_000L, "audio/mp3", isAudioOnly = true)
                    ),
                    directCdnStreamUrl = cleanUrl
                )
            }
        }
    }

    private fun getStandardVideoQualities(prefix: String): List<MediaQualityOption> = listOf(
        MediaQualityOption("4K Ultra HD (2160p 60fps)", "4K", "1.42 GB", 1_420_000_000L, "video/mp4", qualityTag = "4K"),
        MediaQualityOption("1080p Full HD (60fps)", "1080p", "415 MB", 415_000_000L, "video/mp4", isRecommended = true, qualityTag = "1080p"),
        MediaQualityOption("720p HD", "720p", "185 MB", 185_000_000L, "video/mp4", qualityTag = "720p"),
        MediaQualityOption("480p SD (Data Saver)", "480p", "82 MB", 82_000_000L, "video/mp4", qualityTag = "480p"),
        MediaQualityOption("Audio Only (HQ MP3 320kbps)", "MP3", "14.2 MB", 14_200_000L, "audio/mp3", isAudioOnly = true, qualityTag = "Audio"),
        MediaQualityOption("Audio Only (M4A 256kbps)", "M4A", "10.5 MB", 10_500_000L, "audio/mp4", isAudioOnly = true, qualityTag = "Audio")
    )

    private fun getMovieQualities(portal: String): List<MediaQualityOption> = listOf(
        MediaQualityOption("1080p Full HD (Cinema Print)", "1080p", "2.10 GB", 2_100_000_000L, "video/mp4", isRecommended = true, qualityTag = "1080p"),
        MediaQualityOption("720p HD (HEVC x265)", "720p", "950 MB", 950_000_000L, "video/mp4", qualityTag = "720p"),
        MediaQualityOption("480p Mobile Print", "480p", "420 MB", 420_000_000L, "video/mp4", qualityTag = "480p"),
        MediaQualityOption("Original Audio Track (MP3)", "MP3", "45 MB", 45_000_000L, "audio/mp3", isAudioOnly = true, qualityTag = "Audio")
    )

    private fun extractYouTubeId(url: String): String? {
        return try {
            val uri = Uri.parse(url)
            if (url.contains("youtu.be/")) {
                url.substringAfter("youtu.be/").substringBefore("?").substringBefore("/")
            } else {
                uri.getQueryParameter("v")
            }
        } catch (e: Exception) {
            null
        }
    }
}
