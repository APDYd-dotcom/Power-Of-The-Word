package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poweroftheword.poweroftheword.domain.model.VideoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    viewModel: VideoListViewModel,
    onBackClick: () -> Unit,
    onVideoClick: (VideoItem) -> Unit
) {
    val video = viewModel.video.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                title = {}
            )
        }
    ) { padding ->

        if (isLoading.value) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            item {
                YoutubePlayerComposable(
                    videoUrl = video.value[0].url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            items(video.value) { related ->
                RelatedVideoCard(
                    video = related,
                    onClick = { onVideoClick(related) }
                )
            }
        }
    }
}

@Composable
fun ActionChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        color = Color(0xFFF2F2F2),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}