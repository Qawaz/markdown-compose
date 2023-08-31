package com.wakaztahir.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.core.MarkdownPreviewConfiguration
import com.wakaztahir.markdowncompose.editor.components.BlockComponent
import com.wakaztahir.markdowncompose.editor.components.ProvideLazyEditor
import com.wakaztahir.markdowncompose.editor.components.rememberLazyEditorScope
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.editor.utils.setMarkdown
import com.wakaztahir.markdowncompose.preview.MarkdownPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDEditorEditor(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            var markdown by remember { mutableStateOf(InitialMarkdown) }
            OutlinedTextField(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                value = markdown,
                onValueChange = { markdown = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )
            MarkdownEditor(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight(),
                markdown = markdown
            )
        }
    }
}

@Composable
fun MarkdownEditor(modifier: Modifier = Modifier, markdown: String) {

    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme
    val state = remember { EditorState() }


    LaunchedEffect(markdown) {
        state.setMarkdown(markdown = markdown, colors = colors, typography = typography)
    }

    val listState = rememberLazyListState()
    val scope = rememberLazyEditorScope(state = state, listState = listState)
    ProvideLazyEditor(scope) {
        LazyColumn(modifier = modifier, state = listState) {
            items(state.blocks) { block ->
                BlockComponent(block = block)
            }
        }
    }
}