package com.poweroftheword.poweroftheword.ui.screens.radio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poweroftheword.poweroftheword.domain.model.Program
import com.poweroftheword.poweroftheword.domain.model.Radio
import com.poweroftheword.poweroftheword.ui.screens.home.ProLanguageDropdown
import com.poweroftheword.poweroftheword.ui.screens.home.ThemeToggleButton
import com.poweroftheword.poweroftheword.ui.theme.FigmaRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(
    viewModel: RadioViewModel,
    onPlayClick: (Radio) -> Unit
) {
    val radioStatus by viewModel.radioStatus.collectAsState()
   // val radioStations by viewModel.radioStations.collectAsState() // Assuming there's a list in viewModel
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedLang by remember { mutableStateOf("EN - English") }
    var isDarkMode by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Radio",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                //  Language dropdown
                                ProLanguageDropdown(
                                    selectedLang = selectedLang,
                                    onLangChange = { selectedLang = it }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                //  Theme toggle icon ONLY
                                ThemeToggleButton(
                                    isDarkMode = isDarkMode,
                                    onToggle = { isDarkMode = !isDarkMode }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
//                if (isLoading && radioStatus == null && radioStations.isEmpty()) {
                if (isLoading && radioStatus == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(120.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = 8.dp
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Radio,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(24.dp).size(64.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Radio",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        item {
                            Text(
                                text = "ALL",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Use combined list or radioStatus + stations
                        val allRadios = mutableListOf<Radio>()
                        
                        // Static test radio
                        allRadios.add(
                            Radio(
                                id = "test_radio",
                                name = "Test Radio Station",
                                streamUrl = "https://stream.radio/test", // Placeholder
                                isActive = true,
                                startHour = "00:00",
                                endHour = "23:59"
                            )
                        )

                        radioStatus?.let { allRadios.add(it) }
//                        allRadios.addAll(radioStations)

                        items(allRadios.distinctBy { it.id }) { radio ->
                            RadioStationCard(radio = radio, onPlayClick = { onPlayClick(radio) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RadioStationCard(radio: Radio, onPlayClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for Logo/Thumbnail if exists, else an icon
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (radio.isActive) {
                    Surface(
                        color = FigmaRed,
                        shape = RoundedCornerShape(percent = 50)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                Text(
                    text = radio.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "Radio Station",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "PLAY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProgramItem(program: Program) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = program.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = "${program.day} | ${program.startHour} - ${program.endHour}", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = program.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
