package com.wakaztahir.markdowntext.preview.annotation

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.wakaztahir.markdowntext.preview.components.MDImage

internal const val ImageTag = "marker - image"
internal const val URLTag = "marker - url"

open class InlineBlock

class InlineImage(val title: String, val destination: String) : InlineBlock()

@OptIn(ExperimentalUnitApi::class)
internal fun createDefaultInlineTextContent(blocks: MutableMap<String, InlineBlock>): Map<String, InlineTextContent> {
    return mapOf(
        ImageTag to InlineTextContent(
            placeholder = Placeholder(
                TextUnit(10f, TextUnitType.Em),
                TextUnit(10f, TextUnitType.Em),
                PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                blocks[it]?.let { inlineBlock ->
                    (inlineBlock as? InlineImage)?.let { inlineImage ->
                        MDImage(
                            title = inlineImage.title,
                            destination = inlineImage.destination
                        )
                    }
                }
            }
        )
    )
}