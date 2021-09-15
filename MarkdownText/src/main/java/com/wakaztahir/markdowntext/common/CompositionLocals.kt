package com.wakaztahir.markdowntext.common

import androidx.compose.runtime.compositionLocalOf
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser

internal val LocalCommonMarkParser = compositionLocalOf { createDefaultParser() }

val LocalMarker = compositionLocalOf { Marker() }

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