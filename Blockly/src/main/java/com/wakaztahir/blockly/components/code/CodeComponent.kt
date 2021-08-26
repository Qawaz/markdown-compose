package com.wakaztahir.blockly.components.code

import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.wakaztahir.blockly.components.code.ace.AceEditor
import com.wakaztahir.blockly.components.code.ace.setMode
import com.wakaztahir.blockly.components.code.ace.setTheme

@Composable
fun CodeComponent(
    modifier: Modifier = Modifier,
    mode: AceEditor.Mode,
    theme: AceEditor.Theme,
    height: Int = FrameLayout.LayoutParams.MATCH_PARENT,
    onChange: () -> Unit = {},
) {

    val setProperties: AceEditor.() -> Unit = {
        setMode(mode)
        setTheme(theme)
//        layoutParams.height = height
        this.onChange = onChange
    }

    AndroidView(
        modifier = modifier,
        factory = {
            AceEditor(it, null).apply {
                onLoaded = {
                    setProperties()
                }
            }
        },
        update = {
            setProperties(it)
        }
    )
}