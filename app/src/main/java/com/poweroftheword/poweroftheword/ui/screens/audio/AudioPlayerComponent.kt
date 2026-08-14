package com.poweroftheword.poweroftheword.ui.screens.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.domain.model.AudioItem
import com.poweroftheword.poweroftheword.util.ShareUtils
import com.poweroftheword.poweroftheword.util.formatDate
import com.poweroftheword.poweroftheword.util.formatTime
import kotlinx.coroutines.launch
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
    
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val currentProgress = downloadProgress[audio.id]
    val isDownloading = currentProgress != null

    val shareAppMessage = stringResource(R.string.share_app_message)
    val shareFormat = stringResource(R.string.audio_share_format)

    var showCheck by remember { mutableStateOf(false) }
    var downloadStarted by remember { mutableStateOf(false) }

    LaunchedEffect(isDownloading) {
        if (isDownloading) downloadStarted = true
    }

    LaunchedEffect(isDownloaded) {
        if (isDownloaded && downloadStarted) {
            showCheck = true
            delay(2000)
            showCheck = false
            downloadStarted = false
        }
    }

    //  LOAD AUDIO FROM FILE OR URL
    LaunchedEffect(audio.file, isDownloaded) {
        val wasPlaying = isPlaying
        val lastPosition = if (isPrepared) {
            try { mediaPlayer.currentPosition } catch (e: Exception) { 0 }
        } else 0

        try {
            isPrepared = false
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

            mediaPlayer.setOnPreparedListener { mp ->
                isPrepared = true
                duration = mp.duration.toFloat()
                if (lastPosition > 0) {
                    mp.seekTo(lastPosition)
                }
                if (wasPlaying) {
                    mp.start()
                }
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
                        imageVector = Icons.Rounded.GraphicEq,
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

                // WHATSAPP STYLE ACTION BUTTON
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = currentProgress ?: 0f,
                        animationSpec = tween(durationMillis = 500),
                        label = "downloadProgress"
                    )

                    if (isDownloading && !isDownloaded) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    }

                    val buttonColor by animateColorAsState(
                        targetValue = when {
                            showCheck -> Color(0xFF4CAF50)
                            isPlaying -> MaterialTheme.colorScheme.primary
                            isDownloaded -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        label = "buttonColor"
                    )

                    val iconTint by animateColorAsState(
                        targetValue = if (isPlaying) Color.White else Color.White,
                        label = "iconTint"
                    )

                    FilledIconButton(
                        onClick = {
                            if (isDownloaded || isPrepared) {
                                if (mediaPlayer.isPlaying) {
                                    mediaPlayer.pause()
                                    setIsPlaying(false)
                                } else {
                                    mediaPlayer.start()
                                    setIsPlaying(true)
                                    viewModel.onAudioListened(audio.id)
                                }
                                if (!isDownloaded && !isDownloading) {
                                    viewModel.downloadAudio(audio)
                                }
                            } else if (!isDownloading) {
                                viewModel.downloadAudio(audio)
                            }
                        },
                        modifier = Modifier.size(if (isDownloading && !isDownloaded) 38.dp else 50.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = buttonColor,
                            contentColor = iconTint
                        ),
                        shape = CircleShape
                    ) {
                        AnimatedContent(
                            targetState = when {
                                showCheck -> Icons.Rounded.Check
                                isPlaying -> Icons.Rounded.Pause
                                isDownloading && !isDownloaded -> Icons.Rounded.Close
                                isDownloaded || isPrepared -> Icons.Rounded.PlayArrow
                                else -> Icons.Rounded.Download
                            },
                            transitionSpec = {
                                fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                            },
                            label = "actionIcon"
                        ) { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(if (icon == Icons.Rounded.Close) 18.dp else 28.dp)
                            )
                        }
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
                enabled = isDownloaded || isPrepared,
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
                                if (isDownloaded || isPrepared) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
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
//                IconButton(onClick = {
//                    viewModel.onAudioShared(audio.id)
//                    val shareText = shareFormat.format(
//                        formatDate(audio.date),
//                        audio.title,
//                        shareAppMessage
//                    )
//
//                    if (isDownloaded) {
//                        val file = downloadManager.getAudioFile(audio.id)
//                        if (file.exists()) {
//                            ShareUtils.shareAudioFile(context, file, audio.title, shareText)
//                        } else {
//                            ShareUtils.shareText(context, shareText)
//                        }
//                    } else {
//                        ShareUtils.shareText(context, shareText)
//                    }
//                }) {
//                    Icon(
//                        imageVector = Icons.Default.Share,
//                        contentDescription = null,
//                        tint = if (isDownloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}
