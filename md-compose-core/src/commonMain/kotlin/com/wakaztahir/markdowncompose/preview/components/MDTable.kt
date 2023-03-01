package com.wakaztahir.markdowncompose.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.utils.SimpleTableLayout
import com.wakaztahir.markdowncompose.utils.drawTableBorders

@Composable
internal fun MDTable(rows: MutableList<MutableList<AnnotatedString>>) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.onBackground.copy(.3f))
    ) {

        val tableBorderColor: Color = MaterialTheme.colorScheme.onBackground.copy(.4f)

        if (rows.isNotEmpty()) {
            SimpleTableLayout(
                columns = rows.first().size,
                rows = rows,
                drawDecorations = {
                    Modifier.drawTableBorders(
                        rowOffsets = it.rowOffsets,
                        columnOffsets = it.columnOffsets,
                        borderColor = tableBorderColor,
                        6f
                    )
                },
                cellSpacing = 8f
            ) {
                MDTableCell(
                    modifier = Modifier.padding(end = 4.dp, start = 4.dp),
                    tableContent = it,
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MDTableCell(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
    tableContent: AnnotatedString,
) {

    TODO("Render Text")

}
