package com.poweroftheword.poweroftheword.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.ui.components.FeedItemCardSkeleton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onFeedClick: (FeedItem) -> Unit
) {
    val feeds by viewModel.feeds.collectAsState()
    val likedFeedIds by viewModel.likedFeedIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feed), fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading && feeds.isNotEmpty(),
            onRefresh = { viewModel.loadFeeds() },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when {
                isLoading && feeds.isEmpty() -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(5) {
                            FeedItemCardSkeleton()
                        }
                    }
                }

                feeds.isEmpty() && !isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.no_feeds_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(feeds) { feed ->
                            FeedItemCard(
                                feed = feed,
                                isLiked = likedFeedIds.contains(feed.id.toString()),
                                onLikeClick = { viewModel.toggleLike(feed.id.toString()) },
                                onClick = { onFeedClick(feed) }
                            )
                        }
                    }
                }
            }
        }
    }
}
