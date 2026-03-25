package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.poweroftheword.poweroftheword.R

@Composable
fun VideoCard() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Popular Video", fontWeight = FontWeight.Bold)
            Text("View All", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {

                Box {

                    Image(
                        painter = painterResource(R.drawable.dailword1),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.height(180.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center)
                    )
                }

                Text(
                    "Power of Words - Motivational Speech",
                    modifier = Modifier.padding(12.dp)
                )

                Text(
                    "125,000 views",
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                )
            }
        }
    }
}