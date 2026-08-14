package com.poweroftheword.poweroftheword.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.ui.components.FeedDetailSkeleton
import com.poweroftheword.poweroftheword.ui.util.rememberCaptureController
import com.poweroftheword.poweroftheword.ui.util.capturable
import com.poweroftheword.poweroftheword.util.ShareUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedDetailScreen(
    feed: FeedItem?,
    viewModel: FeedViewModel,
    onBackClick: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()
    
    val shareAppMessage = stringResource(R.string.share_app_message)
    val feedShareFormat = stringResource(R.string.feed_share_format)
    val timeLabel = stringResource(R.string.time_label)
    val locationLabel = stringResource(R.string.location_label)
    val mainSanctuary = stringResource(R.string.main_sanctuary)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.event)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (feed != null) {
                        IconButton(onClick = { 
                            scope.launch {
                                val bitmap = captureController.capture()
                                val shareText = feedShareFormat.format(
                                    timeLabel,
                                    "${feed.startHour ?: ""} - ${feed.endHour ?: ""}",
                                    locationLabel,
                                    feed.location ?: mainSanctuary,
                                    shareAppMessage
                                )

                                ShareUtils.shareImage(
                                    context = context,
                                    bitmap = bitmap,
                                    title = feed.title,
                                    message = shareText
                                )
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share_image))
                        }
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (isLoading) {
                FeedDetailSkeleton()
            } else if (feed == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.event_not_found))
                }
            } else {
                // Register view when screen is opened
                LaunchedEffect(feed.id) {
                    viewModel.onFeedViewed(feed.id.toString())
                }

                val likedFeedIds by viewModel.likedFeedIds.collectAsState()
                val isLiked = likedFeedIds.contains(feed.id.toString())

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    // 🔥 IMAGE HEADER
                    AsyncImage(
                        model = "${BuildConfig.BASE_URL}${feed.photo}",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {

                        // 🔥 CATEGORY TAG
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = feed.type?.uppercase() ?: "",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 🔥 TITLE
                        Text(
                            text = feed.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 🔥 META INFO
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = "Power of the Word",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = feed.date ?: stringResource(R.string.recently),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = stringResource(R.string.views_count, feed.views ?: 0),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔥 DESCRIPTION
                        feed.desc?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔥 EXTRA DETAILS (STATIC STYLE FOR NOW)
                        Text(
                            text = stringResource(R.string.event_details_label) + ":",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow("📅 " + stringResource(R.string.date_label), feed.date ?: "N/A")
                        DetailRow("⏰ " + stringResource(R.string.time_label), "${feed.startHour ?: ""} - ${feed.endHour ?: ""}")
                        DetailRow("📍 " + stringResource(R.string.location_label), feed.location ?: stringResource(R.string.main_sanctuary))
                        DetailRow("🎤 " + stringResource(R.string.leader_label), feed.host ?: stringResource(R.string.pastor_and_team))

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!feed.expectation.isNullOrBlank()) {
                            Text(
                                text = stringResource(R.string.what_to_expect_header),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(feed.expectation)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔥 ACTION BUTTONS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.toggleLike(feed.id.toString()) }
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                    contentDescription = null,
                                    tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isLiked) stringResource(R.string.liked) else stringResource(R.string.like))
                            }

                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                onClick = { 
                                    scope.launch {
                                        val bitmap = captureController.capture()
                                        val shareText = feedShareFormat.format(
                                            timeLabel,
                                            "${feed.startHour ?: ""} - ${feed.endHour ?: ""}",
                                            locationLabel,
                                            feed.location ?: mainSanctuary,
                                            shareAppMessage
                                        )
                                        
                                        ShareUtils.shareImage(
                                            context = context,
                                            bitmap = bitmap,
                                            title = feed.title,
                                            message = shareText
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.share_image))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                
                // Hidden view for capturing
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .offset(y = 2000.dp) // Off-screen
                        .capturable(captureController)
                        .background(Color(0xFFFDF7E7))
                ) {
                    FeedShareCard(feed = feed)
                }
            }
        }
    }
}


@Composable
fun DetailRow(title: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$title: ", fontWeight = FontWeight.Bold)
        Text(value)
    }
}
