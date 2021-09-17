package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
internal fun MDFencedCodeBlock(
    modifier: Modifier = Modifier,
    isParentDocument: Boolean,
    info: String,
    literal: String,
    fenceChar: Char,
    fenceIndent: Int,
    fenceLength: Int,
) {
    val padding = if (isParentDocument) 8.dp else 0.dp
    Text(
        modifier = modifier.padding(bottom = padding, start = 8.dp),
        text = literal,
        style = TextStyle(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colors.onBackground,
    )
}

@Composable
internal fun MDIndentedCodeBlock(
    modifier: Modifier = Modifier,
    isParentDocument: Boolean,
    literal: String,
) {
    val padding = if (isParentDocument) 8.dp else 0.dp
    Text(
        modifier = modifier.padding(bottom = padding, start = 8.dp),
        text = literal,
        style = TextStyle(fontFamily = FontFamily.Monospace),
        color = MaterialTheme.colors.onBackground,
    )
}