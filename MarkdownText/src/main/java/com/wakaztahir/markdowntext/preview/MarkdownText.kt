package com.wakaztahir.markdowntext.preview

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import com.wakaztahir.markdowntext.annotation.URLTag

@Composable
fun MarkdownText(
    text: AnnotatedString,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val marker = LocalMarker.current

    Text(text = text,
        modifier = modifier.pointerInput(layoutResult) {
            layoutResult?.let {
                detectTapGestures { pos ->
                    val position = it.getOffsetForPosition(pos)
                    text.getStringAnnotations(position, position)
                        .firstOrNull()
                        ?.let { sa ->
                            if (sa.tag == URLTag) {
                                uriHandler.openUri(sa.item)
                            }
                        }
                }
            }
        },
        color = MaterialTheme.colors.onBackground,
        style = style,
        inlineContent = marker.inlineContent,
        onTextLayout = {
            layoutResult = it
        },
    )
}