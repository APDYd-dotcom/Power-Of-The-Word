package com.poweroftheword.poweroftheword.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.poweroftheword.poweroftheword.domain.model.FeedItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onFeedClick: (FeedItem) -> Unit
) {
    val feeds by viewModel.feeds.collectAsState()
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            }

            feeds.isEmpty() -> {
                Text(
                    text = "No feeds available",
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            }

            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isLoading,
                    onRefresh = { viewModel.loadFeeds() },
                    modifier = Modifier
                            .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(feeds) { feed ->

                            FeedItemCard(
                                feed = feed,
                                onClick = { onFeedClick(feed) }
                            )
                        }
                    }
                }

            }
        }
    }
}