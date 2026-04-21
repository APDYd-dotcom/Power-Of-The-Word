package com.poweroftheword.poweroftheword.ui.util

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

suspend fun getDominantColorFromUrl(
    context: Context,
    imageUrl: String?,
    onColorReady: (Color) -> Unit
) {
    if (imageUrl.isNullOrBlank()) {
        onColorReady(Color.Transparent)
        return
    }
    
    try {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false) // Required for Palette to extract colors from Bitmap
            .build()

        val result = (loader.execute(request) as? SuccessResult)
        val bitmap = (result?.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap

        if (bitmap != null) {
            val palette = Palette.from(bitmap).generate()
            val color = palette.dominantSwatch?.rgb ?: Color.Black.toArgb()
            onColorReady(Color(color))
        } else {
            onColorReady(Color.Black)
        }
    } catch (e: Exception) {
        onColorReady(Color.Black)
    }
}
