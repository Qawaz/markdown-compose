package com.wakaztahir.markdowncompose.codeeditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wakaztahir.markdowncompose.core.CodeExtension
import com.wakaztahir.markdowncompose.core.Extension
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes

abstract class HighlightCodeExtension : CodeExtension {
    override fun register(hashMap: HashMap<IElementType, Extension>) {
        hashMap[MarkdownElementTypes.CODE_FENCE] = this
    }
    @Composable
    override fun Render(code: String, lang: String?, color: Color) {
        Code(
            configuration = configuration,
            code = code,
            lang = lang,
            color = color
        )
    }
}