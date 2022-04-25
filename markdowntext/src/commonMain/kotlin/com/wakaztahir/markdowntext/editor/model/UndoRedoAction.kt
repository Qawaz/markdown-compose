package com.wakaztahir.markdowntext.editor.model

interface UndoRedoAction {
    suspend fun onUndo()
    suspend fun onRedo()
}