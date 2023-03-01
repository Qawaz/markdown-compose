package com.wakaztahir.markdowncompose.mathjax

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.wakaztahir.mathjax.JLatexMathBitmapBuilder
import com.wakaztahir.mathjax.LatexAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun MathJax(modifier: Modifier, latex: String, color: Color) {
    val context = LocalContext.current
    var latexBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    if (latexBitmap != null) {
        Image(
            modifier = modifier,
            bitmap = latexBitmap!!,
            contentDescription = null
        )
    }

    LaunchedEffect(latex) {
        withContext(Dispatchers.IO) {
            try {
                latexBitmap = JLatexMathBitmapBuilder.latexImageBitmap(
                    context = context,
                    latex = latex,
                    alignment = LatexAlignment.Start,
                    color = color.toArgb(),
                    backgroundColor = null,
                ).asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}