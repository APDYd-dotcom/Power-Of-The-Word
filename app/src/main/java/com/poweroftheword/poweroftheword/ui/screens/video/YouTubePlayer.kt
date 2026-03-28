package com.poweroftheword.poweroftheword.ui.screens.video

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.poweroftheword.poweroftheword.util.extractYoutubeId

@Composable
fun YoutubePlayerComposable(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val videoId = remember(videoUrl) {
        extractYoutubeId(videoUrl)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView(context).apply {

                lifecycleOwner.lifecycle.addObserver(this)

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        videoId?.let {
                            youTubePlayer.loadVideo(it, 0f)
                        }
                    }
                })
            }
        }
    )
}
