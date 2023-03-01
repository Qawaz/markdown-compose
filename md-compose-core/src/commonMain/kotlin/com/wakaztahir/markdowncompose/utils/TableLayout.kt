package com.wakaztahir.markdowncompose.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrain
import kotlin.math.roundToInt

/**
 *
 * The offsets of rows and columns of a [SimpleTableLayout], centered inside their spacing.
 *
 * E.g. If a table is given a cell spacing of 2px, then the first column and row offset will each
 * be 1px.
 */
@Immutable
internal data class TableLayoutResult(
    val rowOffsets: List<Float>,
    val columnOffsets: List<Float>
)

/**
 * A simple table that sizes all columns equally.
 *
 * @param cellSpacing The space in between each cell, and between each outer cell and the edge of
 * the table.
 */
@Composable
internal fun <T> SimpleTableLayout(
    modifier: Modifier = Modifier,
    columns: Int,
    rows: List<List<T>>,
    drawDecorations: (TableLayoutResult) -> Modifier,
    cellSpacing: Float,
    content: @Composable (T) -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val measurables = subcompose(false) {
            for (row in rows) {
                for (cell in row) {
                    content(cell)
                }
            }
        }

        val rowMeasurables = measurables.chunked(columns)
        // Divide the width by the number of columns, then leave room for the padding.
        val cellSpacingWidth = cellSpacing * (columns + 1)
        val cellWidth = (constraints.maxWidth - cellSpacingWidth) / columns
        val cellSpacingHeight = cellSpacing * (rowMeasurables.size + 1)
        // TODO Handle bounded height constraints.
        // val cellMaxHeight = if (!constraints.hasBoundedHeight) {
        //   Float.MAX_VALUE
        // } else {
        //   // Divide the height by the number of rows, then leave room for the padding.
        //   (constraints.maxHeight - cellSpacingHeight) / rowMeasurables.size
        // }
        val cellConstraints = Constraints(maxWidth = cellWidth.roundToInt()).constrain(constraints)

        val rowPlaceables = rowMeasurables.map { cellMeasurables ->
            cellMeasurables.map { cell ->
                cell.measure(cellConstraints)
            }
        }
        val rowHeights = rowPlaceables.map { row -> row.maxByOrNull { it.height }!!.height }

        val tableWidth = constraints.maxWidth
        val tableHeight = (rowHeights.sumOf { it } + cellSpacingHeight).roundToInt()
        layout(tableWidth, tableHeight) {
            var y = cellSpacing
            val rowOffsets = mutableListOf<Float>()
            val columnOffsets = mutableListOf<Float>()

            var rowIndex = -1
            for (cellPlaceables in rowPlaceables) {
                rowIndex++
                rowOffsets += y - cellSpacing / 2f
                var x = cellSpacing

                for (cell in cellPlaceables) {
                    if (rowIndex == 0) {
                        columnOffsets.add(x - cellSpacing / 2f)
                    }
                    cell.place(x.roundToInt(), y.roundToInt())
                    x += cellWidth + cellSpacing
                }

                if (rowIndex == 0) {
                    // Add the right-most edge.
                    columnOffsets.add(x - cellSpacing / 2f)
                }

                y += rowHeights[rowIndex] + cellSpacing
            }

            rowOffsets.add(y - cellSpacing / 2f)

            // Compose and draw the borders.
            val layoutResult = TableLayoutResult(rowOffsets, columnOffsets)
            subcompose(true) {
                Box(modifier = drawDecorations(layoutResult))
            }.single()
                .measure(Constraints.fixed(tableWidth, tableHeight))
                .placeRelative(0, 0)
        }
    }
}


internal fun Modifier.drawTableBorders(
    rowOffsets: List<Float>,
    columnOffsets: List<Float>,
    borderColor: Color,
    borderStrokeWidth: Float
) = drawBehind {
    // Draw horizontal borders.
    for (position in rowOffsets) {
        drawLine(
            borderColor,
            start = Offset(0f, position),
            end = Offset(size.width, position),
            borderStrokeWidth
        )
    }

    // Draw vertical borders.
    for (position in columnOffsets) {
        drawLine(
            borderColor,
            Offset(position, 0f),
            Offset(position, size.height),
            borderStrokeWidth
        )
    }
}