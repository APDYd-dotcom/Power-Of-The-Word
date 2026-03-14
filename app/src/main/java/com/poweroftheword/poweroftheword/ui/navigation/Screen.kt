package com.poweroftheword.poweroftheword.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Videos : Screen("videos", "Videos", Icons.Default.PlayArrow)
    object Audios : Screen("audios", "Audios", Icons.Default.Menu)
    object Live : Screen("live", "Live", Icons.Default.Refresh)
    object Radio : Screen("radio", "Radio", Icons.Default.Notifications)
    object Feed : Screen("feed", "Feed", Icons.Default.List)
    object DailyWord : Screen("daily_word", "Daily Word", Icons.Default.Favorite)
    object Horaire : Screen("horaire", "Schedule", Icons.Default.DateRange)
    object Donation : Screen("donation", "Donation", Icons.Default.Add)
    object About : Screen("about", "About Us", Icons.Default.Info)
    object Contact : Screen("contact", "Contact Us", Icons.Default.Call)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    object VideoPlayer : Screen("video_player/{videoUrl}") {
        fun createRoute(videoUrl: String) = "video_player/${java.net.URLEncoder.encode(videoUrl, "UTF-8")}"
    }

    object FeedDetail : Screen("feed_detail/{feedId}") {
        fun createRoute(feedId: String) = "feed_detail/$feedId"
    }
}
