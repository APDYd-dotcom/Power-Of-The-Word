package com.poweroftheword.poweroftheword.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_likes")
data class VideoLikeEntity(
    @PrimaryKey val videoId: String,
    val isLiked: Boolean,
    val isPending: Boolean
)
