package com.wakaztahir.markdowntext.editor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue

abstract class EditableBlock

/**
 * [text] contains styling for text in [AnnotatedString]
 */
open class TextBlock(annotatedString: AnnotatedString = AnnotatedString("")) : EditableBlock() {
    var textValue = TextFieldValue(annotatedString = annotatedString)
}

/**
 * [text] contains styling for child nodes only
 * [level] can be 0-6 , 0 is default
 */
class HeadingBlock(text: AnnotatedString = AnnotatedString(""), level: Int = 0) :
    TextBlock(text) {
    var level by mutableStateOf(level)
}

/**
 * [text] contains styling for child nodes only
 */
class ParagraphBlock(text: AnnotatedString = AnnotatedString("")) :
    TextBlock(text)

class ImageBlock : EditableBlock()

class ListBlock : EditableBlock() {
    var items = mutableStateListOf<ListItem>()
}

abstract class ListItem(indentationLevel: Int, annotatedString: AnnotatedString) {
    var indentation by mutableStateOf(indentationLevel)
    var textValue = TextFieldValue(annotatedString = annotatedString)
}

class TaskListItem(
    isChecked: Boolean,
    indentationLevel: Int,
    annotatedString: AnnotatedString
) : ListItem(indentationLevel, annotatedString) {
    var isChecked by mutableStateOf(isChecked)
}

class BulletListItem(
    indentationLevel: Int,
    bulletMarker: String,
    annotatedString: AnnotatedString
) : ListItem(indentationLevel, annotatedString) {
    var bulletMarker by mutableStateOf(bulletMarker)
}

class OrderedListItem(
    indentationLevel: Int,
    number: Int,
    delimiter: String,
    annotatedString: AnnotatedString
) :
    ListItem(indentationLevel, annotatedString) {
    var number by mutableStateOf(number)
    var delimiter by mutableStateOf(delimiter)
}

class CodeBlock : EditableBlock()

class QuoteBlock : EditableBlock()
