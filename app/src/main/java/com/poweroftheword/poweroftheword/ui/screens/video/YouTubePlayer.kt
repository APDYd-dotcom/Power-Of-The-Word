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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoId = remember(videoUrl) { extractYoutubeId(videoUrl) }

    if (videoId == null) return

    // 1. Initialize the WebView with strict security settings
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

                // Hardening: Prevent the WebView from accessing local system files
                allowFileAccess = false
                allowContentAccess = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }

            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }
    }

    // 2. Load the URL with Custom Headers for YouTube Validation
    DisposableEffect(videoId) {
        val embedUrl = "https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1&autoplay=1&enablejsapi=1"

        // YouTube recommends identifying your app via the Referer header.
        // Using your package name as the domain is the standard for Android apps.
        val headers = mapOf(
            "Referer" to "https://com.poweroftheword.poweroftheword",
            "Origin" to "https://www.youtube.com"
        )

        webView.loadUrl(embedUrl, headers)

        onDispose {
            webView.stopLoading()
            webView.loadUrl("about:blank") // Stop audio immediately on exit
        }
    }

    // 3. Display the view in Compose
    AndroidView(
        modifier = modifier,
        factory = { webView }
    )
}