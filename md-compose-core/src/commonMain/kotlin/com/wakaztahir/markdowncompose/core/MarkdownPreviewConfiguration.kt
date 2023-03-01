package com.wakaztahir.markdowncompose.core

import androidx.compose.ui.text.AnnotatedString
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

class MarkdownPreviewConfiguration(
    val parser: MarkdownParser = MarkdownParser(GFMFlavourDescriptor()),
    val referenceLinkHandler: ReferenceLinkHandler = ReferenceLinkHandlerImpl(),
    val modify: AnnotatedString.Builder.(AnnotatedString) -> Unit = { append(it) },
    val openLinksOnClick: Boolean = true
) {

    private val _extensions: HashMap<IElementType, Extension> = hashMapOf()
    val extensions: Map<IElementType, Extension> get() = _extensions

    init {
        register(
            object : CodeExtension {
                override val configuration: MarkdownPreviewConfiguration
                    get() = this@MarkdownPreviewConfiguration
            }
        )
    }

    fun register(extension: Extension) {
        extension.register(_extensions)
    }

}