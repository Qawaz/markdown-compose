package com.wakaztahir.markpose.editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markpose.editor.model.blocks.TextBlock
import com.wakaztahir.markpose.editor.utils.TextFormatter
import com.wakaztahir.markpose.editor.utils.textFieldUndoRedoAction
import com.wakaztahir.markpose.editor.utils.updateState
import com.wakaztahir.markpose.utils.IODispatcher
import com.wakaztahir.markpose.utils.getTimeMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
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

    var lastUndoRedoSaveTime by remember { mutableStateOf(getTimeMillis()) }

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
            linkPreviewsJob = scope.launch(IODispatcher()) {
                delay(3000)
                updateLinkPreviews()
            }
            state.activeFormatter = formatter
            formatter.updateState()
        },
        placeholder = {
            Text(text = "Note", color = MaterialTheme.colors.onBackground.copy(.4f))
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            textColor = MaterialTheme.colors.onBackground
        ),
        textStyle = MaterialTheme.typography.body1
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