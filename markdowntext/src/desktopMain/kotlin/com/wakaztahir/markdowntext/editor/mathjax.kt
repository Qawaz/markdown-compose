package com.wakaztahir.markdowntext.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.wakaztahir.markdowntext.utils.toAwtColor
import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXFormula
import org.scilab.forge.jlatexmath.TeXIcon
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.Icon


@Composable
actual fun MathJax(modifier: Modifier, latex: String) {

    val latexColor = MaterialTheme.colors.onBackground
    var latexImage by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(latex, latexColor) {
        kotlin.runCatching {
            val tf = TeXFormula(latex)
            tf.setColor(latexColor.toAwtColor())
            val ti: TeXIcon = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, 48F)
            latexImage = createBitmap(ti).toComposeImageBitmap()
        }.onFailure {
            it.printStackTrace()
        }
    }

    latexImage?.let {
        Image(
            modifier = Modifier.fillMaxWidth(),
            bitmap = it,
            contentDescription = null
        )
    }
}

fun createBitmap(icon: Icon): BufferedImage {
    val bi = BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_4BYTE_ABGR)
    val g: Graphics2D = bi.createGraphics()
    g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER)
    icon.paintIcon(null, g, 0, 0)
    g.dispose()
    return bi
}