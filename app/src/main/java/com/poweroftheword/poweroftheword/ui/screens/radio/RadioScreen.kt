package com.poweroftheword.poweroftheword.ui.screens.radio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poweroftheword.poweroftheword.domain.model.Radio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(
    viewModel: RadioViewModel
) {
    val radioStatus by viewModel.radioStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentlyPlayingId by viewModel.currentlyPlayingId.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    // Filtered list to avoid duplicates if radioStatus is also in the list
//    val radioList = remember(radioStatus) {
//        val staticList = listOf(
//            Radio("1", "RFI", "https://rfimonde64k.ice.infomaniak.ch/rfimonde-64.mp3", "00:00", "23:59", true),
//            Radio("2", "Gospel FM", "", "00:00", "23:59", false),
//            Radio("3", "Praise Radio", "", "00:00", "23:59", false),
//            Radio("4", "Classic FM", "", "00:00", "23:59", true)
//        )
//        // If we have a real radioStatus from API, we could prepend or replace
//        radioStatus?.let { listOf(it) + staticList.filter { s -> s.id != it.id } } ?: staticList
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1F2A3A), Color(0xFF0D1B2A), Color(0xFF0D1B2A), MaterialTheme.colorScheme.background)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Radio",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->

            if (isLoading && radioStatus == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {

                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // 🔷 HEADER
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF2A3442)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Radio,
                                    contentDescription = null,
                                    tint = Color(0xFF4DA3FF),
                                    modifier = Modifier.size(64.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                "Radio",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 🔷 TITLE
                    item {
                        Text(
                            "ALL STATIONS",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 🔷 LIST
                    items(radioStatus) { radio ->
                        RadioStationCard(
                            radio = radio,
                            isCurrentlyPlaying = radio.id == currentlyPlayingId,
                            isPlaying = isPlaying,
                            onPlayClick = { viewModel.togglePlay(radio) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RadioStationCard(
    radio: Radio,
    isCurrentlyPlaying: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A3442)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🎧 IMAGE + LIVE BADGE
            Box {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Radio,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    )
                }

                if (radio.isActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-6).dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Red)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "LIVE",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // TEXT
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = radio.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    if (isCurrentlyPlaying) "Currently playing" else "Radio Station",
                    color = if (isCurrentlyPlaying) Color(0xFF4DA3FF) else Color.Gray,
                    fontSize = 12.sp
                )
            }

            // PLAY BUTTON
            Button(
                onClick = onPlayClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentlyPlaying && isPlaying) Color.Red.copy(alpha = 0.8f) else Color(0xFF2F6BFF)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (isCurrentlyPlaying && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (isCurrentlyPlaying && isPlaying) "PAUSE" else "PLAY",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
