package com.wakaztahir.blockly.components.blocks.textblock.markdowntext

import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import androidx.appcompat.widget.AppCompatEditText

class UndoRedoHelper(private val addUndoState: (state: EditItem) -> Unit) : TextWatcher {

    private var isUndoOrRedo = false
    private var currentEditItem: EditItem? = null
    private var mBeforeChange: CharSequence? = null
    private var mAfterChange: CharSequence? = null
    private var lastActionType = ActionType.NOT_DEF
    private var lastActionTime: Long = 0
    private val actionType: ActionType
        get() {
            return if (!TextUtils.isEmpty(mBeforeChange) && TextUtils.isEmpty(mAfterChange)) {
                ActionType.DELETE
            } else if (TextUtils.isEmpty(mBeforeChange) && !TextUtils.isEmpty(mAfterChange)) {
                ActionType.INSERT
            } else {
                ActionType.PASTE
            }
        }

    fun undo(editText: AppCompatEditText, state: EditItem?) {
        val edit: EditItem = (state ?: return)
        //getting the text
        val text = editText.editableText
        //getting start cursor position
        val start = edit.cursorStartPos
        //getting end cursor position
        val end = start + if (edit.afterText != null) edit.afterText!!.length else 0
        //replacing text
        isUndoOrRedo = true
        text.replace(start, end, edit.beforeText) //getting the previous text mmBefore from history
        isUndoOrRedo = false
        //removing underlines
        for (o in text.getSpans(0, text.length, UnderlineSpan::class.java)) {
            text.removeSpan(o)
        }
        //setting selection
        Selection.setSelection(
            text,
            if (edit.beforeText == null) start else start + edit.beforeText!!.length
        )
    }


    fun redo(editText: AppCompatEditText, state: EditItem?) {
        val edit: EditItem = (state ?: return) // checking to if there is redo thing in redo stack
        //getting the text
        val text = editText.editableText
        //getting cursor start position
        val start = edit.cursorStartPos
        //getting cursor end position
        val end = start + if (edit.beforeText != null) edit.beforeText!!.length else 0
        //replacing text
        isUndoOrRedo = true
        text.replace(start, end, edit.afterText)
        isUndoOrRedo = false
        // getting rid of underlines
        for (o in text.getSpans(0, text.length, UnderlineSpan::class.java)) {
            text.removeSpan(o)
        }
        //setting the cursor selection
        Selection.setSelection(
            text,
            if (edit.afterText == null) start else start + edit.afterText!!.length
        )
    }

    inner class EditItem(
        var cursorStartPos: Int,
        var beforeText: CharSequence?,
        var afterText: CharSequence?
    )

    internal enum class ActionType {
        INSERT, DELETE, PASTE, NOT_DEF
    }

    private fun makeBatch(start: Int) {
        val at = actionType
        val editItem: EditItem? = currentEditItem
        if (lastActionType != at || ActionType.PASTE == at || System.currentTimeMillis() - lastActionTime > 500 || editItem == null) {
            currentEditItem = EditItem(start, mBeforeChange, mAfterChange)
            addUndoState(currentEditItem!!)
        } else {
            if (at == ActionType.DELETE) {
                editItem.cursorStartPos = start
                editItem.beforeText = TextUtils.concat(mBeforeChange, editItem.beforeText)
            } else {
                editItem.afterText = TextUtils.concat(editItem.afterText, mAfterChange)
            }
        }
        lastActionType = at
        lastActionTime = System.currentTimeMillis()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (isUndoOrRedo) {
            return
        }
        if (s != null) {
            mBeforeChange = s.subSequence(start, start + count)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isUndoOrRedo) {
            return
        }
        if (s != null) {
            mAfterChange = s.subSequence(start, start + count)
        }
        makeBatch(start)
    }

    override fun afterTextChanged(s: Editable?) {}
}