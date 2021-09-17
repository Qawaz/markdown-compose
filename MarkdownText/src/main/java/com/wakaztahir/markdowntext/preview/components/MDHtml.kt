package com.wakaztahir.markdowntext.preview.components

import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MDHtmlInline(literal: String) {
    Text(text = literal)
}

@Suppress("Deprecation")
@Composable
fun MDHtmlBlock(literal: String) {
    AndroidView(
        factory = {
            TextView(it)
        }, update = {
            it.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(literal, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(literal)
            }
        }
    )
}