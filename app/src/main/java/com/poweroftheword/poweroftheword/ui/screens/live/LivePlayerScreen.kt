package com.poweroftheword.poweroftheword.ui.screens.live

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.ui.theme.LocalStatusBarAppearance
import com.poweroftheword.poweroftheword.ui.screens.video.YoutubePlayerComposable
import com.poweroftheword.poweroftheword.util.extractYoutubeId

@OptIn(UnstableApi::class)
@Composable
fun LivePlayerScreen(
    videoUrl: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isFullScreen by remember { mutableStateOf(false) }
    val statusBarAppearance = LocalStatusBarAppearance.current

    // Force white icons (isDarkIcons = false) for the dark video background
    LaunchedEffect(Unit) {
        statusBarAppearance.isDarkIcons = false
    }
    
    DisposableEffect(Unit) {
        onDispose {
            statusBarAppearance.isDarkIcons = null
        }
    }

    // Ensure we have a complete URL
    val completeVideoUrl = remember(videoUrl) {
        if (videoUrl.startsWith("http")) videoUrl else "${BuildConfig.BASE_URL}$videoUrl"
    }

    val youtubeId = remember(completeVideoUrl) { extractYoutubeId(completeVideoUrl) }
    
    // Pulse animation for the LIVE dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    LaunchedEffect(completeVideoUrl) {
        if (youtubeId == null) {
            val mediaItem = MediaItem.fromUri(completeVideoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (youtubeId != null) {
            YoutubePlayerComposable(
                videoUrl = completeVideoUrl,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // High-performance Video Player
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setBackgroundColor(android.graphics.Color.BLACK)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = {
                    it.player = exoPlayer
                }
            )
        }

        // Gradient overlay at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            FilledIconButton(
                onClick = onBackClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(26.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Full Screen Toggle Button
                FilledIconButton(
                    onClick = {
                        isFullScreen = !isFullScreen
                        activity?.requestedOrientation = if (isFullScreen) {
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else {
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Fullscreen",
                        modifier = Modifier.size(26.dp)
                    )
                }

                // LIVE Indicator
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = dotAlpha))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
