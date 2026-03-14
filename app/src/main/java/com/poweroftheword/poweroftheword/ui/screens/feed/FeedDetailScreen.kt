package com.poweroftheword.poweroftheword.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.domain.model.Feed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailScreen(
    feed: Feed?,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Announcement") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (feed == null) {
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Text(text = "Announcement not found.", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                feed.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(feed.type.replaceFirstChar { it.uppercase() }) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = feed.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = feed.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
