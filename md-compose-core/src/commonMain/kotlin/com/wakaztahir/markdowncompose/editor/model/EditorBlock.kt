package com.wakaztahir.markdowncompose.editor.model

import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.utils.randomUUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class EditorBlock {

    @Transient
    val uuid = randomUUID()

    abstract fun exportText(state: EditorState): String
    abstract fun exportMarkdown(state: EditorState): String
    abstract fun exportHTML(state: EditorState): String

}