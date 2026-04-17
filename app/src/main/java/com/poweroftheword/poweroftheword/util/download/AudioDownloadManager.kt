package com.poweroftheword.poweroftheword.util.download

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.contentLength
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient
) {
    fun getAudioFile(audioId: Int): File {
        val directory = File(context.filesDir, "audios")
        if (!directory.exists()) directory.mkdirs()
        return File(directory, "audio_$audioId.mp3")
    }

    fun isAudioDownloaded(audioId: Int): Boolean {
        return getAudioFile(audioId).exists()
    }

    fun downloadAudio(audioId: Int, url: String): Flow<DownloadProgress> = flow {
        val file = getAudioFile(audioId)
        try {
            val response = client.get(url)
            val contentLength = response.contentLength() ?: 0L
            val channel = response.bodyAsChannel()
            
            file.outputStream().use { output ->
                var bytesRead = 0L
                val buffer = ByteArray(8 * 1024)
                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer)
                    if (read == -1) break
                    output.write(buffer, 0, read)
                    bytesRead += read
                    if (contentLength > 0) {
                        emit(DownloadProgress.Running(bytesRead.toFloat() / contentLength))
                    }
                }
            }
            emit(DownloadProgress.Success(file))
        } catch (e: Exception) {
            if (file.exists()) file.delete()
            emit(DownloadProgress.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)
}

sealed class DownloadProgress {
    data class Running(val progress: Float) : DownloadProgress()
    data class Success(val file: File) : DownloadProgress()
    data class Error(val message: String) : DownloadProgress()
}
