package com.poweroftheword.poweroftheword.util

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {
    fun shareText(context: Context, text: String, title: String = "Share via") {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        if (context !is Activity) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }

    fun shareAudioFile(context: Context, file: File, title: String, message: String) {
        // Create a descriptive filename for sharing
        val cleanTitle = title.replace(Regex("[^a-zA-Z0-9]"), "_")
        val sharedFile = File(context.cacheDir, "Power_Of_The_Word_$cleanTitle.mp3")
        
        try {
            file.copyTo(sharedFile, overwrite = true)
        } catch (e: Exception) {
            // Fallback to original if copy fails
        }

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            if (sharedFile.exists()) sharedFile else file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (message.isBlank()) "audio/*" else "*/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            if (message.isNotBlank()) {
                putExtra(Intent.EXTRA_TEXT, message)
            }
            putExtra(Intent.EXTRA_SUBJECT, title)
            
            // ClipData helps some apps recognize the file and text, and is required for URI permissions in some cases
            val clip = ClipData.newRawUri(null, uri)
            if (message.isNotBlank()) {
                clip.addItem(ClipData.Item(message))
            }
            clipData = clip

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(intent, "Share Audio")
        if (context !is Activity) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }

    fun shareAudioInTwoParts(context: Context, file: File, title: String, message: String) {
        // Part 2: Prepare Audio Share (Triggered second, so it stays underneath)
        shareAudioFile(context, file, title, "")

        // Part 1: Prepare Text Share (Triggered last, so it appears on top)
        shareText(context, message, "Step 1: Share Sermon Info")
    }

    fun shareImage(context: Context, bitmap: Bitmap, title: String, message: String = "") {
        val imagesFolder = File(context.cacheDir, "images")
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                if (message.isNotBlank()) {
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                
                val clip = ClipData.newRawUri(null, uri)
                if (message.isNotBlank()) {
                    clip.addItem(ClipData.Item(message))
                }
                clipData = clip
            }

            val shareIntent = Intent.createChooser(intent, "Share Event")
            if (context !is Activity) {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

