package com.wakaztahir.markdowncompose.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode

interface Extension {

    val configuration: MarkdownPreviewConfiguration

    fun register(hashMap: HashMap<IElementType, Extension>)

    @Composable
    fun Render(content: String, node: ASTNode, color: Color)

}