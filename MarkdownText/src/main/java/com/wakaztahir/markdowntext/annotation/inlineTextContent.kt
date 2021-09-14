package com.wakaztahir.markdowntext.annotation

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.wakaztahir.markdowntext.preview.components.MDImage
import org.commonmark.node.Image
import org.commonmark.node.Node

internal const val ImageTag = "marker - image"
internal const val URLTag = "marker - url"

@OptIn(ExperimentalUnitApi::class)
internal fun createDefaultInlineTextContent(blocks: MutableMap<String, Node>): Map<String, InlineTextContent> {
    return mapOf(
        ImageTag to InlineTextContent(
            placeholder = Placeholder(
                TextUnit(10f, TextUnitType.Em),
                TextUnit(10f, TextUnitType.Em),
                PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                blocks[it]?.let { node ->
                    (node as? Image)?.let { imageNode ->
                        MDImage(
                            node = imageNode
                        )
                    }
                }
            }
        )
    )
}