package com.poweroftheword.poweroftheword.util

fun extractYoutubeId(url: String): String? {
    val regex = Regex("(?:v=|youtu.be/|embed/)([^&?]+)")
    return regex.find(url)?.groupValues?.get(1)
}