package com.poweroftheword.poweroftheword.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

@Composable
fun localizedString(@StringRes id: Int, vararg args: Any): String {
    val context = LocalLocalizedContext.current
    val formattedArgs = args.map {
        when (it) {
            is Float -> it.clean()
            is Double -> it.clean()
            else -> it
        }
    }.toTypedArray()
    return context.getString(id, *formattedArgs)
}

fun Float.clean(): String {
    return if (this % 1.0f == 0.0f) this.toLong().toString() else this.toString()
}

fun Double.clean(): String {
    return if (this % 1.0 == 0.0) this.toLong().toString() else this.toString()
}
