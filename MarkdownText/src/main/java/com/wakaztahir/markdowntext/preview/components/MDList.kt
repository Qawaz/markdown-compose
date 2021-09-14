package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.LocalMarker
import com.wakaztahir.markdowntext.preview.MarkdownText
import org.commonmark.ext.task.list.items.TaskListItemMarker
import org.commonmark.node.*

@Composable
internal fun MDListItems(
    listBlock: ListBlock,
    modifier: Modifier = Modifier,
    item: @Composable (node: Node) -> Unit
) {
    val marker = LocalMarker.current
    val bottom = if (listBlock.parent is Document) 8.dp else 0.dp
    val start = if (listBlock.parent is Document) 0.dp else 8.dp
    Column(modifier = modifier.padding(bottom = bottom, start = start)) {
        var listItem = listBlock.firstChild
        while (listItem != null) {
            var child = listItem.firstChild
            while (child != null) {
                when (child) {
                    is BulletList -> MDBulletList(child, modifier)
                    is OrderedList -> MDOrderedList(child, modifier)
                    else -> {
                        if(child is TaskListItemMarker && child.next !is BulletList && child.next !is OrderedList){
                            Row {
                                item(child)
                                marker.preventBulletMarker = true
                                item(child.next)
                            }
                            child = child.next
                        }else{
                            item(child)
                        }
                    }
                }
                child = child.next
            }
            listItem = listItem.next
        }
    }
}


@Composable
fun MDBulletList(bulletList: BulletList, modifier: Modifier = Modifier) {
    val mdMarker = LocalMarker.current
    val marker = bulletList.bulletMarker
    val body1 = MaterialTheme.typography.body1
    MDListItems(bulletList, modifier = modifier) {
        if (it !is TaskListItemMarker) {
            val text = remember(it, body1) {
                buildAnnotatedString {
                    if(!mdMarker.preventBulletMarker) {
                        append("$marker ")
                    }else{
                        mdMarker.preventBulletMarker = false
                    }
                    appendMarkdownContent(mdMarker, it)
                    toAnnotatedString()
                }
            }
            MarkdownText(text, MaterialTheme.typography.body1)
        } else {
            Checkbox(
                checked = it.isChecked,
                onCheckedChange = {

                },
                enabled = false
            )
        }
    }
}

@Composable
fun MDOrderedList(orderedList: OrderedList, modifier: Modifier = Modifier) {
    var number = orderedList.startNumber
    val delimiter = orderedList.delimiter
    val marker = LocalMarker.current
    MDListItems(orderedList, modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("${number++}$delimiter ")
            appendMarkdownContent(marker, it)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, modifier)
    }
}