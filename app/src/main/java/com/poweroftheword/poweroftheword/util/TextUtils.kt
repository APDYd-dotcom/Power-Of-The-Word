package com.poweroftheword.poweroftheword.util

/**
 * Truncates the string if it's longer than the specified [maxLength] and adds [ellipsis].
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (this.length <= maxLength) {
        this
    } else {
        this.take(maxLength).removeSuffix(" ") + ellipsis
    }
}

fun convertToYoutubeOriginalUrl(url: String): String {
    return when {
        url.contains("youtube.com/embed/") -> {
            val id = url.substringAfter("embed/").substringBefore("?").substringBefore("/")
            "https://youtu.be/$id"
        }
        url.contains("youtube.com/watch?v=") -> {
            val id = url.substringAfter("v=").substringBefore("&")
            "https://youtu.be/$id"
        }
        url.contains("youtu.be/") -> {
            val id = url.substringAfter("youtu.be/").substringBefore("?").substringBefore("/")
            "https://youtu.be/$id"
        }
        else -> url
    }
}
