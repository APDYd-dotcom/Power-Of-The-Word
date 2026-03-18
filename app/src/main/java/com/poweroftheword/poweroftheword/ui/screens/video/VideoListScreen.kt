package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.domain.model.Video

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    viewModel: VideoListViewModel,
    onVideoClick: (Video) -> Unit
) {
    val videos by viewModel.filteredVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val error by viewModel.error.collectAsState()

    val videoTypes = listOf("preach", "testimony", "live")

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = {
                        Text(
                            "Videos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    },
                    actions = {
                        Surface(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                "EN",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { /* Search */ }) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )

                // Filter chips like YouTube's top bar
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                ) {
                    item {
                        FilterChip(
                            selected = selectedType == null,
                            onClick = { viewModel.onTypeSelect(null) },
                            label = { Text("All", fontSize = 14.sp) },
                            shape = RoundedCornerShape(20.dp),
                            border = null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFFF2F2F2),
                                labelColor = Color.Black,
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(videoTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { viewModel.onTypeSelect(type) },
                            label = { Text(type.replaceFirstChar { it.uppercase() }, fontSize = 14.sp) },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                                labelColor = Color.Black,
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadVideos() },
            modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)
        ) {
            if (videos.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = error ?: "No videos found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { video ->
                        VideoYouTubeItem(
                            video = video,
                            onClick = {
                                viewModel.onVideoViewed(video.id)
                                onVideoClick(video)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoYouTubeItem(
    video: Video,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Duration Badge (Dummy data for UI)
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Text(
                    text = "45:15",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            AsyncImage(
                model = "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=100&auto=format&fit=crop", // Using a placeholder for avatar
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Power of the Word",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "${video.views} views • 2 days ago",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            IconButton(
                onClick = { /* Menu */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert, 
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}
