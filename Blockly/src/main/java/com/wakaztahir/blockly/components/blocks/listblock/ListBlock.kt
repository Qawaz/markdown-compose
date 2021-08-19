package com.wakaztahir.timeline.blockly.components.blocks.listblock

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wakaztahir.blockly.model.ListBlock
import com.wakaztahir.blockly.model.ListItem
import kotlinx.coroutines.launch

@Composable
fun ListBlock(
    modifier: Modifier = Modifier,
    block: ListBlock,
    onUpdate: () -> Unit,
    onRemove: () -> Unit
) = ListItems(
    modifier = modifier,
    items = block.items,
    onAdd = {
        block.items.add(it,ListItem())
    },
    onUpdate = onUpdate,
    onRemove = {
        if (block.items.size == 1) {
            onRemove()
        } else {
            block.items.remove(it)
        }
    },
    onReplace = { index, newIndex ->
        if (index > -1 && newIndex > -1 && index < block.items.size && newIndex <= block.items.size) {
            val item = block.items[index]
            block.items.removeAt(index)
            block.items.add(newIndex, item)
            onUpdate()
        } else {
            Log.e("TL_ListBlock", "Index out of bounds")
        }
    }
)

@Composable
private fun ListItems(
    modifier: Modifier = Modifier,
    items: List<ListItem>,
    onAdd: (Int) -> Unit,
    onUpdate: () -> Unit,
    onRemove: (ListItem) -> Unit,
    onReplace: (index: Int, newIndex: Int) -> Unit,
) {

    val scope = rememberCoroutineScope()

    var animationsEnabled by remember { mutableStateOf(true) }

    /**
     * Rearranges other items as one item is being dragged
     */
    val rearrangeItems: suspend (Int, ListItem, Dp) -> Unit = { index, item, yOffset ->

        val tolerance = 5.dp

        if (yOffset < 0.dp) { // item is going up
            var itemHeights = 0.dp
            items.subList(0, index).reversed().forEach { each ->
                itemHeights += each.itemHeight
                if (itemHeights < (-yOffset + tolerance)) {
                    each.topOffset = item.itemHeight
                } else {
                    each.topOffset = 0.dp
                }
            }
        } else { // item is going down
            var itemHeights = 0.dp
            items.subList(index + 1, items.size).forEach { each ->
                itemHeights += each.itemHeight
                if ((yOffset + tolerance) > itemHeights) {
                    each.topOffset = -item.itemHeight
                } else {
                    each.topOffset = 0.dp
                }
            }
        }
    }

    val fixedResetOffset: suspend (Dp, Int, ListItem) -> Unit = { yOffset, index, item ->
        animationsEnabled = false
        var newIndex = index
        var itemHeights = 0.dp
        val tolerance = 5.dp
        if (yOffset > 0.dp) { // item is below its current position
            items.subList(index + 1, items.size).forEach { each ->
                itemHeights += each.itemHeight
                if ((yOffset + tolerance) > itemHeights) {
                    newIndex++
                }
            }
        } else { // item is above its current position
            items.subList(0, index).reversed().forEach { each ->
                itemHeights += each.itemHeight
                if (itemHeights < (-yOffset + tolerance)) {
                    newIndex--
                }
            }
        }

        //Resetting offsets
        items.forEach { each ->
            each.topOffset = 0.dp
        }

        //Changing Indexes of Items
        onReplace(index, newIndex)

        animationsEnabled = true
    }

    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->

            val topOffset by animateDpAsState(targetValue = item.topOffset)
            var yOffset by remember { mutableStateOf(0.dp) }

            ListItem(
                modifier = Modifier.offset(y = yOffset + topOffset),
                item = item,
                onAdd = { onAdd(index + 1) },
                onUpdate = onUpdate,
                onRemove = { onRemove(item) },
                onVerticalDragged = {
                    var aboveHeight = 0.dp
                    items.subList(0, index).forEach { item -> aboveHeight -= item.itemHeight }
                    var belowHeight = 0.dp
                    items.subList(index, items.size - 1)
                        .forEach { item -> belowHeight += item.itemHeight }
                    if ((yOffset + it) > aboveHeight && (yOffset + it) < belowHeight) {
                        yOffset += it
                    }
                    scope.launch {
                        rearrangeItems(index, item, yOffset)
                    }
                },
                onVerticalDragStopped = {
                    scope.launch {
                        fixedResetOffset(yOffset, index, item)
                        yOffset = 0.dp
                    }
                }
            )
        }
    }
}