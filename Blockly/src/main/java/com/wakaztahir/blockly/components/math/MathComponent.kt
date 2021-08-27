package com.wakaztahir.blockly.components.math

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.wakaztahir.blockly.components.math.mathjax.MathJaxView
import com.wakaztahir.blockly.model.MathBlock

@Composable
fun MathComponent(
    modifier: Modifier = Modifier,
    block: MathBlock,
    color: Color = MaterialTheme.colors.onBackground
) = MathComponent(
    modifier = modifier,
    latex = block.latex,
    color = color,
)

@Composable
fun MathComponent(
    modifier : Modifier = Modifier,
    latex: String,
    color: Color = MaterialTheme.colors.onBackground
) {

    val setProperties: MathJaxView.() -> Unit = {
        setInputText(latex)
        setTexColor("rgba(${color.red * 255},${color.green * 255},${color.blue * 255},${color.alpha})")
    }

    AndroidView(
        modifier = modifier,
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