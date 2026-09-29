package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey val id: String,
    val title: String,
    val url: String,
    val category: String = "Movies", // "Movies", "Social", "Streaming", "Custom"
    val iconType: String = "globe",  // "movie", "globe", "youtube", "instagram", "tiktok", "facebook", "twitter"
    val accentColorHex: String = "#FF9F0A",
    val isFavorite: Boolean = true,
    val visitCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
