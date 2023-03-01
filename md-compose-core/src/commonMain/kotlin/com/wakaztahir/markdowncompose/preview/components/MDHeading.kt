package com.wakaztahir.markdowncompose.preview.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.core.annotation.buildMarkdownAnnotatedString
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

@Composable
internal fun MarkdownHeader(
    content: String,
    node: ASTNode,
    style: TextStyle = LocalTextStyle.current,
    color: Color
) {
    node.findChildOfType(MarkdownTokenTypes.ATX_CONTENT)?.let {

        val text = buildAnnotatedString {
            buildMarkdownAnnotatedString(
                content,
                it.children.filter { it.type != MarkdownTokenTypes.EOL },
                MaterialTheme.colorScheme
            )
        }

        MDText(
            text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            style = style,
            color = color
        )
    }
}
