package com.wakaztahir.markpose.editor.utils

import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markpose.editor.model.EditorBlock
import com.wakaztahir.markpose.editor.model.UndoRedoAction
import com.wakaztahir.markpose.editor.states.EditorState
import com.wakaztahir.markpose.utils.getTimeMillis

fun EditorState.addUndoAction(action: UndoRedoAction) {
    undoList.add(action)
    redoList.clear()
    updateUndoRedoState()
}

fun EditorState.updateUndoRedoState() {
    canUndo = undoList.size > 0
    canRedo = redoList.size > 0
}

suspend fun EditorState.undo(onFailure: (Throwable) -> Unit = { it.printStackTrace() }) {
    if (undoList.size > 0) {
        val action = undoList.last()
        kotlin.runCatching { action.onUndo() }.onFailure(onFailure)
        redoList.add(action)
        undoList.removeLast()
        updateUndoRedoState()
    }
}

suspend fun EditorState.redo(onFailure: (Throwable) -> Unit = { it.printStackTrace() }) {
    if (redoList.size > 0) {
        val action = redoList.last()
        kotlin.runCatching { action.onRedo() }.onFailure(onFailure)
        undoList.add(action)
        redoList.removeLast()
        updateUndoRedoState()
    }
}

abstract class TextFieldUndoRedoAction(var savedValue: TextFieldValue) : UndoRedoAction {
    abstract fun getValue(): TextFieldValue
    abstract fun setValue(newValue: TextFieldValue)
    private fun swap() {
        val previousValue = getValue()
        setValue(savedValue)
        savedValue = previousValue
    }

    override suspend fun onUndo() = swap()
    override suspend fun onRedo() = swap()
}

internal fun EditorState.textFieldUndoRedoAction(
    undoRedoTimeWait: Long = 4000,
    lastUndoRedoSaveTime: Long,
    getCurrentTextFieldValue: () -> TextFieldValue,
    updateTextFieldValue: (TextFieldValue) -> Unit,
    updateUndoRedoTime: (Long) -> Unit,
) {

    val textFieldValue = getCurrentTextFieldValue()

    if (textFieldUndoRedoLocked) {
        textFieldUndoRedoLocked = false
        return
    }

    val currentTime = getTimeMillis()
    // Not saving if was saved 5s ago
    if (lastUndoRedoSaveTime > (currentTime - undoRedoTimeWait)) {
        return
    }

    // Avoid saving empty text field values
    if (textFieldValue.text.isEmpty() && textFieldValue.annotatedString.spanStyles.isEmpty() && textFieldValue.annotatedString.paragraphStyles.isEmpty()) {
        return
    }

    // Avoid saving equal values
    val previousAction = undoList.lastOrNull { it is TextFieldUndoRedoAction } as TextFieldUndoRedoAction?
    if (previousAction != null && previousAction.savedValue.text == textFieldValue.text &&
        previousAction.savedValue.annotatedString.spanStyles.size == textFieldValue.annotatedString.spanStyles.size &&
        previousAction.savedValue.annotatedString.paragraphStyles.size == textFieldValue.annotatedString.paragraphStyles.size
    ) {
        return
    }

    // Adding Undo Redo
    addUndoAction(object : TextFieldUndoRedoAction(textFieldValue) {
        override fun getValue(): TextFieldValue = getCurrentTextFieldValue()

        override fun setValue(newValue: TextFieldValue) {
            textFieldUndoRedoLocked = true
            updateTextFieldValue(newValue)
        }
    })

    updateUndoRedoTime(currentTime)
}

fun EditorState.addBlock(index: Int = -1, block: EditorBlock) {
    if (index == -1) blocks.add(block) else blocks.add(index, block)
    addUndoAction(object : UndoRedoAction {
        var blockIndex = -1
        override suspend fun onUndo() {
            blockIndex = blocks.indexOf(block)
            blocks.remove(block)
        }

        override suspend fun onRedo() {
            if (blockIndex != -1) {
                blocks.add(blockIndex, block)
            }
        }
    })
}