package com.poweroftheword.poweroftheword.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VideoLikeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoLikeDao(): VideoLikeDao
}
