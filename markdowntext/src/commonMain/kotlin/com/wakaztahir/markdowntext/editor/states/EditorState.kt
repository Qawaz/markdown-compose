package com.wakaztahir.markdowntext.editor.states

import androidx.compose.runtime.*
import com.wakaztahir.linkpreview.LinkPreview
import com.wakaztahir.markdowntext.editor.model.EditorBlock
import com.wakaztahir.markdowntext.editor.model.UndoRedoAction
import com.wakaztahir.markdowntext.editor.utils.TextFormatter

class EditorState {
    var activeBlock by mutableStateOf<EditorBlock?>(null)
    val blocks = mutableStateListOf<EditorBlock>()
    var activeFormatter by mutableStateOf<TextFormatter?>(null)
    internal val undoList = mutableListOf<UndoRedoAction>()
    internal val redoList = mutableListOf<UndoRedoAction>()
    internal val linkPreviews = mutableStateMapOf<String,LinkPreview>()

    var canUndo by mutableStateOf(false)
        internal set
    var canRedo by mutableStateOf(false)
        internal set

    internal var textFieldUndoRedoLocked: Boolean = false
}