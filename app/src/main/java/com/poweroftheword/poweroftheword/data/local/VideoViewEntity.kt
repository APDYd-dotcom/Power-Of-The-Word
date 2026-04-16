package com.poweroftheword.poweroftheword.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_views")
data class VideoViewEntity(
    @PrimaryKey val videoId: String,
    val viewed: Boolean = true,
    val isPending: Boolean = true
)
