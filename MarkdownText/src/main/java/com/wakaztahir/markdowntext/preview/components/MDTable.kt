package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.LocalMarker
import com.wakaztahir.markdowntext.preview.MarkdownText
import com.wakaztahir.markdowntext.utils.SimpleTableLayout
import org.commonmark.ext.gfm.tables.*
import org.commonmark.node.Node

private fun extractNodes(
    node: Node,
    foundSection: (Node) -> Unit = {},
    foundRow: (TableRow) -> Unit = {},
    foundCell: (TableCell) -> Unit = {}
) {
    var child = node.firstChild
    while (child != null) {
        when (child) {
            is TableHead -> foundSection(child)
            is TableBody -> foundSection(child)
            is TableRow -> foundRow(child)
            is TableCell -> foundCell(child)
        }
        child = child.next
    }
}

@Composable
internal fun MDTable(node: TableBlock) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(width = 2.dp, color = MaterialTheme.colors.onBackground.copy(.3f))
    ) {

        // rows contain list of columns
        val rows = mutableListOf<MutableList<TableCell>>()

        extractNodes(
            node,
            foundSection = { section ->
                extractNodes(section, foundRow = { row ->
                    val list = mutableListOf<TableCell>()
                    extractNodes(row, foundCell = {
                        list.add(it)
                    })
                    rows.add(list)
                })
            },
        )

        if (rows.isNotEmpty()) {
            SimpleTableLayout(
                columns = rows.first().size,
                rows = rows,
                drawDecorations = {
                    Modifier
                },
                cellSpacing = 8f
            ) {
                MDTableCell(node = it, modifier = Modifier.padding(end = 4.dp, start = 4.dp))
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MDTableCell(
    node: TableCell,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.body1,
    color: Color = MaterialTheme.colors.onBackground,
) {

    val marker = LocalMarker.current
    val text = remember(node) {
        buildAnnotatedString {
            appendMarkdownContent(marker, node)
            toAnnotatedString()
        }
    }



    MarkdownText(
        modifier = modifier.padding(4.dp),
        text = text,
        style = style,
        color = color
    )

}
