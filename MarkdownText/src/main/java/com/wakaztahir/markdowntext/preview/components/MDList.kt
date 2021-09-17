package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.preview.model.LocalPreviewRenderer

interface PreviewListScope

interface BulletListScope : PreviewListScope {
    @Composable
    fun BulletListItem(bulletMarker: Char, appendContent: AnnotatedString.Builder.() -> Unit)

    @Composable
    fun TaskListItem(isChecked: Boolean, appendContent: AnnotatedString.Builder.() -> Unit)

}

interface OrderedListScope : PreviewListScope {
    @Composable
    fun OrderedListItem(
        number: Int,
        delimiter: Char,
        appendContent: AnnotatedString.Builder.() -> Unit
    )
}

@Composable
fun MDBulletList(
    modifier: Modifier = Modifier,
    isParentDocument: Boolean,
    content: @Composable BulletListScope.() -> Unit,
) {
    val bottom = if (isParentDocument) 8.dp else 0.dp
    val start = if (isParentDocument) 0.dp else 8.dp
    Column(modifier = modifier.padding(bottom = bottom, start = start)) {
        content(object : BulletListScope {
            @Composable
            override fun BulletListItem(
                bulletMarker: Char,
                appendContent: AnnotatedString.Builder.() -> Unit
            ) = MDBulletListItem(
                bulletMarker = bulletMarker,
                appendContent = appendContent
            )

            @Composable
            override fun TaskListItem(
                isChecked: Boolean,
                appendContent: AnnotatedString.Builder.() -> Unit
            ) = MDTaskListItem(
                isChecked = isChecked,
                appendContent = appendContent
            )
        })
    }
}

@Composable
fun MDOrderedList(
    modifier: Modifier = Modifier,
    isParentDocument: Boolean,
    content: @Composable (OrderedListScope.() -> Unit)
) {
    val bottom = if (isParentDocument) 8.dp else 0.dp
    val start = if (isParentDocument) 0.dp else 8.dp
    Column(modifier = modifier.padding(bottom = bottom, start = start)) {
        content(object : OrderedListScope {
            @Composable
            override fun OrderedListItem(
                number: Int,
                delimiter: Char,
                appendContent: AnnotatedString.Builder.() -> Unit
            ) = MDOrderedListItem(
                number = number,
                delimiter = delimiter,
                appendContent = appendContent
            )
        })
    }
}

@Composable
private fun MDBulletListItem(
    bulletMarker: Char,
    appendContent: AnnotatedString.Builder.() -> Unit
) {

    val renderer = LocalPreviewRenderer.current

    val text = buildAnnotatedString {
        append(bulletMarker)
        appendContent()
    }

    renderer.PreviewText(text = text, style = MaterialTheme.typography.body1)
}

@Composable
private fun MDTaskListItem(
    isChecked: Boolean,
    appendContent: AnnotatedString.Builder.() -> Unit
) {

    val renderer = LocalPreviewRenderer.current

    Row {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {},
            enabled = false
        )
        renderer.PreviewText(
            text = buildAnnotatedString {
                append("\t")
                appendContent()
            },
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun MDOrderedListItem(
    number: Int,
    delimiter: Char,
    appendContent: AnnotatedString.Builder.() -> Unit
) {
    val renderer = LocalPreviewRenderer.current

    val text = buildAnnotatedString {
        append("$number$delimiter ")
        appendContent()
    }

    renderer.PreviewText(text = text, style = MaterialTheme.typography.body1)
}