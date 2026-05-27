package com.poweroftheword.poweroftheword.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.poweroftheword.poweroftheword.data.local.VideoViewDao
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.util.DeviceUtils

class VideoViewSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: ChurchRepository,
    private val dao: VideoViewDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pendingViews = dao.getPendingViews()
        if (pendingViews.isEmpty()) return Result.success()

        val deviceId = DeviceUtils.getDeviceId(applicationContext)

        return try {
            pendingViews.forEach { entity ->
                repository.recordVideoView(entity.videoId, deviceId)
                dao.insertOrUpdate(entity.copy(isPending = false))
                Log.d("VideoViewSyncWorker", "Synced view for video ${entity.videoId}")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("VideoViewSyncWorker", "Error syncing video views", e)
            Result.retry()
        }
    }
}
