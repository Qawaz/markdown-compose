package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.utils.SimpleTableLayout
import com.wakaztahir.markdowntext.utils.drawTableBorders

@Composable
internal fun MDTable(rows: MutableList<MutableList<AnnotatedString>>) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(width = 2.dp, color = MaterialTheme.colors.onBackground.copy(.3f))
    ) {

        val tableBorderColor: Color = MaterialTheme.colors.onBackground.copy(.4f)

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
    style: TextStyle = MaterialTheme.typography.body1,
    color: Color = MaterialTheme.colors.onBackground,
    tableContent: AnnotatedString,
) {

    TODO("Render Text")

}
