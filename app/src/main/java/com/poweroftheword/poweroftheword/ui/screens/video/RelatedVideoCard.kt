package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.util.extractYoutubeId

@Composable
fun RelatedVideoCard(
    video: VideoItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val videoId = extractYoutubeId(video.url) ?: ""
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("https://img.youtube.com/vi/$videoId/maxresdefault.jpg")
                .crossfade(true)
                .error(R.drawable.logo)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp, 80.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {

            Text(
                text = video.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Power of the Word",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${video.view ?: 0} views",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
