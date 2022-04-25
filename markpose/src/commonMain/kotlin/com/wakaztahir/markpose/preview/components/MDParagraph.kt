package com.wakaztahir.markpose.preview.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markpose.core.annotation.buildMarkdownAnnotatedString
import org.intellij.markdown.ast.ASTNode

@Composable
internal fun MarkdownParagraph(
    content: String,
    node: ASTNode,
    color: Color = Color.Unspecified,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit,
) {
    val styledText = buildAnnotatedString {
        pushStyle(MaterialTheme.typography.body1.toSpanStyle())
        buildMarkdownAnnotatedString(content, node, MaterialTheme.colors)
        pop()
    }

    MDText(styledText, style = MaterialTheme.typography.body1, color = color, modify = modify)
}
