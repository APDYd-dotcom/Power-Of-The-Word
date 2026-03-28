package com.poweroftheword.poweroftheword.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.domain.model.Feed
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.domain.model.Video

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onVideoClick: (Video) -> Unit,
    onFeedClick: (Feed) -> Unit,
    onLiveClick: (String) -> Unit,
    onRadioClick: () -> Unit,
    onSeeAllVideos: () -> Unit,
    onSeeAllFeeds: () -> Unit
) {
    val state by viewModel.state.collectAsState()


    Surface () {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadHomeData() },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column() {
                LazyColumn {

                    item { DynamicHero(viewModel.state.collectAsState().value.dailyWord) }

                    item {
//                        YoutubeVideoCard(
//                            "dQw4w9WgXcQ",
//                            "Discover how the words you speak can transform your life...",
//                            "12 Views"
//                        )
                    }

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

                Spacer(modifier = Modifier.height(40.dp))
            }

        }
    }
}

fun extractYouTubeVideoId(url: String): String? {
    val patterns = listOf(
        Regex("""youtu\.be/([A-Za-z0-9_-]{11})"""),
        Regex("""[?&]v=([A-Za-z0-9_-]{11})"""),
        Regex("""youtube\.com/embed/([A-Za-z0-9_-]{11})""")
    )
    for (pattern in patterns) {
        val match = pattern.find(url)
        if (match != null) return match.groupValues[1]
    }
    if (url.matches(Regex("""[A-Za-z0-9_-]{11}"""))) return url
    return null
}

@Composable
fun FeedLargeCard(feed: FeedItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column {
            Box(modifier = Modifier.height(192.dp).fillMaxWidth()) {
                AsyncImage(
                    model = feed.photo ?: "",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.8f)),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
                Text(
                    text = feed.title,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = feed.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2 days ago", // Fixed timestamp to match React stub behavior or map to real model property later
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = rememberAsyncImagePainter(android.R.drawable.ic_menu_view), // Using generic drawable for view eye icon
                            contentDescription = "Views",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "1,243 views", // Static string based on React design, adapt to real data as needed
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}