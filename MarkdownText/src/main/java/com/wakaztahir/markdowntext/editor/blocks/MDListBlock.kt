package com.wakaztahir.markdowntext.editor.blocks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wakaztahir.markdowntext.editor.EditableMarkdown
import com.wakaztahir.markdowntext.editor.model.BulletListItem
import com.wakaztahir.markdowntext.editor.model.ListBlock
import com.wakaztahir.markdowntext.editor.model.OrderedListItem
import com.wakaztahir.markdowntext.editor.model.TaskListItem

@Composable
internal fun ListBlock.EditableList(modifier: Modifier) {
    val list = this
    // todo make items draggable
    Column {
        list.items.forEach { listItem ->
            //todo indent items according to their indentation
            when (listItem) {
                is BulletListItem -> listItem.EditableListItem()
                is OrderedListItem -> listItem.EditableListItem()
                is TaskListItem -> listItem.EditableListItem()
            }
        }
    }
}

@Composable
internal fun BulletListItem.EditableListItem() {
    val item = this
    Row {
        Text(text = "-> ")
        EditableMarkdown(
            textValue = item.textValue,
            onUpdate = {
                item.textValue = it
            }
        )
    }
}

@Composable
internal fun OrderedListItem.EditableListItem() {

}

@Composable
internal fun TaskListItem.EditableListItem() {
    val item = this
    Row {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = {
                item.isChecked = it
            }
        )
        EditableMarkdown(
            textValue = item.textValue,
            onUpdate = {
                item.textValue = it
            }
        )
    }
}