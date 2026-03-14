package com.poweroftheword.poweroftheword.ui.screens.audio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.poweroftheword.poweroftheword.domain.model.Audio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListScreen(
    viewModel: AudioListViewModel,
    onAudioClick: (Audio) -> Unit
) {
    val audios by viewModel.filteredAudios.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Audio Sermons") })
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading && audios.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (audios.isEmpty()) {
                Text(
                    text = "No audio sermons found.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(audios) { audio ->
                        AudioItem(
                            audio = audio,
                            onClick = {
                                viewModel.onAudioListened(audio.id)
                                onAudioClick(audio)
                            },
                            onLikeClick = { viewModel.likeAudio(audio.id) },
                            onShareClick = { viewModel.shareAudio(audio) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search audios...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun AudioItem(
    audio: Audio,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = audio.title, style = MaterialTheme.typography.titleMedium)
                Text(text = audio.date, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "${audio.listens} listens",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row {
                IconButton(onClick = onLikeClick) {
                    Icon(Icons.Default.Favorite, contentDescription = "Like", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }
}
