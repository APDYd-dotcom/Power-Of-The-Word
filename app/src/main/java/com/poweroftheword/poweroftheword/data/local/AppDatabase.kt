package com.poweroftheword.poweroftheword.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VideoLikeEntity::class, AudioLikeEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoLikeDao(): VideoLikeDao
    abstract fun audioLikeDao(): AudioLikeDao
}
