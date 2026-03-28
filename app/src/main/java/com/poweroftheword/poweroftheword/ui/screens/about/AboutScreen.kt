package com.poweroftheword.poweroftheword.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.poweroftheword.poweroftheword.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onDonationClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
        ) {

            // 👤 HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.dailword1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        "Pastor Justin Nitezuwera",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Evangelist of the Good News of Jesus Christ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 📝 DESCRIPTION
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Text(
                    "Wake up praying and praising the Lord every day!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Power of the Word is a broadcast to spiritually awaken Christians in order to allow the Holy Spirit to accomplish the will of God.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📅 WEEKLY PROGRAM
            SectionTitle("Weekly Program")

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {

                val programs = listOf(
                    Triple("Monday", "06:00 AM - 07:00 AM", "Morning Prayer"),
                    Triple("Tuesday", "06:00 AM - 07:00 AM", "Power of the Word"),
                    Triple("Wednesday", "06:00 AM - 07:00 AM", "Bible Study"),
                    Triple("Thursday", "06:00 AM - 07:00 AM", "Testimony Time"),
                    Triple("Friday", "06:00 AM - 07:00 AM", "Praise & Worship"),
                    Triple("Saturday", "08:00 AM - 10:00 AM", "Weekend Special"),
                    Triple("Sunday", "09:00 AM - 11:00 AM", "Live Preaching")
                )

                Column {
                    programs.forEachIndexed { index, item ->
                        ProgramRow(item)
                        if (index != programs.lastIndex) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(0.2f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📞 CONTACTS (🔥 CLONED STYLE)
            SectionTitle("Contacts")

// 🌐 SOCIAL MEDIA (images)
            ContactItem(icon = R.drawable.whatsapp,size = 35, text = "Give your Testimony")
            ContactItem(icon = R.drawable.whatsapp,size = 35, text = "Contact us")
            ContactItem(icon = R.drawable.facebook, size = 35, text = "Facebook Power of the word")
            ContactItem(icon = R.drawable.tiktok,size = 35, text = "TikTok Power of the word")
            ContactItem(icon = R.drawable.instagram,size = 30, text = "Instagram Power of the word")
            ContactItem(icon = R.drawable.youtube,size = 45, text = "YouTube Power of the word")

// ⚙️ SYSTEM ACTIONS (icons)
            ContactItem(iconVector = Icons.Default.Email, onClik = { onSettingsClick() }, text = "info@poweroftheword.com")
            ContactItem(iconVector = Icons.Default.Favorite, onClik = { onDonationClick() }, text = "Donate to Power of the Word")
            ContactItem(iconVector = Icons.Default.MenuBook, text = "The Power of the Word Story")
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ProgramRow(item: Triple<String, String, String>) {

    Column(modifier = Modifier.padding(16.dp)) {

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(item.first, fontWeight = FontWeight.Bold)
            Text(item.second, style = MaterialTheme.typography.bodySmall)
        }

        Text(
            item.third,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ContactItem(
    icon: Int? = null, // for images (social media)
    iconVector: ImageVector? = null, // for material icons,
    onClik: () -> Unit? = {},
    size: Int = 22,
    text: String
) {

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClik() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔥 ICON CONTAINER
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onClik }
                .background(
                    when (text) {
                        "info@poweroftheword.com" -> Color(0xFFFF6B6B) // 🔴 Email
                        "Donate to Power of the Word" -> Color(0xFFB36BFF) // 🟣 Donate
                        "The Power of the Word Story" -> Color(0xFF4A90E2) // 🔵 Story
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
            contentAlignment = Alignment.Center
        ) {

            when {
                icon != null -> {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(size.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                iconVector != null -> {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}