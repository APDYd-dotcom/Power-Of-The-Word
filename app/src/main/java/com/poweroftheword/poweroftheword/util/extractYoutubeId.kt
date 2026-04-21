package com.poweroftheword.poweroftheword.util

fun extractYoutubeId(url: String): String? {
    if (url.isBlank()) return null
    val regex = Regex("(?:v=|youtu\\.be/|embed/|live/|shorts/|v/|e/)([^&?#/ ]+)")
    return regex.find(url)?.groupValues?.get(1)
}
