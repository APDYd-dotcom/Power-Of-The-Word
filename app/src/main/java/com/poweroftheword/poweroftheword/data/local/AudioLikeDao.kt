package com.poweroftheword.poweroftheword.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioLikeDao {
    @Query("SELECT * FROM audio_likes WHERE audioId = :audioId")
    fun getLikeFlow(audioId: String): Flow<AudioLikeEntity?>

    @Query("SELECT * FROM audio_likes WHERE audioId = :audioId")
    suspend fun getLikeSync(audioId: String): AudioLikeEntity?

    @Query("SELECT * FROM audio_likes")
    fun getAllLikesFlow(): Flow<List<AudioLikeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(like: AudioLikeEntity)

    @Query("SELECT * FROM audio_likes WHERE isPending = 1")
    suspend fun getPendingLikes(): List<AudioLikeEntity>
}
