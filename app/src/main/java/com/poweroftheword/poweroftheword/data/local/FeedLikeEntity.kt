package com.poweroftheword.poweroftheword.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_likes")
data class FeedLikeEntity(
    @PrimaryKey val feedId: String,
    val isLiked: Boolean,
    val isPending: Boolean
)
