package com.poweroftheword.poweroftheword.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.poweroftheword.poweroftheword.data.local.FeedLikeDao
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FeedLikeSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ChurchRepository,
    private val dao: FeedLikeDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pendingLikes = dao.getPendingLikes()
        if (pendingLikes.isEmpty()) return Result.success()

        val deviceId = DeviceUtils.getDeviceId(applicationContext)

        return try {
            pendingLikes.forEach { entity ->
                val action = if (entity.isLiked) "like" else "unlike"
                
                val response = repository.interactions(
                    deviceId = deviceId,
                    feedId = entity.feedId,
                    action = action
                )

                if (response.success.fanta) {
                    dao.insertOrUpdate(entity.copy(isPending = false))
                    Log.d("FeedLikeSyncWorker", "Synced ${entity.feedId} as $action")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("FeedLikeSyncWorker", "Error syncing feed likes", e)
            Result.retry()
        }
    }
}
