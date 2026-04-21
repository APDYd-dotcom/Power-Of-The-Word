package com.poweroftheword.poweroftheword.ui.screens.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.domain.model.LiveItem
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.ui.components.DynamicHeroSkeleton
import com.poweroftheword.poweroftheword.ui.components.FeedItemCardSkeleton
import com.poweroftheword.poweroftheword.ui.components.VideoCardSkeleton
import com.poweroftheword.poweroftheword.ui.screens.feed.FeedItemCard
import com.poweroftheword.poweroftheword.ui.screens.live.LiveYouTubeStyleItem
import com.poweroftheword.poweroftheword.ui.screens.video.VideoCard
import com.poweroftheword.poweroftheword.util.shimmerEffect
import com.poweroftheword.poweroftheword.util.truncate

@Composable
fun LiveSection(live: LiveItem, onLiveClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onLiveClick(live.streamUrl) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(
                    Color.Red.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Indicator with Pulse effect (simulated with static design for now)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = "${BuildConfig.BASE_URL}${live.thumbnail}",
                    contentDescription = live.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    color = Color.Red,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "LIVE",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Sensors,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Red
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "ON AIR NOW",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Red,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                
                Text(
                    text = live.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${live.view} people are watching",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
            
            Surface(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onFeedClick: (FeedItem) -> Unit,
    onLiveClick: (String) -> Unit,
    onSeeAllLive: () -> Unit,
    onRadioClick: () -> Unit,
    onSeeAllVideos: () -> Unit,
    onSeeAllFeeds: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val likedVideoIds by viewModel.likedVideoIds.collectAsState()
    val likedFeedIds by viewModel.likedFeedIds.collectAsState()

    Surface {
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.latestVideos.isNotEmpty(),
            onRefresh = { viewModel.loadHomeData() },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (state.isLoading && state.latestVideos.isEmpty()) {
                    item { DynamicHeroSkeleton() }
                    item { SectionHeaderSkeleton() }
                    item { VideoCardSkeleton() }
                    item { SectionHeaderSkeleton() }
                    items(3) { FeedItemCardSkeleton() }
                } else {
                    item { 
                        DynamicHero(
                            dailyWord = state.dailyWord,
                            currentLanguage = state.currentLanguage,
                            isDarkMode = isDarkMode,
                            onLanguageChange = { viewModel.changeLanguage(it) },
                            onThemeToggle = onThemeToggle
                        )
                    }

                    val activeLive = state.liveStreams.find { it.isActive }
                    val activeRadio = state.radioStatus.find { it.isActive }

                    if (activeLive != null || activeRadio != null) {
                        item {
                            SectionHeader(
                                title = "Live Stream",
                                onSeeAllClick = { onSeeAllLive() }
                            )
                        }
                        
                        if (activeLive != null) {
                            item {
                                LiveSection(
                                    live = activeLive,
                                    onLiveClick = { onLiveClick(activeLive.streamUrl) }
                                )
                            }
                        }

                        if (activeRadio != null) {
                            item {
                                RadioLiveSection(
                                    radio = activeRadio,
                                    onRadioClick = onRadioClick
                                )
                            }
                        }
                    }

                    if (state.latestVideos.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.popular_video),
                                onSeeAllClick = onSeeAllVideos
                            )
                        }
                        item {
                            val video = state.latestVideos[0]
                            VideoCard(
                                video = video.copy(isLiked = likedVideoIds.contains(video.id.toString())),
                                onClick = { onVideoClick(video) },
                                onLikeClick = { viewModel.likeVideo(video.id.toString()) }
                            )
                        }
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.latest_post),
                            onSeeAllClick = onSeeAllFeeds
                        )
                    }

                    items(state.latestFeeds.take(3)) { feed ->
                        FeedItemCard(
                            feed = feed,
                            isLiked = likedFeedIds.contains(feed.id.toString()),
                            onLikeClick = { viewModel.toggleFeedLike(feed.id.toString()) },
                            onClick = { onFeedClick(feed) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier.clickable { onSeeAllClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.view_all).truncate(12),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "See All",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun RadioLiveSection(radio: com.poweroftheword.poweroftheword.domain.model.Radio, onRadioClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onRadioClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                AsyncImage(
                    model = radio.photo,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "RADIO",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Sensors,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "RADIO ON AIR",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                
                Text(
                    text = radio.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${radio.startHour} - ${radio.endHour}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
            
            Surface(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SectionHeaderSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}
