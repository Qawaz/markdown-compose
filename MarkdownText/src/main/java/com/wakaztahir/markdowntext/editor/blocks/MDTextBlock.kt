package com.wakaztahir.markdowntext.editor.blocks

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.wakaztahir.markdowntext.editor.EditableMarkdown
import com.wakaztahir.markdowntext.editor.model.HeadingBlock
import com.wakaztahir.markdowntext.editor.model.ParagraphBlock
import com.wakaztahir.markdowntext.editor.model.TextBlock

@Composable
fun TextBlock.EditableTextBlock() {
    when (val block = this) {
        is HeadingBlock -> MDHeadingBlock(block = block)
        is ParagraphBlock -> MDParagraphBlock(block)
        else -> {
            EditableMarkdown(
                block.textValue,
                onUpdate = {
                    block.textValue = it
                }
            )
        }
    }
}

@Composable
private fun MDHeadingBlock(block: HeadingBlock) {
    EditableMarkdown(
        block.textValue,
        onUpdate = {
            block.textValue = it
        },
        textStyle = when (block.level) {
            1 -> MaterialTheme.typography.h1
            2 -> MaterialTheme.typography.h2
            3 -> MaterialTheme.typography.h3
            4 -> MaterialTheme.typography.h4
            5 -> MaterialTheme.typography.h5
            6 -> MaterialTheme.typography.h6
            else -> LocalTextStyle.current
        }
    )
}

@Composable
private fun MDParagraphBlock(block: ParagraphBlock) {
    EditableMarkdown(
        block.textValue,
        onUpdate = {
            block.textValue = it
        }
    )
}