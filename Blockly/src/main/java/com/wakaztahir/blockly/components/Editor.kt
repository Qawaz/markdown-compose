package com.wakaztahir.blockly.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.wakaztahir.blockly.states.EditorState
import com.wakaztahir.blockly.states.TextState

val LocalEditor = staticCompositionLocalOf { EditorState(text = TextState()) }

@Composable
fun Editor(
    state: EditorState = remember { EditorState(TextState()) },
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalEditor provides state) {
        content()
    }
}