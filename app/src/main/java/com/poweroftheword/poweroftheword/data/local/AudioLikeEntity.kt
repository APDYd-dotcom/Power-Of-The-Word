package com.poweroftheword.poweroftheword.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_likes")
data class AudioLikeEntity(
    @PrimaryKey val audioId: String,
    val isLiked: Boolean,
    val isPending: Boolean
)
