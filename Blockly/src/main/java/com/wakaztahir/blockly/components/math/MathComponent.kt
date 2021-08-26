package com.wakaztahir.blockly.components.math

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.wakaztahir.blockly.components.math.mathjax.MathJaxView

@Composable
fun MathComponent(
    latext: String,
    color: Color = MaterialTheme.colors.onBackground
) {

    val setProperties: MathJaxView.() -> Unit = {
        setInputText(latext)
        setTexColor("rgba(${color.red * 255},${color.green * 255},${color.blue * 255},${color.alpha})")
    }

    AndroidView(
        factory = {
            MathJaxView(it).apply {
                setOnLoadedListener {
                    setProperties()
                }
                setOnRenderedListener {
                    Log.d("BL_MathComponent", "Math Rendered")
                }
            }
        },
        update = {
            setProperties(it)
        }
    )
}