package com.wakaztahir.markdowncompose.core

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

interface CodeExtension : Extension {

    override fun register(hashMap: HashMap<IElementType, Extension>) {
        hashMap[MarkdownElementTypes.CODE_FENCE] = this
    }

    @Composable
    override fun Render(content: String, node: ASTNode, color: Color) {
        // CODE_FENCE_START, FENCE_LANG, {content}, CODE_FENCE_END
        val lang = node.findChildOfType(MarkdownTokenTypes.FENCE_LANG)
        val start = node.children[2].startOffset
        val end = node.children[node.children.size - 2].endOffset
        Render(
            content.subSequence(start, end).trim().toString(),
            lang = lang?.getTextInNode(content)?.trim()?.toString(),
            color
        )
    }

    @Composable
    fun Render(code: String, lang: String?, color: Color) {
        Surface(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp)
        ) {
            val scroll = rememberScrollState(0)
            Text(
                text = buildAnnotatedString {
                    configuration.modify(this, buildAnnotatedString {
                        append(code)
                    })
                },
                modifier = Modifier
                    .horizontalScroll(scroll)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = color
                )
            )
        }
    }


}