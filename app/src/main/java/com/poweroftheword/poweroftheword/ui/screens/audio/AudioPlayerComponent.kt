package com.poweroftheword.poweroftheword.ui.screens.audio

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
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
import com.poweroftheword.poweroftheword.util.formatTime
import com.poweroftheword.poweroftheword.util.formatDate
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerComponent(
    context: Context,
    audioUrl: String,
    viewModel : AudioListViewModel,
    audioId: Int,
    isLiked: Boolean,
    date: String,
    time: String,
    title: String
) {
    val mediaPlayer = remember { MediaPlayer() }

    var isPrepared by remember { mutableStateOf(false) }
    val (isPlaying, setIsPlaying) = rememberSaveable { mutableStateOf(false) }

    var duration by remember { mutableFloatStateOf(0f) }
    var position by remember { mutableFloatStateOf(0f) }

    //  LOAD AUDIO FROM URL
    LaunchedEffect(audioUrl) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource( "https://poweroftheword.bi${audioUrl}")

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
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A3442)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f))
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

                // ICON BOX (UNCHANGED)
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
                    text = formatDate(date),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = formatTime(time),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp),
                    fontSize = 12.sp,
                    color = Color.White
                )
            }


                // PLAY BUTTON (UNCHANGED)
                Button(
                    onClick = {
                        if (!isPrepared) return@Button

                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            setIsPlaying(false)
                        } else {
                            mediaPlayer.start()
                            setIsPlaying(true)
                        }
                    },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //  CUSTOM SLIDER (UNCHANGED DESIGN)
            Slider(
                value = position,
                onValueChange = {
                    position = it
                    mediaPlayer.seekTo(it.toInt())
                },
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
                        val progress =
                            if (duration > 0f) position / duration else 0f

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
                                MaterialTheme.colorScheme.primary,
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
                Text(formatTime(position), fontSize = 11.sp)
                Text(formatTime(duration), fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // LIKE BUTTON
                IconButton(onClick = {
                    viewModel.likeAudio(audioId)
                }) {
                    Icon(
                        Icons.Outlined.ThumbUp,
                        tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = null)
                }

                // SHARE BUTTON (FIXED FOR URL)
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "$title\n$audioUrl")
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Audio"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = null)
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