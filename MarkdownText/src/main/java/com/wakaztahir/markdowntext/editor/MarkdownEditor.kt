package com.wakaztahir.markdowntext.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.common.LocalParsedMarkdown
import com.wakaztahir.markdowntext.editor.blocks.*
import com.wakaztahir.markdowntext.editor.model.*

@Composable
fun MarkdownEditor(
    modifier: Modifier = Modifier,
    parsed: ParsedMarkdown,
) {
    CompositionLocalProvider(LocalParsedMarkdown provides parsed) {
        CompositionLocalProvider(LocalMarker provides parsed.marker) {
            Column(modifier = modifier) {
                parsed.items.forEach { block ->
                    MDEditableBlock(block = block)
                }
            }
        }
    }
}

/**
 * Render a markdown editor with parsed markdown in a lazy list
 * Use these providers above LazyColumn
 * Use [CompositionLocalProvider] to provide [LocalParsedMarkdown] to render editable text components
 * Use [CompositionLocalProvider] to provide [LocalMarker] so your styles get applied according to your theme
 */
fun LazyListScope.markdownEditor(parsed: ParsedMarkdown) {
    items(parsed.items) { block ->
        MDEditableBlock(block = block)
    }
}

@Composable
private fun MDEditableBlock(block: EditableBlock) {
    when (block) {
        is TextBlock -> block.EditableTextBlock()
        is CodeBlock -> block.EditableCode()
        is QuoteBlock -> block.EditableQuote()
        is ListBlock -> block.EditableList()
        is ImageBlock -> block.EditableImage()
    }
}

