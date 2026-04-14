package com.poweroftheword.poweroftheword.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoLikeDao {
    @Query("SELECT * FROM video_likes WHERE videoId = :videoId")
    fun getLikeFlow(videoId: String): Flow<VideoLikeEntity?>

    @Query("SELECT * FROM video_likes WHERE videoId = :videoId")
    suspend fun getLikeSync(videoId: String): VideoLikeEntity?

    @Query("SELECT * FROM video_likes")
    fun getAllLikesFlow(): Flow<List<VideoLikeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(like: VideoLikeEntity)

    @Query("SELECT * FROM video_likes WHERE isPending = 1")
    suspend fun getPendingLikes(): List<VideoLikeEntity>
}
