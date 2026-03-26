package com.poweroftheword.poweroftheword.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsDropdown() {

    var expanded by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf("English") }
    var isDarkMode by remember { mutableStateOf(true) }

    Box {

        // 🔹 Trigger Button (Profile / Icon)
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color(0xFF2C2C2C))
        ) {

            // 🌐 Languages
            listOf("English", "French", "Kirundi", "Kiswahili").forEach { lang ->

                DropdownMenuItem(
                    text = {
                        Text(
                            lang,
                            color = if (lang == selectedLang) Color.Green else Color.White
                        )
                    },
                    onClick = {
                        selectedLang = lang
                        expanded = false
                    }
                )
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.Gray)

            //  Dark / Light Mode Toggle
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            if (isDarkMode) "Dark Mode" else "Light Mode",
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it }
                        )
                    }
                },
                onClick = { isDarkMode = !isDarkMode }
            )
        }
    }
}