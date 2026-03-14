package com.poweroftheword.poweroftheword.ui.screens.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.poweroftheword.poweroftheword.domain.model.Live

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    viewModel: LiveViewModel,
    onLiveClick: (Live) -> Unit
) {
    val liveStreams by viewModel.liveStreams.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Live Broadcasts") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading && liveStreams.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (liveStreams.isEmpty()) {
                Text(
                    text = "No live broadcasts at the moment.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(liveStreams) { live ->
                        LiveItem(
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
fun LiveItem(live: Live, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(if (live.isActive) Color.Red else Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = live.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (live.isActive) "LIVE NOW" else "Offline",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (live.isActive) Color.Red else Color.Gray
                )
                Text(
                    text = "${live.viewers} viewers",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
