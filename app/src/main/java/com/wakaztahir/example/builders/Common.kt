package com.wakaztahir.example.builders

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.wakaztahir.example.testing.appendMarkdownContent
import org.commonmark.node.*

infix fun AnnotatedString.Builder.appendHardLineBreak(node: HardLineBreak) {
    append("\n")
}

infix fun AnnotatedString.Builder.appendText(node: Text) {
    append(node.literal)
}

infix fun AnnotatedString.Builder.appendParagraph(node : Paragraph){
    appendMarkdownContent(node)
    append("\n")
}

infix fun AnnotatedString.Builder.appendEmphasis(node: Emphasis) {
    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
    appendMarkdownContent(node)
    pop()
}

infix fun AnnotatedString.Builder.appendStrongEmphasis(node: StrongEmphasis) {
    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
    appendMarkdownContent(node)
    pop()
}

@OptIn(ExperimentalUnitApi::class)
infix fun AnnotatedString.Builder.appendHeading(node : Heading){

    val size = when(node.level){
        1 -> 42f
        2 -> 38f
        3 -> 32f
        4 -> 28f
        5 -> 24f
        6 -> 20f
        else -> 18f
    }

    pushStyle(SpanStyle(fontSize = TextUnit(size, TextUnitType.Sp)))
    appendMarkdownContent(node)
    pop()
    append("\n")
}