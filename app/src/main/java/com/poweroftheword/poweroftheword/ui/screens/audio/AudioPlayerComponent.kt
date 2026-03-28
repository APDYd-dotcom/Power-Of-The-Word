package com.poweroftheword.poweroftheword.ui.screens.audio

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun AudioPlayerComponent(
    audioUrl: String,
    title: String,
    desc: String
) {
    val mediaPlayer = remember { MediaPlayer() }

    var isPrepared by remember { mutableStateOf(false) }
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    var duration by remember { mutableFloatStateOf(0f) }
    var position by remember { mutableFloatStateOf(0f) }

    // 🔥 Load from URL
    LaunchedEffect(audioUrl) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(audioUrl)

            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                duration = it.duration.toFloat()
            }

            mediaPlayer.prepareAsync()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔥 Update progress
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            if (mediaPlayer.isPlaying) {
                position = mediaPlayer.currentPosition.toFloat()
            }
            delay(500)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Button(
                onClick = {
                    if (!isPrepared) return@Button

                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer.start()
                        isPlaying = true
                    }
                }
            ) {
                Text(if (isPlaying) "Pause" else "Play")
            }

            Slider(
                value = position,
                onValueChange = {
                    position = it
                    mediaPlayer.seekTo(it.toInt())
                },
                valueRange = 0f..duration
            )

            Text(title, fontWeight = FontWeight.Bold)
            Text(desc)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}