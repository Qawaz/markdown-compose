package com.wakaztahir.markdowncompose.editor.model

interface UndoRedoAction {
    suspend fun onUndo()
    suspend fun onRedo()
}