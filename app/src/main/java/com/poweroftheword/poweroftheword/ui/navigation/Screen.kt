package com.poweroftheword.poweroftheword.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Outlined.Home)
    object Videos : Screen("videos", "Video", Icons.Outlined.PlayArrow)
    object Audios : Screen("audios", "Audio", Icons.Outlined.MusicNote)
    object Radio : Screen("radio", "Radio", Icons.Outlined.SettingsInputAntenna)
    object About : Screen("about", "About", Icons.Outlined.Info)
    object Feed : Screen("feed", "Feed", Icons.Outlined.RssFeed)
    
    // Non-bottom bar screens
    object Live : Screen("live", "Live", Icons.Outlined.LiveTv)
    object DailyWord : Screen("daily_word", "Daily Word", Icons.Default.Favorite)
    object Horaire : Screen("horaire", "Schedule", Icons.Default.DateRange)
    object Programs : Screen("programs", "Programs", Icons.Default.DateRange)
    object Donation : Screen("donation", "Donation", Icons.Default.Add)
    object Contact : Screen("contact", "Contact Us", Icons.Default.Call)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
//    object VideoPlayer : Screen("video_player/{videoUrl}") {
//        fun createRoute(videoUrl: String) = "video_player/${java.net.URLEncoder.encode(videoUrl, "UTF-8")}"
//    }

    object VideoDetail : Screen("video_detail/{videoId}") {
        fun createRoute(videoId: Int) = "video_detail/$videoId"
    }

    object FeedDetail : Screen("feed_detail/{feedId}") {
        fun createRoute(feedId: Int) = "feed_detail/$feedId"
    }
}
