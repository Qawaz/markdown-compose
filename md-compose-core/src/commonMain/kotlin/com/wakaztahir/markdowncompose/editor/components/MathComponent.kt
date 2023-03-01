package com.wakaztahir.markdowncompose.editor.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock

@Composable
fun MathBlock.DefaultMathComponent(
    modifier: Modifier = Modifier,
) {
    val state = LocalEditor.current
    DefaultCodeComponent(
        modifier = modifier,
        onRemove = { state.blocks.remove(this) }
    )
}