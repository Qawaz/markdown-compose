package com.wakaztahir.markpose.editor.model

interface UndoRedoAction {
    suspend fun onUndo()
    suspend fun onRedo()
}