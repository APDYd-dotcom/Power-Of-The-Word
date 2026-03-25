package com.poweroftheword.poweroftheword.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@Composable
fun PostCard(
    title: String,
    description: String,
    imageRes: Int,
    time: String,
    views: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column {

            // 🔥 IMAGE + TITLE OVERLAY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 🌑 Gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // 📝 Title on image
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }

            // 📄 CONTENT SECTION
            Column(
                modifier = Modifier
                    .background(Color(0xFF1E2A38))
                    .padding(12.dp)
            ) {

                Text(
                    text = description,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ⏱️ Time + 👁 Views
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = time,
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = views,
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}