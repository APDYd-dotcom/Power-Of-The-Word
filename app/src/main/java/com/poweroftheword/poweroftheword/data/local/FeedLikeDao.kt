package com.poweroftheword.poweroftheword.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedLikeDao {
    @Query("SELECT * FROM feed_likes WHERE feedId = :feedId")
    fun getLikeFlow(feedId: String): Flow<FeedLikeEntity?>

    @Query("SELECT * FROM feed_likes WHERE feedId = :feedId")
    suspend fun getLikeSync(feedId: String): FeedLikeEntity?

    @Query("SELECT * FROM feed_likes")
    fun getAllLikesFlow(): Flow<List<FeedLikeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(like: FeedLikeEntity)

    @Query("SELECT * FROM feed_likes WHERE isPending = 1")
    suspend fun getPendingLikes(): List<FeedLikeEntity>
}
