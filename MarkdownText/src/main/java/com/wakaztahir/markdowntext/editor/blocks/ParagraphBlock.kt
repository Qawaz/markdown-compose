package com.wakaztahir.markdowntext.editor.blocks

import androidx.compose.runtime.Composable
import com.wakaztahir.markdowntext.editor.EditableMarkdown
import org.commonmark.node.Paragraph

@Composable
fun ParagraphBlock(node: Paragraph) {
    EditableMarkdown(node = node)
}