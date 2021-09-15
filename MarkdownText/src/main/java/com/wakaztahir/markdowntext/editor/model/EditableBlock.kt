package com.wakaztahir.markdowntext.editor.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString

abstract class EditableBlock

open class TextBlock(annotatedString: AnnotatedString = AnnotatedString("")) : EditableBlock() {
    var text by mutableStateOf(annotatedString)
}

class HeadingBlock(annotatedString: AnnotatedString = AnnotatedString("")) : TextBlock(annotatedString)

class ParagraphBlock(annotatedString: AnnotatedString = AnnotatedString("")) : TextBlock(annotatedString)

class ImageBlock : EditableBlock()

class ListBlock : EditableBlock()

class CodeBlock : EditableBlock()

class QuoteBlock : EditableBlock()
