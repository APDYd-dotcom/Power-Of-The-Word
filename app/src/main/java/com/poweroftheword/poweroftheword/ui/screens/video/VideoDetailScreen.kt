package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
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
fun VideoDetailScreen(
    viewModel: VideoDetailViewModel,
    onBackClick: () -> Unit,
    onVideoClick: (Video) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val video = state.video
            if (video != null) {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    // Video Player Placeholder / Thumbnail
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            AsyncImage(
                                model = video.thumbnailUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Play Button Overlay
                            Surface(
                                modifier = Modifier.align(Alignment.Center),
                                shape = CircleShape,
                                color = Color.Red
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp).size(40.dp)
                                )
                            }
                            // Duration
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
                    }

                    // Video Info
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${video.views} views • 5 days ago",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Channel Info
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=100&auto=format&fit=crop",
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Power of the Word",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Action Buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { /* Like */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF2F2F2),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    Icon(Icons.Outlined.ThumbUp, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("3.1K", fontSize = 14.sp)
                                }
                                
                                Button(
                                    onClick = { /* Share */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF2F2F2),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Share", fontSize = 14.sp)
                                }
                                
                                Spacer(modifier = Modifier.weight(1f))
                                
                                IconButton(
                                    onClick = { /* More actions */ },
                                    modifier = Modifier.background(Color(0xFFF2F2F2), CircleShape)
                                ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = null)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Description Box
                            Surface(
                                color = Color(0xFFF8F8F8),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = video.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Show more",
                                        color = Color.Blue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 8.dp).clickable { /* Expand */ }
                                    )
                                }
                            }
                        }
                    }

                    // Divider
                    item {
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // More Videos Section
                    item {
                        Text(
                            text = "More videos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    items(state.relatedVideos) { relatedVideo ->
                        RelatedVideoItem(
                            video = relatedVideo,
                            onClick = { onVideoClick(relatedVideo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RelatedVideoItem(video: Video, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier.size(140.dp, 80.dp).clip(RoundedCornerShape(8.dp))) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
            ) {
                Text(
                    text = "52:30",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Power of the Word",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = "${video.views} views • 2 days ago",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
        
        IconButton(onClick = { /* Menu */ }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
        }
    }
}
