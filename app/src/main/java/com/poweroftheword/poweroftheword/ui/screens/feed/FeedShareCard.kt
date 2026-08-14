package com.poweroftheword.poweroftheword.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.R

@Composable
fun FeedShareCard(
    feed: FeedItem,
    modifier: Modifier = Modifier
) {
    // Pro theme colors from the screenshot
    val backgroundColor = Color(0xFFFDF7E7) // Light cream background
    val primaryTextColor = Color(0xFF1A1A1B)
    val secondaryTextColor = Color(0xFF4A4A4B)
    val accentColor = Color(0xFFD4E3F3) // Soft blue for category tag background

    Card(
        modifier = modifier
            .width(360.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // 🔥 IMAGE HEADER
            AsyncImage(
                model = "${BuildConfig.BASE_URL}${feed.photo}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(24.dp)) {

                // 🔥 CATEGORY TAG
                Surface(
                    shape = RoundedCornerShape(50),
                    color = accentColor
                ) {
                    Text(
                        text = feed.type?.uppercase() ?: "EVENT",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = Color(0xFF5A8DBE),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔥 TITLE
                Text(
                    text = feed.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp
                    ),
                    color = primaryTextColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 🔥 META INFO
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Power of the Word",
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryTextColor
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = secondaryTextColor
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = feed.date ?: "Recently",
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryTextColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = secondaryTextColor
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${feed.views} views",
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryTextColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))

                Spacer(modifier = Modifier.height(20.dp))

                // 🔥 DESCRIPTION
                feed.desc?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 26.sp
                        ),
                        color = primaryTextColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔥 EVENT DETAILS HEADER
                Text(
                    text = stringResource(R.string.event_details_label),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge,
                    color = primaryTextColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShareDetailRow("📅", stringResource(R.string.date_label), feed.date ?: "N/A", primaryTextColor)
                ShareDetailRow("⏰", stringResource(R.string.time_label), "${feed.startHour ?: ""} - ${feed.endHour ?: ""}", primaryTextColor)
                ShareDetailRow("📍", stringResource(R.string.location_label), feed.location ?: "Main Sanctuary", primaryTextColor)
                ShareDetailRow("🎤", stringResource(R.string.leader_label), feed.host ?: "Pastor & Team", primaryTextColor)

                Spacer(modifier = Modifier.height(32.dp))
                
                // Branding footer
                Text(
                    text = "poweroftheword.bi",
                    style = MaterialTheme.typography.labelMedium,
                    color = secondaryTextColor.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun ShareDetailRow(emoji: String, label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}
