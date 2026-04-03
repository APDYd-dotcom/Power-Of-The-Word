package com.poweroftheword.poweroftheword.ui.screens.home

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
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
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.domain.model.DailyWord
import com.poweroftheword.poweroftheword.BuildConfig

@Composable
fun DynamicHero(
    dailyWord: DailyWord?,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {

    val context = LocalContext.current
    val view = LocalView.current

    var dominantColor by remember { mutableStateOf(Color.Black) }
    var isDarkMode by remember { mutableStateOf(true) }
    val item = dailyWord?.dailywords?.firstOrNull()

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = dominantColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = 
                dominantColor.luminance() > 0.5f
        }
    }

    LaunchedEffect(item?.photo) {
        if (item?.photo != null) {
            getDominantColorFromUrl(context, "${BuildConfig.BASE_URL}${item.photo}") {
                dominantColor = it
            }
        }
    }

    AppTheme(isDarkMode) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
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
                                Color.Black.copy(alpha = 0.1f)
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
                        text = "Power of the Word",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        //  Language dropdown connected to ViewModel
                        ProLanguageDropdown(
                            selectedLang = currentLanguage,
                            onLangChange = onLanguageChange
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        //  Theme toggle icon
                        ThemeToggleButton(
                            isDarkMode = isDarkMode,
                            onToggle = { isDarkMode = !isDarkMode }
                        )
                    }
                }
            }
        }
    }
}

suspend fun getDominantColorFromUrl(
    context: Context,
    imageUrl: String?,
    onColorReady: (Color) -> Unit
) {
    try {
        val loader = coil.ImageLoader(context)

        val request = coil.request.ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false) // ⚠️ VERY IMPORTANT for Palette
            .build()

        val result = (loader.execute(request) as coil.request.SuccessResult)

        val bitmap = (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap

        val palette = Palette.from(bitmap).generate()
        val color = palette.dominantSwatch?.rgb ?: Color.Black.toArgb()

        onColorReady(Color(color))

    } catch (e: Exception) {
        onColorReady(Color.Black)
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
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.25f))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(selectedLang, color = Color.White)

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E1E1E))
        ) {

            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            color = if (code == selectedLang)
                                Color(0xFF66FF99)
                            else Color.White
                        )
                    },
                    onClick = {
                        onLangChange(code)
                        expanded = false
                    }
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

@Composable
fun AppTheme(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme(),
        content = content
    )
}
