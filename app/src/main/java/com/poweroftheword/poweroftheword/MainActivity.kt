package com.poweroftheword.poweroftheword

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.poweroftheword.poweroftheword.ui.screens.MainScreen
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsViewModel
import com.poweroftheword.poweroftheword.ui.theme.PowerOfTheWordTheme
import com.poweroftheword.poweroftheword.util.LocalLocalizedContext
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        askNotificationPermission()
        
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val currentLanguage by settingsViewModel.currentLanguage.collectAsState()
            
            // Re-wrap the context whenever the language changes
            val context = LocalContext.current
            val localizedContext = remember(currentLanguage) {
                updateResources(context, currentLanguage)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalLocalizedContext provides localizedContext
            ) {
                PowerOfTheWordTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = when (language.uppercase()) {
            "FR" -> Locale.FRENCH
            "SW" -> Locale("sw")
            "KI", "RN" -> Locale("rn")
            else -> Locale.ENGLISH
        }
        
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        
        val localizedContext = context.createConfigurationContext(configuration)
        
        // Wrap the localized context but keep the original context as base
        // This allows Hilt to find the Activity context by traversing ContextWrappers
        return object : ContextWrapper(context) {
            override fun getResources() = localizedContext.resources
            override fun getAssets() = localizedContext.assets
            override fun getSystemService(name: String): Any? {
                return if (Context.LAYOUT_INFLATER_SERVICE == name) {
                    localizedContext.getSystemService(name)
                } else {
                    super.getSystemService(name)
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
