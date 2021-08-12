package com.wakaztahir.timeline.blockly.components.blocks.listblock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.timeline.blockly.model.ListBlock
import com.wakaztahir.timeline.blockly.model.ListItem

@Composable
fun ListBlock(
    block: ListBlock,
    onUpdate: () -> Unit,
    onRemove: () -> Unit
) {
    ListItems(
        items = block.items,
        onAdd = {
            block.items.add(ListItem())
        },
        onUpdate = onUpdate,
        onRemove = {
            if (block.items.size == 1) {
                onRemove()
            } else {
                block.items.remove(it)
            }
        },
        onAccommodate = { index, newIndex ->
            if (index > 0 && index < block.items.size && newIndex > 0 && newIndex < block.items.size) {
                val item = block.items[index]
                block.items.remove(item)
                block.items.add(newIndex, item)
            }
        }
    )
}

@Composable
private fun ListItems(
    modifier: Modifier = Modifier,
    items: List<ListItem>,
    onAdd: () -> Unit,
    onUpdate: () -> Unit,
    onRemove: (ListItem) -> Unit,
    onAccommodate: (index: Int, expected: Int) -> Unit,
) {
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->

            var yOffset by remember { mutableStateOf(0.dp) }

            ListItem(
                modifier = Modifier.offset(y = yOffset),
                item = item,
                onAdd = onAdd,
                onUpdate = onUpdate,
                onRemove = { onRemove(item) },
                onVerticalDragged = {
                    yOffset += it
                }
            )
        }
    }
}