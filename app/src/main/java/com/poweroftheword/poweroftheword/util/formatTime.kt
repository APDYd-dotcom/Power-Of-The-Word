package com.poweroftheword.poweroftheword.util

fun formatTime(ms: Float): String {
    if (ms.isNaN() || ms <= 0f) return "00:00"

    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60

    return if (hours > 0) {
        val remainingMinutes = minutes % 60
        String.format("%02d:%02d:%02d", hours, remainingMinutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun formatTime(timeString: String?): String {
    if (timeString.isNullOrBlank()) return "00:00"
    
    // If the string is actually a number (milliseconds), format it using the Float version
    val ms = timeString.toFloatOrNull()
    if (ms != null) {
        return formatTime(ms)
    }
    
    // Otherwise, assume it's already a formatted time string (like "01:20:00" or "15:30")
    // and return it as is, or you could add more complex parsing here if needed.
    return timeString
}
