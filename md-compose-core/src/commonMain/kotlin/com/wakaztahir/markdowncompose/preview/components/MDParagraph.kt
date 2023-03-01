package com.wakaztahir.markdowncompose.preview.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowncompose.core.annotation.buildMarkdownAnnotatedString
import org.intellij.markdown.ast.ASTNode

@Composable
internal fun MarkdownParagraph(
    content: String,
    node: ASTNode,
    color: Color = Color.Unspecified,
) {
    val styledText = buildAnnotatedString {
        pushStyle(MaterialTheme.typography.bodyMedium.toSpanStyle())
        buildMarkdownAnnotatedString(content, node, MaterialTheme.colorScheme)
        pop()
    }

    MDText(styledText, style = MaterialTheme.typography.bodyMedium, color = color)
}
