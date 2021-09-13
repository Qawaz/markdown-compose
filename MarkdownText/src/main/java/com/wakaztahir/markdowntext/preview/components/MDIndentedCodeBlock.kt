package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.commonmark.node.Document
import org.commonmark.node.IndentedCodeBlock

@Composable
fun MDIndentedCodeBlock(node: IndentedCodeBlock,modifier : Modifier = Modifier) {
    val padding = if (node.parent is Document) 8.dp else 0.dp
    Text(
        modifier = modifier.padding(bottom = padding, start = 8.dp),
        text = node.literal,
        style = TextStyle(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colors.onBackground,
    )
}