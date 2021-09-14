package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.LocalMarker
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
            .padding(16.dp)
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

        Row {
            rows.forEachIndexed { rowIndex, row->
                Column {
                    row.forEachIndexed { index, tableCell ->
                        if (index == 0) {
                            // head
                            MDTableCell(
                                node = tableCell,
                                modifier = Modifier.then(
                                    if(rowIndex+1 == rows.size){
                                        Modifier.fillMaxWidth()
                                    }else{
                                        Modifier
                                    }
                                ).background(
                                    color = MaterialTheme.colors.onBackground.copy(.4f)
                                )
                            )
                        } else {
                            MDTableCell(node = tableCell)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MDTableCell(node: TableCell, modifier: Modifier = Modifier) {

    val marker = LocalMarker.current
    val text = remember(node) {
        buildAnnotatedString {
            appendMarkdownContent(marker, node)
            toAnnotatedString()
        }
    }

    Text(
        modifier = modifier.padding(4.dp),
        text = text,
        color = MaterialTheme.colors.onBackground
    )

}
