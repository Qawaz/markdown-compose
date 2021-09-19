package com.wakaztahir.markdowntext.editor.model

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowntext.editor.blocks.*
import com.wakaztahir.markdowntext.editor.blocks.EditableImage
import com.wakaztahir.markdowntext.editor.blocks.EditableTextBlock
import com.wakaztahir.markdowntext.editor.blocks.MDHeadingBlock
import com.wakaztahir.markdowntext.editor.blocks.MDParagraphBlock

abstract class EditableBlock {
    @Composable
    abstract fun RenderBlock(modifier: Modifier)
}

/**
 * [text] contains styling for text in [AnnotatedString]
 */
open class TextBlock(annotatedString: AnnotatedString = AnnotatedString("")) : EditableBlock() {
    var textValue = TextFieldValue(annotatedString = annotatedString)

    @Composable
    override fun RenderBlock(modifier: Modifier) = this.EditableTextBlock(modifier = modifier)
}

/**
 * [text] contains styling for child nodes only
 * [level] can be 0-6 , 0 is default
 */
class HeadingBlock(text: AnnotatedString = AnnotatedString(""), level: Int = 0) :
    TextBlock(text) {
    var level by mutableStateOf(level)

    @Composable
    override fun RenderBlock(modifier: Modifier) = this.MDHeadingBlock(modifier = modifier)
}

/**
 * [text] contains styling for child nodes only
 */
class ParagraphBlock(text: AnnotatedString = AnnotatedString("")) : TextBlock(text){
    @Composable
    override fun RenderBlock(modifier : Modifier) = this.MDParagraphBlock(modifier = modifier)
}

class ImageBlock : EditableBlock() {
    @Composable
    override fun RenderBlock(modifier: Modifier) = this.EditableImage(modifier = modifier)
}

class ListBlock : EditableBlock() {
    var items = mutableStateListOf<ListItem>()

    @Composable
    override fun RenderBlock(modifier: Modifier) = this.EditableList(modifier = modifier)
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

class CodeBlock : EditableBlock() {
    @Composable
    override fun RenderBlock(modifier: Modifier) = this.EditableCode(modifier = modifier)
}

class QuoteBlock : EditableBlock() {
    @Composable
    override fun RenderBlock(modifier: Modifier) = this.EditableQuote(modifier = modifier)
}
