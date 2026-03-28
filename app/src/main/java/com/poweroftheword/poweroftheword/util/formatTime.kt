package com.poweroftheword.poweroftheword.util

fun formatTime(ms: Float): String {
    if (ms.isNaN() || ms <= 0f) return "00:00"

    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d", minutes, seconds)
}