package com.wakaztahir.markdowncompose.editor.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wakaztahir.markdowncompose.editor.model.TextFieldValueBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.utils.textFieldUndoRedoAction
import com.wakaztahir.markdowncompose.utils.*
import compose.icons.MaterialDesignIcons
import compose.icons.materialdesignicons.TrashCanOutline
import kotlinx.coroutines.launch

@Composable
fun CodeBlock.DefaultCodeComponent(modifier: Modifier) {
    val state = LocalEditor.current
    this@DefaultCodeComponent.DefaultCodeComponent(modifier = modifier, onRemove = { state.blocks.remove(this) })
}

@Composable
fun TextFieldValueBlock.DefaultCodeComponent(modifier: Modifier = Modifier, onRemove: () -> Unit) {

    val state = LocalEditor.current
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = MaterialDesignIcons.TrashCanOutline,
                    contentDescription = "Delete Icon",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        var lastUndoRedoTime by remember { mutableStateOf(currentTimeMillis()) }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                scope.launch {
                    state.textFieldUndoRedoAction(
                        lastUndoRedoSaveTime = lastUndoRedoTime,
                        getCurrentTextFieldValue = { textFieldValue },
                        updateTextFieldValue = { newValue -> textFieldValue = newValue },
                        updateUndoRedoTime = { time -> lastUndoRedoTime = time }
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )
    }
}