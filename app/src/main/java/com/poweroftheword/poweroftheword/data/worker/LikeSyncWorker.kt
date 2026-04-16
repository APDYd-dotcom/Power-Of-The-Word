package com.poweroftheword.poweroftheword.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.poweroftheword.poweroftheword.data.local.VideoLikeDao
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LikeSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ChurchRepository,
    private val dao: VideoLikeDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pendingLikes = dao.getPendingLikes()
        if (pendingLikes.isEmpty()) return Result.success()

        val deviceId = DeviceUtils.getDeviceId(applicationContext)

        return try {
            pendingLikes.forEach { entity ->
                val action = if (entity.isLiked) "like" else "unlike"
                
                // Call existing interactions API
                val response = repository.interactions(
                    deviceId = deviceId,
                    videoId = entity.videoId,
                    action = action
                )

                if (response.success) {
                    // Mark as synced
                    dao.insertOrUpdate(entity.copy(isPending = false))
                    Log.d("LikeSyncWorker", "Synced ${entity.videoId} as $action")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("LikeSyncWorker", "Error syncing likes", e)
            Result.retry()
        }
    }
}
