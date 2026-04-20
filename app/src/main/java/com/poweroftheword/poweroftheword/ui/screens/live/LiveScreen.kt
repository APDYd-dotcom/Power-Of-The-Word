package com.poweroftheword.poweroftheword.ui.screens.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.domain.model.Live
import com.poweroftheword.poweroftheword.domain.model.LiveItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    viewModel: LiveViewModel,
    onLiveClick: (LiveItem) -> Unit
) {
    val liveStreams by viewModel.liveStreams.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Broadcasts", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading && liveStreams.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (liveStreams.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No live broadcasts at the moment.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(liveStreams) { live ->
                        LiveYouTubeStyleItem(
                            live = live,
                            onClick = {
                                viewModel.onLiveClicked(live.id)
                                onLiveClick(live)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveYouTubeStyleItem(live: LiveItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Color.Black)
        ) {
            AsyncImage(
                model = "${BuildConfig.BASE_URL}${live.thumbnail}",
                contentDescription = live.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Optional: Overlay the Play button
            Icon(
                Icons.Default.PlayArrow, 
                contentDescription = null, 
                modifier = Modifier.size(64.dp).align(Alignment.Center),
                tint = Color.White.copy(alpha = 0.5f)
            )
            
            if (live.isActive) {
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = Color.Red,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = "LIVE",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = "${live.view} watching",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("P", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = live.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2
                )
                Text(
                    text = "Power Of The Word • Live",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
