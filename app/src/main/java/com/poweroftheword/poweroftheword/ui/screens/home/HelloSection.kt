package com.poweroftheword.poweroftheword.ui.screens.home

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.domain.model.DailyWord

@Composable
fun DynamicHero(
    viewModel: DailyWord?
) {

    val context = LocalContext.current

    var dominantColor by remember { mutableStateOf(Color.Black) }
    var isDarkMode by remember { mutableStateOf(true) }
    var selectedLang by remember { mutableStateOf("EN - English") }
//    val state by viewModel
    val item = viewModel?.results?.firstOrNull()



    LaunchedEffect(Unit) {
        getDominantColorFromUrl(context, item?.photo) {
            dominantColor = it
        }
    }

    AppTheme(isDarkMode) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {

            AsyncImage(
                model = item?.photo,
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
                                dominantColor.copy(alpha = 0.7f),
                                Color.Black.copy(alpha = 0.25f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Power of the Word",
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        //  Language dropdown
                        ProLanguageDropdown(
                            selectedLang = selectedLang,
//                            size = 30,
                            onLangChange = { selectedLang = it }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        //  Theme toggle icon ONLY
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

//this ia for Drowable
//fun getDominantColor(
//    context: Context,
//    @DrawableRes imageRes: Int,
//    onColorReady: (Color) -> Unit
//) {
//    val bitmap = BitmapFactory.decodeResource(context.resources, imageRes)
//
//    Palette.from(bitmap).generate { palette ->
//        val color = palette?.dominantSwatch?.rgb ?: Color.Black.toArgb()
//        onColorReady(Color(color))
//    }
//}

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
    size: Int? = null,
    onLangChange: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "EN - English",
        "KI - Kirundi",
        "FR - Français",
        "SW - Swahili"
    )

    size?.dp?.let {
        Box(
            modifier = Modifier.size(it)
        ) {

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

                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = lang,
                                color = if (lang == selectedLang)
                                    Color(0xFF66FF99)
                                else Color.White
                            )
                        },
                        onClick = {
                            onLangChange(lang)
                            expanded = false
                        }
                    )
                }
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