package com.poweroftheword.poweroftheword.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
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

/**
 * Manages status bar icon appearance across the app.
 * isDarkIcons: true = Black icons (for light backgrounds), false = White icons (for dark backgrounds), null = Use theme default.
 */
class StatusBarAppearance {
    var isDarkIcons by mutableStateOf<Boolean?>(null)
}

val LocalStatusBarAppearance = staticCompositionLocalOf { StatusBarAppearance() }

@Composable
fun PowerOfTheWordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    useDarkStatusBar: Boolean? = null,
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
    val context = LocalContext.current
    val statusBarAppearance = remember { StatusBarAppearance() }
    
    // Icon appearance priority: 
    // 1. Explicit theme parameter override (useDarkStatusBar)
    // 2. Component-level override (statusBarAppearance.isDarkIcons)
    // 3. Global theme default (!darkTheme)
    val isAppearanceLightStatusBars = when {
        useDarkStatusBar != null -> !useDarkStatusBar
        statusBarAppearance.isDarkIcons != null -> statusBarAppearance.isDarkIcons!!
        else -> !darkTheme
    }

    if (!view.isInEditMode) {
        // Apply status bar and navigation bar visibility
        LaunchedEffect(isAppearanceLightStatusBars, darkTheme) {
            val activity = context.findActivity() ?: return@LaunchedEffect
            val window = activity.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            
            // Edge-to-edge transparency
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            insetsController.isAppearanceLightStatusBars = isAppearanceLightStatusBars
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalStatusBarAppearance provides statusBarAppearance) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
