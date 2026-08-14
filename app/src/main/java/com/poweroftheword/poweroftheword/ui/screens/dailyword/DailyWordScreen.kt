package com.poweroftheword.poweroftheword.ui.screens.dailyword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.ui.theme.LocalStatusBarAppearance
import com.poweroftheword.poweroftheword.ui.util.getDominantColorFromUrl
import com.poweroftheword.poweroftheword.util.ShareUtils
import com.poweroftheword.poweroftheword.util.LocalizationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWordScreen(
    viewModel: DailyWordViewModel,
    onBackClick: () -> Unit = {}
) {

    val state by viewModel.state.collectAsState()
    val item = state.data?.firstOrNull()
    val context = LocalContext.current
    val statusBarAppearance = LocalStatusBarAppearance.current

    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val defaultDominantColor = if (isDarkTheme) Color(0xFF12141C) else Color(0xFFF8F9FA)
    var dominantColor by remember(isDarkTheme) { mutableStateOf(defaultDominantColor) }

    // Sync status bar icons with dominant color luminance
    LaunchedEffect(dominantColor) {
        statusBarAppearance.isDarkIcons = dominantColor.luminance() > 0.5f
    }

    // Reset status bar on leave
    DisposableEffect(Unit) {
        onDispose {
            statusBarAppearance.isDarkIcons = null
        }
    }

    LaunchedEffect(item?.photo, isDarkTheme) {
        if (item?.photo != null) {
            getDominantColorFromUrl(context, "${BuildConfig.BASE_URL}${item.photo}") {
                dominantColor = it
            }
        } else {
            dominantColor = defaultDominantColor
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Devotion", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        val lang = viewModel.currentLanguage.value
                        val shareMessage = LocalizationUtils.getLocalizedString(context, R.string.share_app_message, lang)
                        ShareUtils.shareText(
                            context = context,
                            text = shareMessage
                        )
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            if (state.isLoading && item == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (item == null) {
                Text(
                    text = "No daily word available.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                        AsyncImage(
                            model = "${BuildConfig.BASE_URL}${item.photo}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Gradient overlay for better text readability if we put text on it
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                        startY = 600f
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Text(
                                text = item!!.date,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Word of the Day",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = item.language,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        HorizontalDivider(
                            modifier = Modifier.width(60.dp),
                            thickness = 4.dp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Be blessed and share this word with someone today.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
