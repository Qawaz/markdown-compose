package com.wakaztahir.markdowncompose.mathjax

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wakaztahir.markdowncompose.core.CodeExtension
import com.wakaztahir.markdowncompose.core.Extension
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes

abstract class MathJaxExtension(private var parentExtension: CodeExtension? = null) : CodeExtension {

    override fun register(hashMap: HashMap<IElementType, Extension>) {
        parentExtension = hashMap[MarkdownElementTypes.CODE_FENCE] as? CodeExtension
        hashMap[MarkdownElementTypes.CODE_FENCE] = this
    }

    @Composable
    override fun Render(code: String, lang: String?, color: Color) {
        if (lang == "latex") {
            MathJax(
                latex = code,
                color = color
            )
        } else if (parentExtension != null) {
            parentExtension?.Render(code, lang, color)
        } else {
            super.Render(code, lang, color)
        }
    }

}