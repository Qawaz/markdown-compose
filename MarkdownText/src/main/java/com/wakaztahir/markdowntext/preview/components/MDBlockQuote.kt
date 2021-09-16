package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.common.LocalMarker

@Composable
internal fun MDBlockQuote(
    modifier: Modifier = Modifier,
    appendContent: AnnotatedString.Builder.() -> Unit,
) {

    val color = MaterialTheme.colors.onBackground

    Box(modifier = modifier
        .drawBehind {
            drawLine(
                color = color,
                strokeWidth = 8f,
                start = Offset(36.dp.value, 0f),
                end = Offset(36.dp.value, size.height)
            )
        }
        .padding(start = 24.dp, top = 4.dp, bottom = 4.dp)) {

        val body1 = MaterialTheme.typography.body1

        val text = buildAnnotatedString {
            pushStyle(
                body1.toSpanStyle().plus(SpanStyle(fontStyle = FontStyle.Italic))
            )
            appendContent()
            pop()
            toAnnotatedString()
        }

        Text(
            text,
            modifier,
            color = MaterialTheme.colors.onBackground
        )
    }
}