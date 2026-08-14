package com.poweroftheword.poweroftheword.ui.screens.home

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.domain.model.DailyWord
import com.poweroftheword.poweroftheword.BuildConfig
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.ui.theme.LocalStatusBarAppearance
import com.poweroftheword.poweroftheword.ui.util.getDominantColorFromUrl
import com.poweroftheword.poweroftheword.util.truncate

@Composable
fun DynamicHero(
    dailyWord: DailyWord?,
    currentLanguage: String,
    isDarkMode: Boolean,
    onLanguageChange: (String) -> Unit,
    onThemeToggle: (Boolean) -> Unit,
    onShareApp: () -> Unit
) {
    val context = LocalContext.current
    val statusBarAppearance = LocalStatusBarAppearance.current

    val defaultDominantColor = if (isDarkMode) Color(0xFF12141C) else Color(0xFFF8F9FA)
    var dominantColor by remember(isDarkMode) { mutableStateOf(defaultDominantColor) }
    val item = dailyWord?.dailywords?.firstOrNull()

    // Sync status bar icons with dominant color luminance
    LaunchedEffect(dominantColor) {
        statusBarAppearance.isDarkIcons = dominantColor.luminance() > 0.5f
    }

    // Clean up when leaving this section to restore theme defaults
    DisposableEffect(Unit) {
        onDispose {
            statusBarAppearance.isDarkIcons = null
        }
    }

    LaunchedEffect(item?.photo, isDarkMode) {
        if (item?.photo != null) {
            getDominantColorFromUrl(context, "${BuildConfig.BASE_URL}${item.photo}") {
                dominantColor = it
            }
        } else {
            dominantColor = defaultDominantColor
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        AsyncImage(
            model = "${BuildConfig.BASE_URL}${item?.photo}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            dominantColor.copy(alpha = 0.1f),
                            Color.Black.copy(alpha = 0.4f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Power of the Word".truncate(20),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProLanguageDropdown(
                        selectedLang = currentLanguage,
                        onLangChange = onLanguageChange
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ThemeToggleButton(
                        isDarkMode = isDarkMode,
                        onToggle = { onThemeToggle(!isDarkMode) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onShareApp,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.share_app),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun ProLanguageDropdown(
    selectedLang: String,
    onLangChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "EN" to "English",
        "KI" to "Kirundi",
        "FR" to "Français",
        "SW" to "Swahili"
    )

    Box {
        Surface(
            onClick = { expanded = true },
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(24.dp),
            contentColor = Color.White,
            modifier = Modifier.height(40.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = selectedLang,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2B313F),
                            Color(0xFF1B1F29)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            Text(
                text = "Select Language",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            languages.forEach { (code, name) ->
                val isSelected = code == selectedLang
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) Color(0xFF66FF99) else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF66FF99),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onLangChange(code)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggle: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.25f))
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = if (isDarkMode)
                Icons.Default.DarkMode
            else
                Icons.Default.LightMode,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}
