package com.wakaztahir.markdowncompose.editor.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.utils.TextFormatter
import com.wakaztahir.markdowncompose.editor.utils.textFieldUndoRedoAction
import com.wakaztahir.markdowncompose.editor.utils.updateState
import com.wakaztahir.markdowncompose.utils.IODispatcher
import com.wakaztahir.markdowncompose.utils.currentTimeMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextBlock.TextComponent(modifier: Modifier) {

    val state = LocalEditor.current
    val scope = rememberCoroutineScope()
    val formatter = remember {
        object : TextFormatter() {
            override var value: TextFieldValue
                get() = textFieldValue
                set(newValue) {
                    textFieldValue = newValue
                }
        }
    }

    var lastUndoRedoSaveTime by remember { mutableStateOf(currentTimeMillis()) }

    var linkPreviewsJob by remember { mutableStateOf<Job?>(null) }

    suspend fun updateLinkPreviews() {
        state.refreshLinkPreviews()
    }

    OutlinedTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it.copy() // annotated string styles aren't being updated
            scope.launch {
                state.textFieldUndoRedoAction(
                    lastUndoRedoSaveTime = lastUndoRedoSaveTime,
                    getCurrentTextFieldValue = { textFieldValue },
                    updateTextFieldValue = { newValue -> textFieldValue = newValue },
                    updateUndoRedoTime = { time -> lastUndoRedoSaveTime = time }
                )
            }
            linkPreviewsJob?.cancel()
            linkPreviewsJob = scope.launch(IODispatcher) {
                delay(3000)
                updateLinkPreviews()
            }
            state.activeFormatter = formatter
            formatter.updateState()
        },
        placeholder = {
            Text(text = "Note", color = MaterialTheme.colorScheme.onBackground.copy(.4f))
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            textColor = MaterialTheme.colorScheme.onBackground
        ),
        textStyle = MaterialTheme.typography.bodyMedium
    )

    LaunchedEffect(key1 = null, block = {
        if (requestFocus) {
            try {
                focusRequester.requestFocus()
                requestFocus = false
            } catch (_: Exception) {
                // Requests focus on launch
            }
        }
    })
}