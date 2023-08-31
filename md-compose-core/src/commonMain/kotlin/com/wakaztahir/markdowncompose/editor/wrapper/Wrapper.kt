package com.wakaztahir.markdowncompose.editor.wrapper

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("NOTHING_TO_INLINE")
inline fun AnnotatedString.Builder.push(wrapper: Wrapper) {
    with(wrapper) { push() }
}

sealed interface Wrapper {

    fun toHTML(): String

    fun toMarkdown(): String

    fun AnnotatedString.Builder.push()

}

@SerialName("text")
@Serializable
class TextWrap(val text: String) : Wrapper {

    override fun toHTML(): String {
        return text
    }

    override fun toMarkdown(): String {
        return text
    }

    override fun AnnotatedString.Builder.push() {
        append(text)
    }

    override fun toString(): String {
        return text
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextWrap) return false

        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }


}

@SerialName("bold")
@Serializable
class BoldWrap(val item: Wrapper) : Wrapper {

    override fun toHTML(): String {
        return "<strong>" + item.toHTML() + "</strong>"
    }

    override fun toMarkdown(): String {
        return "**" + item.toMarkdown() + "**"
    }

    override fun AnnotatedString.Builder.push() {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        with(item) { push() }
        pop()
    }

    override fun toString(): String {
        return toHTML()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BoldWrap) return false

        if (item != other.item) return false

        return true
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }

}

@SerialName("italic")
@Serializable
class ItalicWrap(val item: Wrapper) : Wrapper {

    override fun toHTML(): String {
        return "<em>" + item.toHTML() + "</em>"
    }

    override fun toMarkdown(): String {
        return "*" + item.toHTML() + "*"
    }

    override fun AnnotatedString.Builder.push() {
        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
        with(item) { push() }
        pop()
    }

    override fun toString(): String {
        return toHTML()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItalicWrap) return false

        if (item != other.item) return false

        return true
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }


}

@SerialName("strikethrough")
@Serializable
class StrikethroughWrap(val item: Wrapper) : Wrapper {

    override fun toHTML(): String {
        return "<s>" + item.toHTML() + "</s>"
    }

    override fun toMarkdown(): String {
        return "~~" + item.toHTML() + "~~"
    }

    override fun AnnotatedString.Builder.push() {
        pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
        with(item) { push() }
        pop()
    }

    override fun toString(): String {
        return toHTML()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StrikethroughWrap) return false

        if (item != other.item) return false

        return true
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }

}

@SerialName("link")
@Serializable
class LinkWrap(val to: String, val item: Wrapper) : Wrapper {

    override fun toHTML(): String {
        return "<a href=\"" + to + "\">" + item.toHTML() + "</a>"
    }

    override fun toMarkdown(): String {
        return "[" + item.toMarkdown() + "](" + to + ")"
    }

    @OptIn(ExperimentalTextApi::class)
    override fun AnnotatedString.Builder.push() {
        pushUrlAnnotation(UrlAnnotation(to))
        with(item) { push() }
        pop()
    }

    override fun toString(): String {
        return toHTML()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinkWrap) return false

        if (to != other.to) return false
        if (item != other.item) return false

        return true
    }

    override fun hashCode(): Int {
        var result = to.hashCode()
        result = 31 * result + item.hashCode()
        return result
    }

}

@SerialName("children")
@Serializable
class ChildrenWrap(private val children: List<Wrapper>) : Wrapper {

    constructor(vararg wrap: Wrapper) : this(wrap.toList())

    override fun toHTML(): String {
        return children.joinToString("") { it.toHTML() }
    }

    override fun toMarkdown(): String {
        return children.joinToString("") { it.toMarkdown() }
    }

    override fun AnnotatedString.Builder.push() {
        for (child in children) with(child) { push() }
    }

    override fun toString(): String {
        return toHTML()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChildrenWrap) return false

        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        return children.hashCode()
    }

}