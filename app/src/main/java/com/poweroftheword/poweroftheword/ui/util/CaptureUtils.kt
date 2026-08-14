package com.poweroftheword.poweroftheword.ui.util

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.launch

class CaptureController(val graphicsLayer: GraphicsLayer) {
    suspend fun capture(): Bitmap {
        return graphicsLayer.toImageBitmap().asAndroidBitmap()
    }
}

@Composable
fun rememberCaptureController(): CaptureController {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) { CaptureController(graphicsLayer) }
}

fun Modifier.capturable(controller: CaptureController): Modifier {
    return this.drawWithContent {
        controller.graphicsLayer.record {
            this@drawWithContent.drawContent()
        }
        drawLayer(controller.graphicsLayer)
    }
}
