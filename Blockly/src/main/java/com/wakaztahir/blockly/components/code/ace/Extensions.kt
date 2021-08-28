package com.wakaztahir.blockly.components.code.ace

import android.util.Log
import java.util.*

fun AceEditor.setMode(mode: AceEditor.Mode) {
    evaluateJavascript("editor.session.setMode(\"ace/mode/${mode.name.lowercase(Locale.getDefault())}\");") {}
}

fun AceEditor.setTheme(theme: AceEditor.Theme) {
    evaluateJavascript("editor.setTheme(\"ace/theme/${theme.name.lowercase(Locale.getDefault())}\");") {}
}

fun AceEditor.setFontSize(fontSizeInpx: Int) {
    evaluateJavascript("editor.setFontSize($fontSizeInpx);") {}
}

fun AceEditor.setSoftWrap(enabled: Boolean) {
    evaluateJavascript(
        "editor.getSession().setUseWrapMode(${
            enabled.toString().lowercase(Locale.getDefault())
        });"
    ) {}
}

fun AceEditor.setText(text: String) {
    val stringBuilder = StringBuilder()
    stringBuilder.append("editor.session.setValue(\"")
    stringBuilder.append(text)
    stringBuilder.append("\")")
    Log.d("TL_CodeComponent", stringBuilder.toString())
    evaluateJavascript(stringBuilder.toString()) {}
}

fun AceEditor.insertTextAtCursor(text: String) {
    evaluateJavascript("editor.insert(\"$text\");") {}
}

fun AceEditor.requestText(onReceived: (String) -> Unit) {
    evaluateJavascript("editor.getValue()") {
        onReceived(it.removeSurrounding("\""))
    }
}

fun AceEditor.requestRowCount() {
    evaluateJavascript("editor.session.getLength()") {
        Log.d("BL_RowCount", it)
    }
}

fun AceEditor.requestCursorCoords() {
    evaluateJavascript("editor.getCursorPosition()") {
        Log.d("BL_CursorCoords", it)
    }
}

fun AceEditor.requestLine(lineNumber: Int, onReceived: (String) -> Unit) {
    evaluateJavascript("editor.session.getLine($lineNumber)") {
        onReceived(it)
    }
}

fun AceEditor.requestLines(startLine: Int, endLine: Int, onReceived: (String) -> Unit) {
    evaluateJavascript("editor.session.getLines($startLine, $endLine)") {
        onReceived(it)
    }
}

fun AceEditor.startFind(
    toFind: String,
    backwards: Boolean,
    wrap: Boolean,
    caseSensitive: Boolean,
    wholeWord: Boolean,
) {
    evaluateJavascript(
        "javascript:editor.find('" + toFind + "', backwards: " + backwards.toString() +
                ", wrap: " + wrap.toString() +
                ",caseSensitive: " + caseSensitive.toString() +
                ",wholeWord: " + wholeWord.toString() + ",regExp: false})"
    ) {
        Log.d("BL_startFind", it)
    }
}

fun AceEditor.requestSelectedText(onReceived: (String) -> Unit) {
    evaluateJavascript("editor.getSelectedText()") {
        onReceived(it)
    }
}

fun AceEditor.replace(text: String, replace: String) {
    evaluateJavascript("editor.replace(\"$text\",\"$replace\");") {}
}

fun AceEditor.hideMenu() {
    evaluateJavascript(
        """
            var hider = document.getElementById("menu-hider")
            if(hider!=null){
                document.body.removeChild(hider)
            }
            
            var newStyle = document.createElement("style")
            newStyle.id = "menu-hider"
            newStyle.innerHTML = ".ace_mobile-menu {display : none !important;}"
            document.body.appendChild(newStyle)
        """.trimIndent()
    ) {}
}

fun AceEditor.showMenu() {
    evaluateJavascript(
        """
        var hider = document.getElementById("menu-hider")
        if(hider!=null){
            document.body.removeChild(hider)
        }
    """.trimIndent()
    ) {}
}
