package com.wakaztahir.markdowntext.editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowntext.editor.utils.TextFormatter
import com.wakaztahir.markdowntext.editor.utils.textFieldUndoRedoAction
import com.wakaztahir.markdowntext.editor.utils.updateState
import com.wakaztahir.markdowntext.utils.IODispatcher
import com.wakaztahir.markdowntext.utils.MyIcons
import com.wakaztahir.markdowntext.utils.getTimeMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.detectReorder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItemBlock.ListItemComponent(
    modifier: Modifier = Modifier,
    lazyEditor: LazyEditorScope,
    index: Int,
) {

    val state = LocalEditor.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var draggedPadding by remember(isIndented) {
        mutableStateOf(
            if (isIndented) {
                28.dp
            } else {
                0.dp
            }
        )
    }

    val density = LocalDensity.current

    val horizontalDraggableState = rememberDraggableState {
        val padding = with(density) { draggedPadding + it.toDp() }
        if (padding > 0.dp && padding < 28.dp) {
            draggedPadding = padding
            isIndented = padding >= 19.dp
        }
    }

    var showClose by remember { mutableStateOf(false) }
    val formatter = remember {
        object : TextFormatter() {
            override var value: TextFieldValue
                get() = textFieldValue
                set(newValue) {
                    textFieldValue = newValue
                }
        }
    }

    var lastUndoRedoTime by remember { mutableStateOf(getTimeMillis()) }

    var linkPreviewsJob by remember { mutableStateOf<Job?>(null) }

    suspend fun updateLinkPreviews() {
        state.refreshLinkPreviews()
    }

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.padding(start = draggedPadding + 8.dp).detectReorder(state = lazyEditor.reorderState)
                .draggable(
                    horizontalDraggableState,
                    androidx.compose.foundation.gestures.Orientation.Horizontal
                ),
            painter = MyIcons.DragIndicator,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground
        )
        Checkbox(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp),
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colors.primary,
                checkmarkColor = MaterialTheme.colors.onBackground
            )
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f)
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged {
                    showClose = it.hasFocus
                },
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                scope.launch {
                    state.textFieldUndoRedoAction(
                        lastUndoRedoSaveTime = lastUndoRedoTime,
                        getCurrentTextFieldValue = { textFieldValue },
                        updateUndoRedoTime = { time -> lastUndoRedoTime = time },
                        updateTextFieldValue = { newValue -> textFieldValue = newValue }
                    )
                    state.activeFormatter = formatter
                    formatter.updateState()
                }
                linkPreviewsJob?.cancel()
                linkPreviewsJob = scope.launch(IODispatcher()) {
                    delay(3000)
                    updateLinkPreviews()
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                textColor = if (isChecked) {
                    MaterialTheme.colors.onBackground.copy(.6f)
                } else {
                    MaterialTheme.colors.onBackground
                }
            ),
            textStyle = TextStyle.Companion.Default.copy(textDecoration = if (isChecked) TextDecoration.LineThrough else null),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusRequester.freeFocus()
                // Adding Text After Selection To New Item
                state.blocks.add(
                    index = index + 1,
                    ListItemBlock().apply {
                        requestFocus = true
                        text = if (textFieldValue.selection.collapsed) {
                            if (textFieldValue.selection.end < textFieldValue.text.length) {
                                textFieldValue.text.substring(
                                    textFieldValue.selection.end,
                                    textFieldValue.text.length
                                )
                            } else {
                                ""
                            }
                        } else {
                            ""
                        }
                        isIndented = isIndented
                    }
                )
                // Removing Added Text From Current Item
                if (textFieldValue.selection.collapsed) {
                    if (textFieldValue.selection.end < textFieldValue.text.length) {
                        textFieldValue.text.substring(0, textFieldValue.selection.end).let { text ->
                            this@ListItemComponent.text = text
                        }
                    }
                }
            })
        )
        IconButton(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(20.dp),
            onClick = {
                state.blocks.remove(this@ListItemComponent)
            }
        ) {
            if (showClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    tint = MaterialTheme.colors.onBackground,
                    contentDescription = "Delete Icon"
                )
            }
        }
    }

    LaunchedEffect(key1 = requestFocus, block = {
        if (requestFocus) {
            kotlin.runCatching {
                focusRequester.requestFocus()
            }
            requestFocus = false
        }
    })
}