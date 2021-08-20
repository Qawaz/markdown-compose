package com.wakaztahir.blockly.components.list

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wakaztahir.blockly.R
import com.wakaztahir.blockly.model.ListItem
import kotlinx.coroutines.CoroutineScope

@Composable
fun ListItemComponent(
    modifier: Modifier = Modifier,
    item: ListItem,
    onAdd: (String) -> Unit,
    onUpdate: () -> Unit,
    onRemove: () -> Unit,
    onVerticalDragged: (Dp) -> Unit = {},
    onVerticalDragStopped: suspend CoroutineScope.(Float) -> Unit = {},
    strikeChecked: Boolean = true,
) {

    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = item.text)) }
    val textFieldValue = textFieldValueState.copy(text = item.text)

    val focusRequester = remember(item) { FocusRequester() }
    var draggedPadding by remember(item) { mutableStateOf(0.dp) }

    val density = LocalDensity.current

    LaunchedEffect(key1 = item.isIndented, block = {
        draggedPadding = if (item.isIndented) {
            28.dp
        } else {
            0.dp
        }
    })

    val horizontalDraggableState = rememberDraggableState {
        val padding = with(density) { draggedPadding + it.toDp() }
        if (padding > 0.dp && padding < 28.dp) {
            draggedPadding = padding
            item.isIndented = padding >= 19.dp
        }
    }

    val verticalDraggableState = rememberDraggableState {
        onVerticalDragged(with(density) { it.toDp() })
    }

    Row(
        modifier = modifier
            .padding(start = 8.dp + draggedPadding, end = 8.dp)
            .onSizeChanged {
                item.itemHeight = with(density) { it.height.toDp() }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .draggable(horizontalDraggableState, Orientation.Horizontal)
                .draggable(
                    verticalDraggableState,
                    Orientation.Vertical,
                    onDragStopped = onVerticalDragStopped
                ),
            painter = painterResource(id = R.drawable.drag_indicator),
            tint = MaterialTheme.colors.onBackground,
            contentDescription = stringResource(id = R.string.drag_icon)
        )
        Checkbox(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp),
            checked = item.isChecked,
            onCheckedChange = {
                item.isChecked = it
                onUpdate()
            }
        )
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged {
                    if (!it.hasFocus) {
                        onUpdate()
                    }
                },
            value = textFieldValue,
            onValueChange = {
                textFieldValueState = it
                if (item.text != it.text) {
                    item.text = it.text
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                textColor = if(item.isChecked){
                    MaterialTheme.colors.onBackground.copy(.6f)
                }else{
                    MaterialTheme.colors.onBackground
                }
            ),
            textStyle = TextStyle(textDecoration = if (item.isChecked && strikeChecked) TextDecoration.LineThrough else null),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusRequester.freeFocus()
                // Adding Text After Selection To New Item
                onAdd(
                    if (textFieldValue.selection.collapsed) {
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
                )
                // Removing Added Text From Current Item
                if (textFieldValue.selection.collapsed) {
                    if (textFieldValue.selection.end < textFieldValue.text.length) {
                        item.text = item.text.substring(0, textFieldValue.selection.end)
                    }
                }
            }),
        )
        IconButton(
            modifier = Modifier.size(20.dp),
            onClick = { onRemove() }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                tint = MaterialTheme.colors.onBackground,
                contentDescription = stringResource(id = R.string.delete_icon)
            )
        }
    }

    LaunchedEffect(key1 = item.requestFocus, block = {
        if (item.requestFocus) {
            focusRequester.requestFocus()
            item.requestFocus = false
        }
    })
}