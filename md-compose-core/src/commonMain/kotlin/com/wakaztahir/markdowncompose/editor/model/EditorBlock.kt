package com.wakaztahir.markdowncompose.editor.model

import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.helpers.randomUUID

abstract class EditorBlock {

    val uuid = randomUUID()

    abstract fun exportText(state: EditorState): String
    abstract fun exportMarkdown(state: EditorState): String
    abstract fun exportHTML(state: EditorState): String
}