package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.LocalMarker
import com.wakaztahir.markdowntext.preview.MarkdownText
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
                    var colNum = 0
                    extractNodes(row, foundCell = {
                        val currentColumn = if (colNum < rows.size) {
                            rows[colNum]
                        } else {
                            val col = mutableListOf<TableCell>()
                            rows.add(col)
                            col
                        }
                        currentColumn.add(it)
                        colNum++
                    })
                })
            },
        )

        val density = LocalDensity.current
        var tableHeight by remember { mutableStateOf(0.dp) }

        Row(modifier = Modifier.onSizeChanged {
            tableHeight = with(density) { it.height.toDp() }
        }) {
            rows.forEachIndexed { rowIndex, row ->
                Column(
                    modifier = Modifier
                        .then(
                            if (tableHeight > 0.dp) {
                                Modifier.height(tableHeight)
                            } else {
                                Modifier
                            }
                        )
                        .border(width = 1.5.dp, color = MaterialTheme.colors.onBackground.copy(.3f))
                ) {
                    row.forEachIndexed { index, tableCell ->
                        if (index == 0) {
                            // head
                            MDTableCell(
                                node = tableCell,
                                modifier = Modifier
                                    .then(
                                        if (rowIndex + 1 == rows.size) {
                                            Modifier.fillMaxWidth()
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .background(
                                        color = MaterialTheme.colors.primary
                                    )
                                    .padding(end = 4.dp,start = 4.dp),
                                color = MaterialTheme.colors.onPrimary
                            )
                        } else {
                            MDTableCell(node = tableCell,modifier = Modifier.padding(end = 4.dp,start = 4.dp))
                        }
                    }
                }
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
