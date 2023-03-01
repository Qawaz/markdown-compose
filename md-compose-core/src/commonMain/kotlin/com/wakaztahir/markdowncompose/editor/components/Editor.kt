package com.wakaztahir.markdowncompose.editor.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.wakaztahir.qawazlogger.logIt
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.states.EditorState
import org.burnoutcrew.reorderable.*
import compose.icons.MaterialDesignIcons
import compose.icons.materialdesignicons.Drag
import compose.icons.materialdesignicons.TrashCanOutline

val LocalEditor = compositionLocalOf { EditorState() }

interface LazyEditorScope {
    val state: EditorState
    val reorderState: ReorderableState<*>

    @Composable
    fun CodeComponent(modifier: Modifier = Modifier, block: CodeBlock) = block.DefaultCodeComponent(modifier)

    @Composable
    fun MathComponent(modifier: Modifier = Modifier, block: MathBlock) = block.DefaultMathComponent(modifier = modifier)

}

@Composable
fun EditorState.rememberReorderState(listState: LazyListState): ReorderableState<*> {
    return rememberReorderableLazyListState(
        onMove = { from, to ->
            val fromIndex = blocks.indexOfFirst { it.uuid == from.key }
            val toIndex = blocks.indexOfFirst { it.uuid == to.key }
            runCatching { blocks.removeAt(fromIndex) }.onFailure {
                it.logIt()
            }.getOrNull()?.let {
                blocks.add(minOf(maxOf(0, toIndex), blocks.size), it)
            }
        },
        listState = listState,
        canDragOver = { _, _ -> true }
    )
}

@Composable
fun rememberLazyEditorScope(
    state: EditorState,
    listState: LazyListState,
    reorderState: ReorderableState<*> = state.rememberReorderState(listState)
): LazyEditorScope {
    return remember(reorderState, state) {
        object : LazyEditorScope {
            override val state: EditorState = state
            override val reorderState: ReorderableState<*> = reorderState
        }
    }
}

@Composable
fun ProvideLazyEditor(
    state: EditorState,
    scope: LazyEditorScope,
    content: @Composable LazyEditorScope.() -> Unit,
) = CompositionLocalProvider(LocalEditor provides state) {
    content(scope)
}

@Composable
fun LazyEditorScope.BlockComponent(
    modifier: Modifier = Modifier,
    block: EditorBlock,
    index: Int? = null,
) {
    when (block) {
        is TextBlock -> block.TextComponent(
            modifier = modifier
        )

        is ListItemBlock -> {
            ReorderableItem(
                modifier = modifier,
                state = reorderState,
                key = block.uuid,
                index = index,
            ) {
                block.ListItemComponent(lazyEditor = this@BlockComponent)
            }
        }

        is CodeBlock -> CodeComponent(modifier = modifier, block = block)

        is MathBlock -> MathComponent(modifier = modifier, block = block)
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
            modifier = modifier,
            name = "Text Block ${
                if (block.textFieldValue.text.length > maxPreviewLength) block.textFieldValue.text.replace('\n', ' ')
                    .slice(
                        0..maxPreviewLength
                    ) + "..." else block.textFieldValue.text.replace('\n', ' ')
            }",
            block = block
        )

        is ListItemBlock -> ReorderableComponent(
            modifier = modifier,
            name = "List Block ${
                if (block.text.length > maxPreviewLength) block.text.replace('\n', ' ')
                    .slice(0..maxPreviewLength) + "..." else block.text.replace('\n', ' ')
            }",
            block = block
        )

        is CodeBlock -> ReorderableComponent(
            modifier = modifier,
            name = block.lang.capitalize(Locale.current) + " Code Block",
            block = block
        )

        is MathBlock -> ReorderableComponent(
            modifier = modifier,
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
    ReorderableItem(
        modifier = Modifier.detectReorder(reorderState),
        state = reorderState,
        key = block.uuid,
    ) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                imageVector = MaterialDesignIcons.Drag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                modifier = Modifier.weight(1f),
                text = name
            )
            IconButton(onClick = { state.blocks.remove(block) }) {
                Icon(
                    imageVector = MaterialDesignIcons.TrashCanOutline,
                    contentDescription = null
                )
            }
        }
    }
}