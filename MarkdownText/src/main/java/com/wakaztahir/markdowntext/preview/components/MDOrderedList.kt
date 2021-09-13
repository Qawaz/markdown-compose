package com.wakaztahir.markdowntext.preview.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowntext.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.LocalMarker
import com.wakaztahir.markdowntext.preview.MarkdownText
import org.commonmark.node.OrderedList

@Composable
fun MDOrderedList(orderedList: OrderedList, modifier: Modifier = Modifier) {
    var number = orderedList.startNumber
    val delimiter = orderedList.delimiter
    val marker = LocalMarker.current
    MDListItems(orderedList, modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("${number++}$delimiter ")
            appendMarkdownContent(marker,it)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, modifier)
    }
}
