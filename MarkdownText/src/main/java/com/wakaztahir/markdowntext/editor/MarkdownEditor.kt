package com.wakaztahir.markdowntext.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.editor.blocks.*
import org.commonmark.node.*

@Composable
fun MarkdownEditor(
    modifier : Modifier = Modifier,
    parsed: ParsedMarkdown,
) {
    CompositionLocalProvider(LocalMarker provides parsed.marker) {
        Column(modifier = modifier) {
            parsed.items.forEach { node ->
                EditableBlock(node = node)
            }
        }
    }
}

/**
 * Render a markdown editor with parsed markdown in a lazy list
 * Use [CompositionLocalProvider] to provide [LocalMarker] so your styles get applied according to your theme
 */
fun LazyListScope.markdownEditor(
    parsed: ParsedMarkdown,
) {
    items(parsed.items) { node ->
        EditableBlock(node = node)
    }
}

@Composable
internal fun EditableBlock(node: Node) {
    when (node) {
        is Heading -> HeadingBlock(node)
        is Paragraph -> ParagraphBlock(node)
        is FencedCodeBlock -> FencedCodeBlock(node)
        is IndentedCodeBlock -> IndentedCodeBlock(node)
        is Image -> ImageBlock(node)
        is BulletList -> BulletListBlock(node)
        is OrderedList -> OrderedListBlock(node)
    }
}

