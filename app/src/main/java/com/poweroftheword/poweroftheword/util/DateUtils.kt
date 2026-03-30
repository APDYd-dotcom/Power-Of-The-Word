package com.poweroftheword.poweroftheword.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Formats a date string from "yyyy-MM-dd" or similar to a more readable format like "MMM dd, yyyy"
 */
fun formatDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Unknown Date"
    
    // List of possible input formats from the API
    val inputFormats = listOf(
        "yyyy-MM-dd",
        "yyyy-MM-dd HH:mm:ss",
        "dd-MM-yyyy",
        "MMMM dd, yyyy"
    )
    
    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    for (format in inputFormats) {
        try {
            val parser = SimpleDateFormat(format, Locale.getDefault())
            val date = parser.parse(dateString)
            if (date != null) {
                return outputFormat.format(date)
            }
        } catch (e: Exception) {
            // Continue to next format
        }
    }
    
    // If no format matches, return the original string or a fallback
    return dateString
}
