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
