package com.wakaztahir.markdowncompose.preview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

@Composable
internal fun MarkdownBlockQuote(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Box(modifier = modifier
        .drawBehind {
            drawLine(
                color = color,
                strokeWidth = 2f,
                start = Offset(12.dp.value, 0f),
                end = Offset(12.dp.value, size.height)
            )
        }
        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.bodyMedium.toSpanStyle().plus(SpanStyle(fontStyle = FontStyle.Italic)))
            append(node.getTextInNode(content).toString())
            pop()
        }
        Text(text, modifier, color = color)
    }
}