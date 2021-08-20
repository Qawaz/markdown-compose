package com.wakaztahir.blockly.components.blocks.textblock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.wakaztahir.blockly.components.blocks.textblock.markdowntext.MarkdownEditText
import com.wakaztahir.blockly.states.TextState
import com.wakaztahir.blockly.states.rememberBlocklyTextState
import com.wakaztahir.blockly.utils.toViewColor

@Composable
fun TextComponent(
    modifier: Modifier = Modifier,
    textState: TextState = rememberBlocklyTextState(),
    hint: String = "",
    color: Color = Color.White,
    hintColor: Color = Color.Gray,
    backgroundColor: Color = Color.Transparent,
    taskBoxColor: Color = Color.Yellow,
    taskBoxBackgroundColor: Color = Color.White,
    initialMarkdown: String? = null,
    onTextStateChange: (textState: TextState) -> Unit = { }
) {
    AndroidView(
        modifier = modifier,
        factory = {
            MarkdownEditText(
                it,
                textState,
                taskBoxColor.toViewColor(),
                taskBoxBackgroundColor.toViewColor()
            ).apply {
                this.addTextChangedListener(textState.undoRedoHelper)
            }
        },
        update = {
            textState.editText = it
            it.hint = hint
            it.setMarkdown(initialMarkdown ?: "")
            it.setBackgroundColor(backgroundColor.toViewColor())
            it.setTextColor(color.toViewColor())
            it.setHintTextColor(hintColor.toViewColor())
            it.setOnTextStateChangeListener(object : MarkdownEditText.TextStateListener {
                override fun onUpdate(state: TextState) {
                    onTextStateChange(state)
                }
            })
            textState.initialize()
        })
}