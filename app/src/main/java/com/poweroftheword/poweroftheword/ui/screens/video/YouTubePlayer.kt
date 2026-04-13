package com.poweroftheword.poweroftheword.ui.screens.video

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.poweroftheword.poweroftheword.util.extractYoutubeId

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubePlayerComposable(
    videoUrl: String,
    modifier: Modifier = Modifier,
    onVideoStarted: () -> Unit = {}
) {
    val context = LocalContext.current
    val videoId = remember(videoUrl) { extractYoutubeId(videoUrl) }
    var hasRecordedView by remember(videoUrl) { mutableStateOf(false) }

    if (videoId == null) return

    // Standard YouTube embed HTML to ensure reliable rendering
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { margin: 0; padding: 0; background-color: black; }
                .video-container { position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; }
                .video-container iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }
            </style>
        </head>
        <body>
            <div class="video-container">
                <iframe src="https://www.youtube.com/embed/$videoId?autoplay=1&modestbranding=1&rel=0&enablejsapi=1" 
                        frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                        allowfullscreen></iframe>
            </div>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                // Set background to black
                setBackgroundColor(Color.BLACK)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mediaPlaybackRequiresUserGesture = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (!hasRecordedView) {
                            onVideoStarted()
                            hasRecordedView = true
                        }
                    }
                }
                webChromeClient = WebChromeClient()
            }
        },
        update = { webView ->
            // Use loadDataWithBaseURL to allow the iframe to load correctly
            webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
        }
    )
}
