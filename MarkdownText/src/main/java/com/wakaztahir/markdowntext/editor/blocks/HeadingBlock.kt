package com.wakaztahir.markdowntext.editor.blocks

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.wakaztahir.markdowntext.editor.EditableMarkdown
import org.commonmark.node.Heading

@Composable
internal fun HeadingBlock(node: Heading) {

    Row {
        // todo Render heading manipulation thing
        EditableMarkdown(node = node)
    }

}