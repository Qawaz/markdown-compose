package com.mylibrary.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.editor.components.BlockComponent
import com.wakaztahir.markdowntext.editor.components.ProvideLazyEditor
import com.wakaztahir.markdowntext.editor.components.linkPreviews
import com.wakaztahir.markdowntext.editor.components.refreshLinkPreviews
import com.wakaztahir.markdowntext.editor.states.EditorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Editor(){
    val editorState = remember { EditorState() }
    val listState = rememberLazyListState()
    ProvideLazyEditor(state = editorState, listState = listState) {
        Column(modifier = Modifier.background(color = MaterialTheme.colors.background)) {
            LazyColumn(
                modifier = Modifier.lazyEditor().weight(1f),
                contentPadding = remember { PaddingValues(bottom = 8.dp) },
                state = listState
            ) {

                // editor itself
                itemsIndexed(
                    items = editorState.blocks,
                    key = { _, block -> block.hashCode() }
                ) { index, block ->
                    BlockComponent(modifier = Modifier.weight(1f), block = block, index)
                }

                // link previews
                linkPreviews(editorState)
            }

            LaunchedEffect(null) {
                withContext(Dispatchers.IO) {
                    editorState.refreshLinkPreviews()
                }
            }

            val toolsState = remember { mutableStateOf(ToolsState.Hidden) }

            EditorTools(
                modifier = Modifier.fillMaxWidth(),
                state = toolsState
            )
        }
    }
}