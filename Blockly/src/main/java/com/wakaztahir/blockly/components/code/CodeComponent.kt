package com.wakaztahir.blockly.components.code

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.wakaztahir.blockly.components.code.ace.AceEditor
import com.wakaztahir.blockly.components.code.ace.setMode
import com.wakaztahir.blockly.components.code.ace.setText
import com.wakaztahir.blockly.components.code.ace.setTheme

@Composable
fun CodeComponent(
    modifier: Modifier = Modifier,
    mode: AceEditor.Mode,
    theme: AceEditor.Theme,
    value: String = "",
    minLines: Int = 5,
    maxLines: Int = 30,
    onValueChange: AceEditor.() -> Unit = {},
) {

    val setProperties: AceEditor.() -> Unit = {
        setMode(mode)
        setTheme(theme)
        setText(value)
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