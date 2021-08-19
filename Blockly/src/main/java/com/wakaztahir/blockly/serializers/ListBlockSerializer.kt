package com.wakaztahir.blockly.serializers

import com.wakaztahir.blockly.model.ListBlock
import com.wakaztahir.blockly.model.ListItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// Surrogates

@SerialName("ListItem")
@Serializable
class ListItemSurrogate(
    val text: String,
    val isChecked: Boolean,
    val isIndented: Boolean
)

@SerialName("ListBlock")
@Serializable
class ListBlockSurrogate(
    val items: List<ListItemSurrogate>
) : BlockSurrogate()

// Surrogate Converters

internal fun ListItemSurrogate.toListItem(): ListItem {
    return ListItem().apply {
        isChecked = this@toListItem.isChecked
        isIndented = this@toListItem.isIndented
        text = this@toListItem.text
    }
}

internal fun ListItem.toSurrogate(): ListItemSurrogate {
    return ListItemSurrogate(
        text = this.text,
        isChecked = this.isChecked,
        isIndented = this.isIndented,
    )
}

internal fun ListBlock.toSurrogate(): ListBlockSurrogate {
    return ListBlockSurrogate(this.items.map { it.toSurrogate() })
}

internal fun ListBlockSurrogate.toListBlock(): ListBlock {
    return ListBlock().apply {
        items.addAll(this@toListBlock.items.map { it.toListItem() })
    }
}