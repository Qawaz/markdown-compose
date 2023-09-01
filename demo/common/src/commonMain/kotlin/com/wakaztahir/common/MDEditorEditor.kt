@file:Suppress("unused")

package com.wakaztahir.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.editor.components.*
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.serialization.polymorphicEditorBlockSerializer
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.editor.utils.exportToMarkdown
import com.wakaztahir.markdowncompose.editor.utils.toMarkdown
import com.wakaztahir.markdowncompose.editor.utils.setMarkdown
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@Composable
fun MDEditorEditor(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        EditorToJson()
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

@Composable
private fun EditorOnly() {
    val listState = rememberLazyListState()
    val state = remember {
        EditorState().apply {
            this.blocks.add(TextBlock(TextFieldValue(buildAnnotatedString {
                append("There's nothing can console me but my darling ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("human")
                pop()
                append(" bold")
            })))
        }
    }
    val scope = rememberLazyEditorScope(state, listState)
    ProvideLazyEditor(scope) {
        Column {
            EditorTools(
                state = remember {
                    State()
                }
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight(),
                state = listState
            ) {
                items(state.blocks) { block ->
                    BlockComponent(block = block)
                }
            }
        }
    }
}

@Composable
private fun EditorToJson() {

    val listState = rememberLazyListState()
    val state = remember { EditorState() }
    val scope = rememberLazyEditorScope(state, listState)
    var jsonToEditor by remember { mutableStateOf(false) }

    ProvideLazyEditor(scope) {

        Box {
            EditorTools(state = remember { State() })
            IconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = { jsonToEditor = !jsonToEditor }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = null
                )
            }
        }

        @Composable
        fun RowScope.Editor() {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight(),
                state = listState
            ) {
                items(state.blocks) { block ->
                    BlockComponent(block = block)
                }
            }
        }

        @Composable
        fun RowScope.Field() {
            val json = Json {
                serializersModule = SerializersModule { polymorphicEditorBlockSerializer() }
            }
            var jsonStr by remember { mutableStateOf("") }

            suspend fun autoMarkdownIt() {
                if (jsonToEditor) {
                    try {
                        val blocks = json.decodeFromString<List<EditorBlock>>(jsonStr)
                        state.blocks.clear()
                        state.blocks.addAll(blocks)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    jsonStr = json.encodeToString(state.blocks.toList())
                }
                delay(1000)
                autoMarkdownIt()
            }

            LaunchedEffect(null) {
                autoMarkdownIt()
            }

            OutlinedTextField(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                value = jsonStr,
                onValueChange = { jsonStr = it },
                readOnly = !jsonToEditor,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (jsonToEditor) {
                Field()
                Editor()
            } else {
                Editor()
                Field()
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
                    @Suppress("DEPRECATION")
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