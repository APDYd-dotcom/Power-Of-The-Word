package com.poweroftheword.poweroftheword.ui.screens.live

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.domain.model.LiveItem
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.ui.screens.video.VideoListViewModel
import com.poweroftheword.poweroftheword.ui.theme.LocalStatusBarAppearance
import com.poweroftheword.poweroftheword.ui.screens.video.YoutubePlayerComposable
import com.poweroftheword.poweroftheword.util.extractYoutubeId
import com.poweroftheword.poweroftheword.util.formatDate

private val LiveRed = Color(0xFFE62117)
private val DarkBackground = Color(0xFF0F111A)
private val SurfaceColor = Color(0xFF1A1D29)

@OptIn(UnstableApi::class)
@Composable
fun LivePlayerScreen(
    liveId: Int,
    viewModel: LiveViewModel,
    videoViewModel: VideoListViewModel,
    onBackClick: () -> Unit,
    onVideoClick: (VideoItem) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lives by viewModel.liveStreams.collectAsState()
    val videos by videoViewModel.filteredVideos.collectAsState()
    val live = remember(lives, liveId) { lives.find { it.id == liveId } }

    val liveVideos = remember(videos) { videos.filter { it.type.equals("live", ignoreCase = true) } }

    var isFullScreen by remember { mutableStateOf(false) }
    val statusBarAppearance = LocalStatusBarAppearance.current

    LaunchedEffect(Unit) {
        statusBarAppearance.isDarkIcons = false
        viewModel.onLiveClicked(liveId)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            statusBarAppearance.isDarkIcons = null
        }
    }

    if (live == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = LiveRed)
        }
        return
    }

    val completeVideoUrl = remember(live.streamUrl) {
        val streamUrl = live.streamUrl.trim()
        when {
            streamUrl.isBlank() -> ""
            streamUrl.startsWith("http") -> streamUrl
            else -> {
                val baseUrl = BuildConfig.BASE_URL.trim().removeSuffix("/")
                val path = if (streamUrl.startsWith("/")) streamUrl else "/$streamUrl"
                "$baseUrl$path"
            }
        }
    }

    val youtubeId = remember(completeVideoUrl) { extractYoutubeId(completeVideoUrl) }
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("LivePlayer", "ExoPlayer Error: ${error.message}", error)
                }
            })
        }
    }

    LaunchedEffect(completeVideoUrl, youtubeId) {
        if (youtubeId == null && completeVideoUrl.isNotBlank()) {
            val mediaItem = MediaItem.fromUri(completeVideoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        } else {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Player Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            if (youtubeId != null) {
                YoutubePlayerComposable(
                    videoUrl = completeVideoUrl,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                            setShowNextButton(false)
                            setShowPreviousButton(false)
                            setShowFastForwardButton(false)
                            setShowRewindButton(false)
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

            // Top Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // LIVE Button (Jump to Edge) - YouTube style
                    if (youtubeId == null) {
                        Surface(
                            onClick = { 
                                exoPlayer.seekToDefaultPosition()
                                exoPlayer.play()
                            },
                            color = LiveRed,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color.White, CircleShape)
                                )
                                Text(
                                    text = "LIVE",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            isFullScreen = !isFullScreen
                            activity?.requestedOrientation = if (isFullScreen) {
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            } else {
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            }
                        },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Info Section
        if (!isFullScreen) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = LiveRed,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${live.view} watching",
                                    fontSize = 13.sp,
                                    color = Color(0xFF3EA6FF),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatDate(live.date),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        // Pill Like Button
                        Surface(
                            onClick = { viewModel.onLikeClicked(live.id) },
                            shape = RoundedCornerShape(24.dp),
                            color = SurfaceColor,
                            modifier = Modifier.height(40.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Like",
                                    tint = Color(0xFF3EA6FF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = live.like.toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = live.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Suggested Section Header
                    Text(
                        text = "Past Live Broadcasts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Suggested other real-time lives
                    lives.filter { it.id != live.id }.forEach { suggestedLive ->
                        SuggestedLiveCard(suggestedLive) {
                            viewModel.onLiveClicked(suggestedLive.id)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Suggested "live" type videos (Past Broadcasts)
                    liveVideos.forEach { video ->
                        SuggestedVideoLiveCard(video) {
                            onVideoClick(video)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun SuggestedVideoLiveCard(video: VideoItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = SurfaceColor
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                val youtubeId = extractYoutubeId(video.url)
                val thumbnailUrl = if (youtubeId != null) {
                    "https://img.youtube.com/vi/$youtubeId/mqdefault.jpg"
                } else {
                    "" 
                }

                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                Surface(
                    color = LiveRed,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f).align(Alignment.Top)) {
                Text(
                    text = video.title,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.views ?: 0} views",
                    fontSize = 12.sp,
                    color = Color(0xFF3EA6FF)
                )
            }
        }
    }
}

@Composable
fun SuggestedLiveCard(live: LiveItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = SurfaceColor
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = "${BuildConfig.BASE_URL}${live.thumbnail}",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                Surface(
                    color = LiveRed,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f).align(Alignment.Top)) {
                Text(
                    text = live.title,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${live.view} viewers",
                    fontSize = 12.sp,
                    color = Color(0xFF3EA6FF)
                )
            }
        }
    }
}
