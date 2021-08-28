package com.wakaztahir.blockly.components.code

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.wakaztahir.blockly.components.code.ace.*
import com.wakaztahir.blockly.model.CodeBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeComponent(
    modifier: Modifier = Modifier,
    block: CodeBlock,
    theme: AceEditor.Theme,
    showContextMenu: Boolean = false,
    minLines: Int = 5,
    maxLines: Int = 30,
    onUpdate: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    CodeComponent(
        modifier = modifier,
        mode = block.mode,
        theme = theme,
        value = block.value,
        showContextMenu = showContextMenu,
        minLines = minLines,
        maxLines = maxLines,
        onValueChange = {
            scope.launch(Dispatchers.Main) {
                scope.coroutineContext.cancelChildren()
                requestText {
                    block.value = it
                }
                launch(Dispatchers.IO) {
                    delay(300)
                    onUpdate()
                }
            }
        }
    )
}

@Composable
fun CodeComponent(
    modifier: Modifier = Modifier,
    mode: AceEditor.Mode,
    theme: AceEditor.Theme,
    value: String = "",
    showContextMenu: Boolean = false,
    minLines: Int = 5,
    maxLines: Int = 30,
    onValueChange: AceEditor.() -> Unit = {},
) {

    val setProperties: AceEditor.() -> Unit = {
        setMode(mode)
        setTheme(theme)
        setText(value)
        if (showContextMenu) {
            showMenu()
        } else {
            hideMenu()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            AceEditor(it, minLines = minLines, maxLines = maxLines).apply {
                onLoaded = {
                    setProperties()
                }
                onChange = {
                    onValueChange()
                }
            }
        },
        update = {
            setProperties(it)
        }
    )
}