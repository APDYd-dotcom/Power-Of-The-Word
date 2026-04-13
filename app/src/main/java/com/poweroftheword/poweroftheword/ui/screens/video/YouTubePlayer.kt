package com.poweroftheword.poweroftheword.ui.screens.video

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxWidth
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

    val webView = remember {
        WebView(context).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setBackgroundColor(Color.TRANSPARENT)
            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                mediaPlaybackRequiresUserGesture = false

                // Important
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
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
    }

    DisposableEffect(videoId) {

        val embedUrl =
            "https://www.youtube.com/embed/$videoId?playsinline=1&autoplay=1"

        // ✅ Your requested headers
        val headers = mapOf(
            "Referer" to "https://www.youtube.com"
        )

        webView.loadUrl(embedUrl, headers)

        onDispose {
            webView.stopLoading()
            webView.loadUrl("about:blank")
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        factory = { webView }
    )
}