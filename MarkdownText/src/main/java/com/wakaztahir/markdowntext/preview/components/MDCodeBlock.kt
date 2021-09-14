package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.highlight.theme.CodeTheme
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString
import com.wakaztahir.markdowntext.preview.LocalCodeTheme
import com.wakaztahir.markdowntext.preview.LocalCommonMarkParser
import com.wakaztahir.markdowntext.preview.LocalPrettifyParser
import org.commonmark.node.Document
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.IndentedCodeBlock

@Composable
internal fun MDFencedCodeBlock(fencedCodeBlock: FencedCodeBlock, modifier: Modifier = Modifier) {
    val padding = if (fencedCodeBlock.parent is Document) 8.dp else 0.dp
    val prettifyParser = LocalPrettifyParser.current
    val codeTheme = LocalCodeTheme.current
    Text(
        modifier = modifier.padding(bottom = padding, start = 8.dp),
        text = parseCodeAsAnnotatedString(prettifyParser, codeTheme,fencedCodeBlock.info,fencedCodeBlock.literal),
        style = TextStyle(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colors.onBackground,
    )
}

@Composable
internal fun MDIndentedCodeBlock(node: IndentedCodeBlock, modifier : Modifier = Modifier) {
    val padding = if (node.parent is Document) 8.dp else 0.dp
    val prettifyParser = LocalPrettifyParser.current
    val codeTheme = LocalCodeTheme.current
    Text(
        modifier = modifier.padding(bottom = padding, start = 8.dp),
        text = parseCodeAsAnnotatedString(prettifyParser, codeTheme,"js",node.literal),
        style = TextStyle(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colors.onBackground,
    )
}