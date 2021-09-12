package com.wakaztahir.markdowntext.core

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.wakaztahir.markdowntext.components.ImageBlockData
import com.wakaztahir.markdowntext.components.ImageComponent
import com.wakaztahir.markdowntext.model.BlockData

internal const val ImageTag = "marker - image"

@OptIn(ExperimentalUnitApi::class)
internal fun createDefaultInlineTextContent(blocks : MutableMap<String, BlockData> ): Map<String, InlineTextContent> {
    return mapOf(
        ImageTag to InlineTextContent(
            placeholder = Placeholder(
                TextUnit(10f, TextUnitType.Em),
                TextUnit(20f, TextUnitType.Em),
                PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                blocks[it]?.let { blockData ->
                    (blockData as? ImageBlockData)?.let { imageData->
                        ImageComponent(data = imageData)
                    }
                }
            }
        )
    )
}