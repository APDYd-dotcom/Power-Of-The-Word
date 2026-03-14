package com.poweroftheword.poweroftheword.util

import android.content.Context
import android.content.Intent

object ShareUtils {
    fun shareText(context: Context, text: String, title: String = "Share via") {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
