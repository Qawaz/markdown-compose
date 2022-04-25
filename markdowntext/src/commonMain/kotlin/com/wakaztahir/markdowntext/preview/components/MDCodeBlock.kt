package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import com.wakaztahir.markdowntext.editor.MathJax
import com.wakaztahir.markdowntext.editor.components.LocalPrettifyParser
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

@Composable
fun Code(
    code: String,
    lang: String? = null,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit
) {
    Surface(
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        val scroll = rememberScrollState(0)
        if (lang != "latex") {
            val isLight = MaterialTheme.colors.isLight
            val parser = LocalPrettifyParser.current
            val annotatedString = remember(code, lang, parser) {
                parseCodeAsAnnotatedString(
                    parser,
                    if (isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme(),
                    lang ?: "js",
                    code
                )
            }
            Text(
                buildAnnotatedString { modify(annotatedString) },
                modifier = Modifier
                    .horizontalScroll(scroll)
                    .padding(8.dp),
                style = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace, color = color)
            )
        } else {
            MathJax(modifier = Modifier.horizontalScroll(scroll).padding(8.dp), latex = code)
        }
    }
}

@Composable
internal fun MarkdownCodeFence(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit
) {
    // CODE_FENCE_START, FENCE_LANG, {content}, CODE_FENCE_END
    val lang = node.findChildOfType(MarkdownTokenTypes.FENCE_LANG)
    val start = node.children[2].startOffset
    val end = node.children[node.children.size - 2].endOffset
    Code(
        content.subSequence(start, end).trim().toString(),
        lang = lang?.getTextInNode(content)?.trim()?.toString(),
        modifier,
        color,
        modify = modify
    )
}