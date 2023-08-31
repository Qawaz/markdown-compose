package com.wakaztahir.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.editor.components.*
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.editor.utils.exportToMarkdown
import com.wakaztahir.markdowncompose.editor.utils.toMarkdown
import com.wakaztahir.markdowncompose.editor.utils.setMarkdown
import kotlinx.coroutines.delay

@Composable
fun MDEditorEditor(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        MarkdownToEditor()
    }
}

@Composable
private fun MarkdownToEditor() {
    val listState = rememberLazyListState()
    val state = remember { EditorState() }
    val scope = rememberLazyEditorScope(state, listState)
    ProvideLazyEditor(scope) {
        EditorTools(
            state = remember {
                State()
            }
        )
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

            val typography = MaterialTheme.typography
            val colors = MaterialTheme.colorScheme

            LaunchedEffect(markdown) {
                state.setMarkdown(markdown = markdown, colors = colors, typography = typography)
            }

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight(),
                state = listState
            ) {
                items(state.blocks) { block ->
                    BlockComponent(block = block)
                }
            }
        }
    }
}

//@Suppress("unused")
@Composable
private fun EditorToMarkdown() {

    val listState = rememberLazyListState()
    val state = remember { EditorState() }
    val scope = rememberLazyEditorScope(state, listState)
    var newMarkdownConverter by remember { mutableStateOf(false) }

    ProvideLazyEditor(scope) {

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = newMarkdownConverter,
                    onCheckedChange = {
                        newMarkdownConverter = it
                    }
                )
                Text(
                    text = "New Markdown Converter",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            EditorTools(state = remember { State() })
        }

        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {


            val typography = MaterialTheme.typography
            val colors = MaterialTheme.colorScheme

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight(),
                state = listState
            ) {
                items(state.blocks) { block ->
                    BlockComponent(block = block)
                }
            }

            var markdown by remember { mutableStateOf("") }

            suspend fun autoMarkdownIt() {
                markdown = if (newMarkdownConverter) {
                    state.toMarkdown()
                } else {
                    state.exportToMarkdown()
                }
                delay(1000)
                autoMarkdownIt()
            }

            LaunchedEffect(null) {
                autoMarkdownIt()
            }

            OutlinedTextField(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                value = markdown,
                onValueChange = { markdown = it },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )

        }
    }
}