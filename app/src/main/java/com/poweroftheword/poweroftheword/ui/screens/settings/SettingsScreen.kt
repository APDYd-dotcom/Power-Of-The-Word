package com.poweroftheword.poweroftheword.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToFeed: () -> Unit,
    onNavigateToDailyWord: () -> Unit,
    onNavigateToHoraire: () -> Unit,
    onNavigateToPrograms: () -> Unit,
    onNavigateToDonation: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToContact: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val languages = listOf(
        "EN" to "English",
        "FR" to "Français",
        "SW" to "Swahili",
        "RN" to "Kirundi"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle("Language")
            languages.forEach { (code, name) ->
                LanguageItem(
                    name = name,
                    selected = currentLanguage == code,
                    onClick = { viewModel.setLanguage(code) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Church Ministry")
            
            SettingsMenuItem(
                title = "Church Feed",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = onNavigateToFeed
            )
            SettingsMenuItem(
                title = "Daily Word",
                icon = Icons.Default.Favorite,
                onClick = onNavigateToDailyWord
            )
            SettingsMenuItem(
                title = "Pastor Schedule",
                icon = Icons.Default.DateRange,
                onClick = onNavigateToHoraire
            )
            SettingsMenuItem(
                title = "Church Programs",
                icon = Icons.Default.DateRange,
                onClick = onNavigateToPrograms
            )
            SettingsMenuItem(
                title = "Donations & Giving",
                icon = Icons.Default.Add,
                onClick = onNavigateToDonation
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Information")
            
            SettingsMenuItem(
                title = "About Us",
                icon = Icons.Default.Info,
                onClick = onNavigateToAbout
            )
            SettingsMenuItem(
                title = "Contact Us",
                icon = Icons.Default.Call,
                onClick = onNavigateToContact
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun LanguageItem(name: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun SettingsMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        


        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}
