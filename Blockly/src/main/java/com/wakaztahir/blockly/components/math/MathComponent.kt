package com.wakaztahir.blockly.components.math

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.wakaztahir.blockly.components.math.mathjax.MathJaxView

@Composable
fun MathComponent(latext: String) {

    val setProperties: MathJaxView.() -> Unit = {
        setInputText(latext)
    }

    AndroidView(
        factory = {
            MathJaxView(it).apply {
                this.setRenderListener(object : MathJaxView.OnMathJaxRenderListener {
                    override fun onRendered() {
                        setProperties()
                    }
                })
            }
        },
        update = {
            setProperties(it)
        }
    )
}