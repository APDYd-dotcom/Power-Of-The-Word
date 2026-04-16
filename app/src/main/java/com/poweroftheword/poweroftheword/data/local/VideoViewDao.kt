package com.poweroftheword.poweroftheword.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoViewDao {
    @Query("SELECT * FROM video_views WHERE videoId = :videoId")
    suspend fun getVideoView(videoId: String): VideoViewEntity?

    @Query("SELECT * FROM video_views WHERE videoId = :videoId")
    fun getVideoViewFlow(videoId: String): Flow<VideoViewEntity?>

    @Query("SELECT * FROM video_views")
    fun getAllViewsFlow(): Flow<List<VideoViewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(view: VideoViewEntity)

    @Query("SELECT * FROM video_views WHERE isPending = 1")
    suspend fun getPendingViews(): List<VideoViewEntity>
}
