package com.poweroftheword.poweroftheword.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.poweroftheword.poweroftheword.R

sealed class Screen(val route: String, @StringRes val titleResId: Int, val icon: ImageVector? = null) {
    object Home : Screen("home", R.string.home, Icons.Outlined.Home)
    object Videos : Screen("videos", R.string.videos, Icons.Outlined.PlayArrow)
    object Audios : Screen("audios", R.string.audio, Icons.Outlined.MusicNote)
    object Radio : Screen("radio", R.string.radio, Icons.Outlined.SettingsInputAntenna)
    object About : Screen("about", R.string.about, Icons.Outlined.Info)
    object Feed : Screen("feed", R.string.feed, Icons.Outlined.RssFeed)
    
    // Non-bottom bar screens
    object Live : Screen("live", R.string.live, Icons.Outlined.LiveTv)
    
    object LivePlayer : Screen("live_player/{videoUrl}", R.string.live) {
        fun createRoute(videoUrl: String) = "live_player/${java.net.URLEncoder.encode(videoUrl, "UTF-8")}"
    }

    object DailyWord : Screen("daily_word", R.string.daily_word, Icons.Default.Favorite)
    object Horaire : Screen("horaire", R.string.schedule, Icons.Default.DateRange)
    object Programs : Screen("programs", R.string.programs, Icons.Default.DateRange)
    object Donation : Screen("donation", R.string.donation, Icons.Default.Add)
    object Contact : Screen("contact", R.string.contact_us, Icons.Default.Call)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
    
    object VideoDetail : Screen("video_detail/{videoId}", R.string.videos) {
        fun createRoute(videoId: Int) = "video_detail/$videoId"
    }

    object FeedDetail : Screen("feed_detail/{feedId}", R.string.feed) {
        fun createRoute(feedId: Int) = "feed_detail/$feedId"
    }
}
