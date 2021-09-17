package com.wakaztahir.markdowntext.common

import androidx.compose.runtime.compositionLocalOf
import com.wakaztahir.markdowntext.editor.ParsedMarkdown
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser


internal val LocalCommonMarkParser = compositionLocalOf { createDefaultParser() }

/**
 * Utility function for creating default common mark parser
 */
internal fun createDefaultParser(): Parser {
    return Parser.builder()
        .extensions(
            listOf(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListItemsExtension.create(),
            )
        ).build()
}