package com.poweroftheword.poweroftheword.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.poweroftheword.poweroftheword.data.local.AudioLikeDao
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AudioLikeSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ChurchRepository,
    private val dao: AudioLikeDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pendingLikes = dao.getPendingLikes()
        if (pendingLikes.isEmpty()) return Result.success()

        val deviceId = DeviceUtils.getDeviceId(applicationContext)

        return try {
            pendingLikes.forEach { entity ->
                val action = if (entity.isLiked) "like" else "unlike"
                
                // Call existing interactions API for audio
                val response = repository.interactions(
                    deviceId = deviceId,
                    audioId = entity.audioId,
                    action = action
                )

                if (response.success) {
                    // Mark as synced
                    dao.insertOrUpdate(entity.copy(isPending = false))
                    Log.d("AudioLikeSyncWorker", "Synced ${entity.audioId} as $action")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("AudioLikeSyncWorker", "Error syncing audio likes", e)
            Result.retry()
        }
    }
}
