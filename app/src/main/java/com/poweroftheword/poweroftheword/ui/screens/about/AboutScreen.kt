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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.ui.components.AboutScreenSkeleton
import com.poweroftheword.poweroftheword.util.localizedString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onDonationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: PastorViewModel = hiltViewModel()
) {

    val scrollState = rememberScrollState()
    val pastors by viewModel.pastor.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val pastor = pastors.firstOrNull()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        localizedString(R.string.about),
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

        if (isLoading) {
            Box(modifier = Modifier.padding(padding)) {
                AboutScreenSkeleton()
            }
        } else {
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

                    if (pastor != null) {
                        AsyncImage(
                            model = pastor.photo,
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.dailword1),
                            error = painterResource(id = R.drawable.dailword1)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.dailword1),
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = pastor?.fullName ?: localizedString(R.string.pastor_name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = localizedString(R.string.pastor_title),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 📝 DESCRIPTION
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                    Text(
                        localizedString(R.string.about_motto),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = pastor?.bio ?: localizedString(R.string.about_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 📅 WEEKLY PROGRAM
                SectionTitle(localizedString(R.string.weekly_program))

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
                        Triple(localizedString(R.string.monday), "06:00 AM - 07:00 AM", localizedString(R.string.morning_prayer)),
                        Triple(localizedString(R.string.tuesday), "06:00 AM - 07:00 AM", localizedString(R.string.power_of_the_word)),
                        Triple(localizedString(R.string.wednesday), "06:00 AM - 07:00 AM", localizedString(R.string.bible_study)),
                        Triple(localizedString(R.string.thursday), "06:00 AM - 07:00 AM", localizedString(R.string.testimony_time)),
                        Triple(localizedString(R.string.friday), "06:00 AM - 07:00 AM", localizedString(R.string.praise_worship)),
                        Triple(localizedString(R.string.saturday), "08:00 AM - 10:00 AM", localizedString(R.string.weekend_special)),
                        Triple(localizedString(R.string.sunday), "09:00 AM - 11:00 AM", localizedString(R.string.live_preaching))
                    )

                    Column {
                        programs.forEachIndexed { index, item ->
                            ProgramRow(item)
                            if (index != programs.lastIndex) {
                                HorizontalDivider(
                                    Modifier,
                                    DividerDefaults.Thickness,
                                    color = MaterialTheme.colorScheme.outline.copy(0.2f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 📞 CONTACTS (🔥 CLONED STYLE)
                SectionTitle(localizedString(R.string.contacts))

    // 🌐 SOCIAL MEDIA (images)
                ContactItem(icon = R.drawable.whatsapp,size = 35, text = localizedString(R.string.give_testimony))
                ContactItem(icon = R.drawable.whatsapp,size = 35, text = localizedString(R.string.contact_us))
                ContactItem(icon = R.drawable.facebook, size = 35, text = localizedString(R.string.facebook_page))
                ContactItem(icon = R.drawable.tiktok,size = 35, text = localizedString(R.string.tiktok_page))
                ContactItem(icon = R.drawable.instagram,size = 30, text = localizedString(R.string.instagram_page))
                ContactItem(icon = R.drawable.youtube,size = 45, text = localizedString(R.string.youtube_page))

    // ⚙️ SYSTEM ACTIONS (icons)
                ContactItem(iconVector = Icons.Default.Email, onClik = { onSettingsClick() }, text = pastor?.email ?: "info@poweroftheword.com")
                ContactItem(iconVector = Icons.Default.Favorite, onClik = { onDonationClick() }, text = localizedString(R.string.donate_power_word))
                ContactItem(iconVector = Icons.Default.MenuBook, text = localizedString(R.string.power_word_story))
                Spacer(modifier = Modifier.height(40.dp))
            }
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
            Text(item.first, fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.onBackground)
            Text(item.second, style = MaterialTheme.typography.bodySmall,color = MaterialTheme.colorScheme.onBackground)
        }

        Text(
            item.third,
            color = MaterialTheme.colorScheme.onBackground,
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
                    when {
                        text.contains("info@") || text.contains("@") -> Color(0xFFFF6B6B) // 🔴 Email
                        text.contains("Donate") || text.contains("Dons") || text.contains("Changia") || text.contains("Shigikira") -> Color(0xFFB36BFF) // 🟣 Donate
                        text.contains("Story") || text.contains("histoire") || text.contains("Hadithi") || text.contains("Amakuru") -> Color(0xFF4A90E2) // 🔵 Story
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
