package com.poweroftheword.poweroftheword.ui.screens.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poweroftheword.poweroftheword.domain.model.AudioItem
import com.poweroftheword.poweroftheword.util.formatTime
import com.poweroftheword.poweroftheword.util.formatDate
import com.poweroftheword.poweroftheword.util.download.DownloadProgress
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerComponent(
    context: Context,
    audio: AudioItem,
    viewModel: AudioListViewModel,
    isLiked: Boolean,
) {
    val mediaPlayer = remember { MediaPlayer() }
    val downloadManager = viewModel.downloadManager

    var isPrepared by remember { mutableStateOf(false) }
    val (isPlaying, setIsPlaying) = rememberSaveable { mutableStateOf(false) }

    var duration by remember { mutableFloatStateOf(0f) }
    var position by remember { mutableFloatStateOf(0f) }

    val downloadedIds by viewModel.downloadedAudioIds.collectAsState()
    val isDownloaded = downloadedIds.contains(audio.id)
    
    var isDownloading by remember { mutableStateOf(false) }

    //  LOAD AUDIO FROM FILE OR URL
    LaunchedEffect(audio.file, isDownloaded) {
        try {
            mediaPlayer.reset()
            if (isDownloaded) {
                val file = downloadManager.getAudioFile(audio.id)
                if (file.exists()) {
                    mediaPlayer.setDataSource(file.absolutePath)
                } else {
                    mediaPlayer.setDataSource("https://poweroftheword.bi${audio.file}")
                }
            } else {
                mediaPlayer.setDataSource("https://poweroftheword.bi${audio.file}")
            }

            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                duration = it.duration.toFloat()
            }

            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //  UPDATE POSITION
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
            .height(220.dp)
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // ICON BOX
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column {
                    Text(
                        text = formatDate(audio.date),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatTime(audio.visibleTime ?: "04:00h"),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // PLAY / DOWNLOAD BUTTON
                Button(
                    onClick = {
                        if (isDownloaded) {
                            if (!isPrepared) return@Button
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                                setIsPlaying(false)
                            } else {
                                mediaPlayer.start()
                                setIsPlaying(true)
                                viewModel.onAudioListened(audio.id)
                            }
                        } else if (!isDownloading) {
                            isDownloading = true
                            viewModel.downloadAudio(audio)
                        }
                    },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDownloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if (isDownloading && !isDownloaded) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isDownloaded) {
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
                            } else {
                                Icons.Default.Download
                            },
                            contentDescription = null,
                            modifier = Modifier.size(35.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //  CUSTOM SLIDER
            Slider(
                value = position,
                onValueChange = {
                    position = it
                    mediaPlayer.seekTo(it.toInt())
                },
                enabled = isDownloaded,
                valueRange = 0f..maxOf(duration, 1f),
                modifier = Modifier.fillMaxWidth(),
                track = {
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                CircleShape
                            )
                    ) {
                        val progress = if (duration > 0f) position / duration else 0f
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .fillMaxWidth(progress)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )
                    }
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .shadow(1.dp, CircleShape)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .border(
                                2.5f.dp,
                                if (isDownloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                CircleShape
                            )
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(position),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(duration),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audio.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // LIKE BUTTON
                IconButton(onClick = {
                    viewModel.toggleLike(audio.id.toString())
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                        tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null
                    )
                }

                // SHARE BUTTON
                IconButton(onClick = {
                    if (isDownloaded) {
                        viewModel.shareDownloadedAudio(audio)
                    } else {
                        viewModel.shareAudio(audio)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = if (isDownloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}
