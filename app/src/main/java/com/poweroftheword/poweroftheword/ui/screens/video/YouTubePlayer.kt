package com.poweroftheword.poweroftheword.ui.screens.video

import android.annotation.SuppressLint
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

    // Initialize the WebView with strict security settings
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = false
                allowContentAccess = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // When the page is loaded, we count it as a view start
                    if (!hasRecordedView) {
                        onVideoStarted()
                        hasRecordedView = true
                    }
                }
            }
            webChromeClient = WebChromeClient()
        }
    }

    // Load the URL
    DisposableEffect(videoId) {
        val embedUrl = "https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1&autoplay=1&enablejsapi=1"

        val headers = mapOf(
            "Referer" to "https://com.poweroftheword.poweroftheword",
            "Origin" to "https://www.youtube.com"
        )

        webView.loadUrl(embedUrl, headers)

        onDispose {
            webView.stopLoading()
            webView.loadUrl("about:blank")
        }
    }

    // Display the view in Compose
    AndroidView(
        modifier = modifier,
        factory = { webView }
    )
}