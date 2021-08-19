package com.wakaztahir.blockly.states

import androidx.compose.runtime.*
import com.wakaztahir.blockly.components.blocks.textblock.markdowntext.MarkdownEditText
import com.wakaztahir.blockly.components.blocks.textblock.markdowntext.TextStyle
import com.wakaztahir.blockly.components.blocks.textblock.markdowntext.UndoRedoHelper

class TextState {

    val undoStates = mutableListOf<UndoRedoHelper.EditItem>()
    val redoStates = mutableListOf<UndoRedoHelper.EditItem>()
    val undoRedoHelper = UndoRedoHelper {
        redoStates.clear()
        undoStates.add(it)
    }

    var isBold by mutableStateOf(false)
    var isItalic by mutableStateOf(false)
    var isStrikeThrough by mutableStateOf(false)
    var isUnorderedList by mutableStateOf(false)
    var isOrderedList by mutableStateOf(false)
    var isTaskList by mutableStateOf(false)
    var isLink by mutableStateOf(false)
    var editText: MarkdownEditText? = null


    //View Dependent Variables & Functions

    var initialized = false
    var initializers = mutableListOf<() -> Unit>()

    fun initialize() {
        initializers.forEach { init ->
            init()
            initializers.remove(init)
        }
        initialized = true
    }

    fun whenInitialized(perform: () -> Unit) {
        if (!initialized) {
            initializers.add(perform)
        } else {
            perform()
        }
    }

    fun getText(): String {
        return (editText?.text ?: "").toString()
    }

    fun setText(text: String) {
        editText?.setText(text)
    }

    fun getMarkdown(): String {
        return editText?.getMarkdown() ?: ""
    }

    fun setMarkdown(markdown: String) {
        editText?.setMarkdown(markdown)
    }

    fun effect(style: TextStyle, disable: Boolean) {
        editText?.triggerStyle(style, disable)
    }

    fun addLink(title: String?, url: String) {
        editText?.addLinkSpan(title, url)
    }

}

@Composable
fun rememberBlocklyTextState(
    isBold: Boolean = false,
    isItalic: Boolean = false,
    isStrikeThrough: Boolean = false,
    isUnorderedList: Boolean = false,
    isOrderedList: Boolean = false,
    isTaskList: Boolean = false,
    isLink: Boolean = false
): TextState {
    return remember {
        TextState().apply {
            this.isBold = isBold
            this.isItalic = isItalic
            this.isStrikeThrough = isStrikeThrough
            this.isUnorderedList = isUnorderedList
            this.isOrderedList = isOrderedList
            this.isTaskList = isTaskList
            this.isLink = isLink
        }
    }
}