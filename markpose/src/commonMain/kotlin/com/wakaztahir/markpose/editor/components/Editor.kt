package com.wakaztahir.markpose.editor.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.wakaztahir.markpose.editor.model.EditorBlock
import com.wakaztahir.markpose.editor.model.blocks.CodeBlock
import com.wakaztahir.markpose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markpose.editor.model.blocks.MathBlock
import com.wakaztahir.markpose.editor.model.blocks.TextBlock
import com.wakaztahir.markpose.editor.states.EditorState
import com.wakaztahir.markpose.utils.MyIcons
import org.burnoutcrew.reorderable.*

val LocalEditor = compositionLocalOf { EditorState() }

abstract class LazyEditorScope {
    abstract val state : EditorState
    internal abstract val reorderState: ReorderableState
    abstract fun Modifier.lazyEditor(): Modifier
}

@Composable
fun ProvideLazyEditor(
    state: EditorState,
    listState: LazyListState = rememberLazyListState(),
    content: @Composable LazyEditorScope.() -> Unit,
) {
    val reorderState = rememberReorderState(listState = listState)
    CompositionLocalProvider(LocalEditor provides state) {
        content(object : LazyEditorScope() {

            override val state: EditorState
                get() = state

            override val reorderState: ReorderableState
                get() = reorderState

            override fun Modifier.lazyEditor(): Modifier = this.reorderable(state = reorderState, onMove = { from, to ->
                state.blocks.move(
                    state.blocks.indexOfFirst { it.hashCode() == from.key },
                    state.blocks.indexOfFirst { it.hashCode() == to.key })
            })

        })
    }
}

@Composable
fun LazyEditorScope.BlockComponent(
    modifier: Modifier = Modifier,
    block: EditorBlock,
    index: Int
) {
    when (block) {
        is TextBlock -> block.TextComponent(modifier = modifier.draggedItem(reorderState.offsetByKey(block.hashCode())))
        is ListItemBlock -> block.ListItemComponent(
            modifier = modifier.draggedItem(reorderState.offsetByKey(block.hashCode())),
            this,
            index
        )
        is CodeBlock -> block.CodeComponent(modifier = modifier.draggedItem(reorderState.offsetByKey(block.hashCode())))
        is MathBlock -> block.MathComponent(modifier = modifier.draggedItem(reorderState.offsetByKey(block.hashCode())))
    }
}

@Composable
fun LazyEditorScope.BlockReorderableComponent(
    modifier: Modifier = Modifier,
    block: EditorBlock,
    maxPreviewLength: Int = 20,
) {
    when (block) {
        is TextBlock -> ReorderableComponent(
            modifier = Modifier.draggedItem(reorderState.offsetByKey(block.hashCode())).then(modifier),
            name = "Text Block ${
                if (block.textFieldValue.text.length > maxPreviewLength) block.textFieldValue.text.replace('\n', ' ')
                    .slice(
                        0..maxPreviewLength
                    ) + "..." else block.textFieldValue.text.replace('\n', ' ')
            }",
            block = block
        )
        is ListItemBlock -> ReorderableComponent(
            modifier = Modifier.draggedItem(reorderState.offsetByKey(block.hashCode())).then(modifier),
            name = "List Block ${
                if (block.text.length > maxPreviewLength) block.text.replace('\n', ' ')
                    .slice(0..maxPreviewLength) + "..." else block.text.replace('\n', ' ')
            }",
            block = block
        )
        is CodeBlock -> ReorderableComponent(
            modifier = Modifier.draggedItem(reorderState.offsetByKey(block.hashCode())).then(modifier),
            name = block.lang.capitalize(Locale.current) + " Code Block",
            block = block
        )
        is MathBlock -> ReorderableComponent(
            modifier = Modifier.draggedItem(reorderState.offsetByKey(block.hashCode())).then(modifier),
            name = "Math Block ${
                if (block.latex.length > maxPreviewLength) block.latex.replace('\n', ' ')
                    .slice(0..maxPreviewLength) + "..." else block.latex.replace('\n', ' ')
            }",
            block = block
        )
    }
}

@Composable
private fun LazyEditorScope.ReorderableComponent(
    modifier: Modifier = Modifier,
    block: EditorBlock,
    name: String
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                .detectReorder(state = this@ReorderableComponent.reorderState),
            painter = MyIcons.DragIndicator,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground
        )
        Text(
            modifier = Modifier.weight(1f),
            text = name
        )
        IconButton(onClick = { state.blocks.remove(block) }){
            Icon(
                painter = MyIcons.Delete,
                contentDescription = null
            )
        }
    }
}