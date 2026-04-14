package com.poweroftheword.poweroftheword.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.ui.theme.*
import com.poweroftheword.poweroftheword.util.truncate
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.util.formatDate

@Composable
fun FeedItemCard(
    feed: FeedItem,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onClick: () -> Unit
) {

    val categoryColor = when (feed.type.lowercase()) {
        "igikorane" -> FigmaBrightBlue
        "itaganzo" -> FigmaGreen
        else -> FigmaPurple
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column {

            // 🔥 IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {

                AsyncImage(
                    model = "${BuildConfig.BASE_URL}${feed.photo}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 🔥 GRADIENT
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(0.3f),
                                    Color.Black.copy(0.85f)
                                )
                            )
                        )
                )

                // 🔥 CATEGORY
                Surface(
                    color = categoryColor,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = feed.type.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // 🔥 TITLE
                Text(
                    text = feed.title.truncate(50),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 🔥 CONTENT
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = feed.desc.truncate(120),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = formatDate(feed.date),
                        fontSize = 12.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        IconButton(onClick = onLikeClick) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                contentDescription = null,
                                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "1.2K views",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
