package com.poweroftheword.poweroftheword.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FigmaBrightBlue,
    secondary = FigmaGreen,
    tertiary = FigmaPurple,
    background = Color(0xFF12141C),
    surface = Color(0xFF1E232E),
    surfaceVariant = Color(0xFF2B313F),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB),
    onSurfaceVariant = FigmaGray,
    outline = Color(0xFF3F4451),
    outlineVariant = Color(0xFF2B313F)
)

//private val DarkColorScheme = darkColorScheme(
//    primary = WebPrimaryDark,
//    secondary = WebRed,
//    tertiary = WebAccentDark,
//    background = WebBackgroundDark,
//    surface = WebCardDark,
//    onPrimary = WebBackgroundLight,
//    onSecondary = Color.White,
//    onBackground = WebTextDark,
//    onSurface = WebTextDark,
//    surfaceVariant = WebCardDark,
//    onSurfaceVariant = WebGrayText
//)

private val LightColorScheme = lightColorScheme(
    primary = FigmaBrightBlue,
    secondary = FigmaGreen,
    tertiary = FigmaPurple,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F3F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E),
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6CF)
)

//private val LightColorScheme = lightColorScheme(
//    primary = WebPrimaryLight,
//    secondary = WebRed,
//    tertiary = WebAccentLight,
//    background = WebBackgroundLight,
//    surface = WebCardLight,
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onBackground = WebTextLight,
//    onSurface = WebTextLight,
//    surfaceVariant = WebCardLight,
//    onSurfaceVariant = WebGrayText
//)

@Composable
fun PowerOfTheWordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Note: HomeScreen handles its own status bar transparency for the immersive header
            if (view.context is Activity) {
                 window.statusBarColor = Color.Transparent.toArgb()
                 WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
