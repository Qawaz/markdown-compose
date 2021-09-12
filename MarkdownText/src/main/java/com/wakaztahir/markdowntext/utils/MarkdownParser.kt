package com.wakaztahir.markdowntext.utils

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser

internal fun createDefaultParser(): Parser {
    return Parser.builder()
        .extensions(
            listOf(
                TablesExtension.create(),
                StrikethroughExtension.create()
            )
        ).build()
}
