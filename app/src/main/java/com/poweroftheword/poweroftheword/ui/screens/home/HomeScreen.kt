package com.poweroftheword.poweroftheword.ui.screens.home

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.domain.model.Feed
import com.poweroftheword.poweroftheword.domain.model.Video
import com.poweroftheword.poweroftheword.ui.screens.video.VideoCard
import com.poweroftheword.poweroftheword.ui.screens.video.YoutubeVideoCard

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//    viewModel: HomeViewModel,
//    onVideoClick: (Video) -> Unit,
//    onFeedClick: (Feed) -> Unit,
//    onLiveClick: (String) -> Unit,
//    onRadioClick: () -> Unit,
//    onSeeAllVideos: () -> Unit,
//    onSeeAllFeeds: () -> Unit
//) {
//    val state by viewModel.state.collectAsState()
//    val context = LocalContext.current
//    val view = LocalView.current
//
//    // Dynamic color logic from the Daily Word image
//    var dynamicColor by remember { mutableStateOf(Color.Black.copy(alpha = 0.5f)) }
//
//    val imageRequest = ImageRequest.Builder(context)
//        .data(R.drawable.dailword)
//        .allowHardware(false)
//        .build()
//
//    val painter = rememberAsyncImagePainter(model = imageRequest)
//
//    LaunchedEffect(painter.state) {
//        val painterState = painter.state
//        if (painterState is AsyncImagePainter.State.Success) {
//            val bitmap = (painterState.result.drawable as? BitmapDrawable)?.bitmap
//            bitmap?.let {
//                Palette.from(it).generate { palette ->
//                    val rgb = palette?.dominantSwatch?.rgb ?: Color.Black.toArgb()
//                    dynamicColor = Color(rgb)
//                }
//            }
//        }
//    }
//
//    // Status bar management
//    if (!view.isInEditMode) {
//        LaunchedEffect(dynamicColor) {
//            val window = (view.context as Activity).window
//            window.statusBarColor = Color.Transparent.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
//        }
//    }
//
//    Scaffold(
//        contentWindowInsets = WindowInsets(0, 0, 0, 0),
//        containerColor = MaterialTheme.colorScheme.background
//    ) { paddingValues ->
//        PullToRefreshBox(
//            isRefreshing = state.isLoading,
//            onRefresh = { viewModel.loadHomeData() },
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
//                    .padding(bottom = paddingValues.calculateBottomPadding())
//            ) {
//                // 1. IMMERSIVE HEADER (Daily Word)
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(320.dp)
//                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
//                ) {
//                    Image(
//                        painter = painter,
//                        contentDescription = "Daily Word",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//
//                    // Overlay for top bar elements
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(
//                                Brush.verticalGradient(
//                                    colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent, Color.Black.copy(alpha = 0.8f)),
//                                    startY = 0f,
//                                    endY = Float.POSITIVE_INFINITY
//                                )
//                            )
//                    )
//
//                    // Custom Top Bar inside the Image
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .statusBarsPadding()
//                            .padding(horizontal = 20.dp, vertical = 12.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            "Power of the Word",
//                            modifier = Modifier.weight(1f),
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White,
//                            fontSize = 22.sp
//                        )
//
//                        Surface(
//                            color = Color.White.copy(alpha = 0.2f),
//                            shape = RoundedCornerShape(8.dp),
//                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
//                        ) {
//                            Text(
//                                "EN",
//                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
//                                color = Color.White,
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.width(8.dp))
//
//                        IconButton(
//                            onClick = { /* Search */ },
//                            modifier = Modifier.size(40.dp)
//                        ) {
//                            Icon(
//                                Icons.Default.Search,
//                                contentDescription = "Search",
//                                tint = Color.White,
//                                modifier = Modifier.size(24.dp)
//                            )
//                        }
//                    }
//
//                    // Content over image (optional quote or label)
//                    Column(
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(20.dp)
//                    ) {
//                        Surface(
//                            color = MaterialTheme.colorScheme.secondary,
//                            shape = RoundedCornerShape(4.dp)
//                        ) {
//                            Text(
//                                "DAILY WORD",
//                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                                color = Color.White,
//                                fontSize = 10.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = "Thy word is a lamp unto my feet, and a light unto my path.",
//                            color = Color.White,
//                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
//                            maxLines = 2
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // 2. POPULAR VIDEOS SECTION
//                if (state.latestVideos.isNotEmpty()) {
//                    HomeSectionHeader(title = "Popular Videos", onSeeAll = onSeeAllVideos)
//
//                    state.latestVideos.forEach { video ->
//                        VideoYoutubeStyleItem(
//                            video = video,
//                            onClick = { onVideoClick(video) }
//                        )
//                        Spacer(modifier = Modifier.height(20.dp))
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                // 3. LATEST POSTS SECTION
//                if (state.latestFeeds.isNotEmpty()) {
//                    HomeSectionHeader(title = "Latest Posts", onSeeAll = onSeeAllFeeds)
//                    state.latestFeeds.forEach { feed ->
//                        FeedLargeCard(feed = feed, onClick = { onFeedClick(feed) })
//                        Spacer(modifier = Modifier.height(20.dp))
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(40.dp))
//            }
//        }
//    }
//}
//
//@Composable
//fun VideoYoutubeStyleItem(video: Video, onClick: () -> Unit) {
//    val videoId = extractYouTubeVideoId(video.videoUrl)
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() }
//            .padding(horizontal = 20.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = MaterialTheme.colorScheme.onSurface
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
//        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
//    ) {
//        Column {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(16f / 9f)
//                    .background(Color.Black)
//            ) {
//                if (videoId != null) {
//                    YouTubeLibraryPlayerView(
//                        videoId = videoId,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                } else {
//                    AsyncImage(
//                        model = video.thumbnailUrl,
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                    Surface(
//                        modifier = Modifier.align(Alignment.Center),
//                        shape = CircleShape,
//                        color = Color.Black.copy(alpha = 0.6f)
//                    ) {
//                        Icon(
//                            Icons.Default.PlayArrow,
//                            contentDescription = null,
//                            tint = Color.White,
//                            modifier = Modifier.padding(12.dp).size(32.dp)
//                        )
//                    }
//                }
//            }
//
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    text = video.title,
//                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        painter = coil.compose.rememberAsyncImagePainter(android.R.drawable.ic_menu_view), // Using implicit placeholder for eye
//                        contentDescription = "Views",
//                        modifier = Modifier.size(16.dp),
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "${video.views ?: 0} views", // Assuming views are available or 0
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun HomeSectionHeader(title: String, onSeeAll: (() -> Unit)?) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 20.dp, vertical = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = title,
//            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp),
//            color = MaterialTheme.colorScheme.onBackground
//        )
//        if (onSeeAll != null) {
//            Text(
//                "View All",
//                color = MaterialTheme.colorScheme.primary,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.clickable { onSeeAll() }
//            )
//        }
//    }
//}
//
//fun extractYouTubeVideoId(url: String): String? {
//    val patterns = listOf(
//        Regex("""youtu\.be/([A-Za-z0-9_-]{11})"""),
//        Regex("""[?&]v=([A-Za-z0-9_-]{11})"""),
//        Regex("""youtube\.com/embed/([A-Za-z0-9_-]{11})""")
//    )
//    for (pattern in patterns) {
//        val match = pattern.find(url)
//        if (match != null) return match.groupValues[1]
//    }
//    if (url.matches(Regex("""[A-Za-z0-9_-]{11}"""))) return url
//    return null
//}
//
//@Composable
//fun YouTubeLibraryPlayerView(
//    videoId: String,
//    modifier: Modifier = Modifier
//) {
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    AndroidView(
//        modifier = modifier,
//        factory = { context ->
//            YouTubePlayerView(context).apply {
//                lifecycleOwner.lifecycle.addObserver(this)
//                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                    override fun onReady(youTubePlayer: YouTubePlayer) {
//                        youTubePlayer.cueVideo(videoId, 0f)
//                    }
//                })
//            }
//        }
//    )
//}
//
//@Composable
//fun FeedLargeCard(feed: Feed, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .padding(horizontal = 20.dp)
//            .fillMaxWidth()
//            .clickable { onClick() },
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = MaterialTheme.colorScheme.onSurface
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
//    ) {
//        Column {
//            Box(modifier = Modifier.height(192.dp).fillMaxWidth()) {
//                AsyncImage(
//                    model = feed.imageUrl ?: "",
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            Brush.verticalGradient(
//                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.8f)),
//                                startY = 0f,
//                                endY = Float.POSITIVE_INFINITY
//                            )
//                        )
//                )
//                Text(
//                    text = feed.title,
//                    color = Color.White,
//                    modifier = Modifier
//                        .align(Alignment.BottomStart)
//                        .padding(16.dp),
//                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
//                    maxLines = 2
//                )
//            }
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    text = feed.description,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "2 days ago", // Fixed timestamp to match React stub behavior or map to real model property later
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            painter = coil.compose.rememberAsyncImagePainter(android.R.drawable.ic_menu_view), // Using generic drawable for view eye icon
//                            contentDescription = "Views",
//                            modifier = Modifier.size(14.dp),
//                            tint = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = "1,243 views", // Static string based on React design, adapt to real data as needed
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


@Composable
fun HomeScreen() {

    Column {

        DynamicHero()

        LazyColumn {

            item { YoutubeVideoCard(
                "dQw4w9WgXcQ",
                "Discover how the words you speak can transform your life...",
                "12 Views"
            ) }

            item {
                PostCard(
                    title = "The Power of Positive Words",
                    description = "Discover how the words you speak can transform your life and the lives of those around you...",
                    imageRes = R.drawable.dailword,
                    time = "2 days, 3 hours ago",
                    views = "1,243 views"
                )
            }

            item {
                PostCard(
                    title = "The Power of Positive Words",
                    description = "Discover how the words you speak can transform your life and the lives of those around you...",
                    imageRes = R.drawable.dailword1,
                    time = "2 days, 3 hours ago",
                    views = "1,243 views"
                )
            }
        }

        BottomNavBar()
    }
}