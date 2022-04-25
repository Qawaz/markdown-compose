package com.wakaztahir.markpose.editor.model

import com.wakaztahir.markpose.editor.states.EditorState

abstract class EditorBlock {
    abstract fun exportText(state: EditorState): String
    abstract fun exportMarkdown(state: EditorState): String
    abstract fun exportHTML(state: EditorState): String
}