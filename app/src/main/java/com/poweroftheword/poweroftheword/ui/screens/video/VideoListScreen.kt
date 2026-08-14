package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.ui.components.LanguageDropdownWrapper
import com.poweroftheword.poweroftheword.ui.components.VideoCardSkeleton
import com.poweroftheword.poweroftheword.util.ShareUtils
import com.poweroftheword.poweroftheword.util.convertToYoutubeOriginalUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    viewModel: VideoListViewModel,
    onVideoClick: (VideoItem) -> Unit
) {
    val videos by viewModel.filteredVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val context = LocalContext.current

    val shareAppMessage = stringResource(R.string.share_app_message)
    val pastorName = stringResource(R.string.pastor_name)
    val shareFormat = stringResource(R.string.video_share_format)

    val videoTypes = listOf("preach", "testimony", "live")

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.videos),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    actions = {
                        Box(modifier = Modifier.padding(end = 16.dp)) {
                            LanguageDropdownWrapper(
                                selectedLang = currentLanguage,
                                onLangChange = { viewModel.onLanguageChange(it) }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                ) {
                    item {
                        FilterChip(
                            selected = selectedType == null,
                            onClick = { viewModel.onTypeSelect(null) },
                            label = { Text(stringResource(R.string.all), fontSize = 14.sp) },
                            shape = RoundedCornerShape(20.dp),
                            border = null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (selectedType == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (selectedType == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    items(videoTypes) { type ->
                        val label = when(type) {
                            "preach" -> stringResource(R.string.preach)
                            "testimony" -> stringResource(R.string.testimony)
                            "live" -> stringResource(R.string.live)
                            else -> type.replaceFirstChar { it.uppercase() }
                        }
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { viewModel.onTypeSelect(type) },
                            label = { Text(label, fontSize = 14.sp) },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading && videos.isNotEmpty(),
            onRefresh = { viewModel.loadVideos() },
            modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            if (videos.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = error ?: stringResource(R.string.no_videos_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else if (isLoading && videos.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(5) {
                        VideoCardSkeleton()
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { item ->
                        VideoCard(
                            video = item,
                            onClick = { onVideoClick(item) },
                            onLikeClick = { viewModel.likeVideo(item.id.toString()) },
                            onShareClick = { 
                                viewModel.onVideoShared(item.id.toString())
                                val originalUrl = convertToYoutubeOriginalUrl(item.url)
                                val shareText = shareFormat.format(
                                    item.title,
                                    pastorName,
                                    originalUrl,
                                    shareAppMessage
                                )
                                ShareUtils.shareText(context, shareText)
                            }
                        )
                    }
                }
            }
        }
    }
}
