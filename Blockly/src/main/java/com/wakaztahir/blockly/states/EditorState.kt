package com.wakaztahir.blockly.states

import android.os.Build
import android.text.Html

class EditorState(val text: TextState) {

    fun undo() {
        if (text.editText != null && text.undoStates.isNotEmpty()) {
            val state = text.undoStates.last()
            text.undoRedoHelper.undo(text.editText!!, state)
            text.undoStates.removeAt(text.undoStates.size - 1)
            text.redoStates.add(state)
        }
    }

    fun redo() {
        if (text.editText != null && text.redoStates.isNotEmpty()) {
            val state = text.redoStates.last()
            text.undoRedoHelper.redo(text.editText!!, state)
            text.redoStates.removeAt(text.redoStates.size - 1)
            text.undoStates.add(state)
        }
    }

    @Suppress("Deprecation")
    fun getHtml(): String {
        return if (text.editText != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.toHtml(text.editText?.text, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.toHtml(text.editText?.text)
            }
        } else {
            ""
        }
    }

    fun getText(): String {
        return text.getText()
    }

    fun setText(text: String) {
        this.text.setText(text)
    }

    fun getMarkdown(): String {
        return text.getMarkdown()
    }

    fun setMarkdown(markdown: String) {
        text.setMarkdown(markdown)
    }

}