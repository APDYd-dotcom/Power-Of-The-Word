package com.poweroftheword.poweroftheword.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VideoLikeEntity::class, AudioLikeEntity::class, FeedLikeEntity::class, VideoViewEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoLikeDao(): VideoLikeDao
    abstract fun audioLikeDao(): AudioLikeDao
    abstract fun feedLikeDao(): FeedLikeDao
    abstract fun videoViewDao(): VideoViewDao
}
