package com.wakaztahir.markdowntext.common

import androidx.compose.runtime.compositionLocalOf
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.markdowntext.editor.ParsedMarkdown
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser

val LocalParsedMarkdown = compositionLocalOf { ParsedMarkdown(Marker()) }

val LocalMarker = compositionLocalOf { Marker() }

internal val LocalCommonMarkParser = compositionLocalOf { createDefaultParser() }

internal val LocalPrettifyParser = compositionLocalOf { PrettifyParser() }

internal val LocalCodeTheme = compositionLocalOf { CodeThemeType.Default.theme() }


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