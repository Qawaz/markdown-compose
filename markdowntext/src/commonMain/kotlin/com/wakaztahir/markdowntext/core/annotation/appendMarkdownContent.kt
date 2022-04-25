package com.wakaztahir.markdowntext.core.annotation

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.wakaztahir.markdowntext.utils.*
import com.wakaztahir.markdowntext.utils.innerList
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

/**
 * Appends Styles for all the children nodes of the [parent]
 */
internal fun AnnotatedString.Builder.buildMarkdownAnnotatedString(content: String, node: ASTNode, colors: Colors) {
    buildMarkdownAnnotatedString(content, node.children, colors)
}

/**
 * Appends styles for [node] and its children
 */
internal fun AnnotatedString.Builder.buildMarkdownAnnotatedString(
    content: String,
    children: List<ASTNode>,
    colors: Colors
) {
    children.fastForEach { child ->
        when (child.type) {
            MarkdownElementTypes.PARAGRAPH -> buildMarkdownAnnotatedString(content, child, colors)
            MarkdownElementTypes.IMAGE -> child.findChildOfTypeRecursive(MarkdownElementTypes.LINK_DESTINATION)
                ?.let {
                    appendInlineContent(TAG_IMAGE_URL, it.getTextInNode(content).toString())
                }
            MarkdownElementTypes.EMPH -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                buildMarkdownAnnotatedString(content, child, colors)
                pop()
            }
            MarkdownElementTypes.STRONG -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                buildMarkdownAnnotatedString(content, child, colors)
                pop()
            }
            MarkdownElementTypes.CODE_SPAN -> {
                pushStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = colors.onBackground.copy(alpha = 0.1f)
                    )
                )
                append(' ')
                buildMarkdownAnnotatedString(content, child.children.innerList(), colors)
                append(' ')
                pop()
            }
            MarkdownElementTypes.AUTOLINK -> appendAutoLink(content, child, colors)
            MarkdownElementTypes.INLINE_LINK -> appendMarkdownLink(content, child, colors)
            MarkdownElementTypes.SHORT_REFERENCE_LINK -> appendMarkdownLink(content, child, colors)
            MarkdownElementTypes.FULL_REFERENCE_LINK -> appendMarkdownLink(content, child, colors)
            GFMElementTypes.STRIKETHROUGH -> {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                buildMarkdownAnnotatedString(content,child.children,colors)
                pop()
            }
            // handling html tags as text
            MarkdownTokenTypes.TEXT, MarkdownTokenTypes.HTML_TAG -> append(child.getTextInNode(content).toString())
            GFMTokenTypes.GFM_AUTOLINK -> if (child.parent == MarkdownElementTypes.LINK_TEXT) {
                append(child.getTextInNode(content).toString())
            } else appendAutoLink(content, child, colors)
            MarkdownTokenTypes.SINGLE_QUOTE -> append('\'')
            MarkdownTokenTypes.DOUBLE_QUOTE -> append('\"')
            MarkdownTokenTypes.LPAREN -> append('(')
            MarkdownTokenTypes.RPAREN -> append(')')
            MarkdownTokenTypes.LBRACKET -> append('[')
            MarkdownTokenTypes.RBRACKET -> append(']')
            MarkdownTokenTypes.LT -> append('<')
            MarkdownTokenTypes.GT -> append('>')
            MarkdownTokenTypes.COLON -> append(':')
            MarkdownTokenTypes.EXCLAMATION_MARK -> append('!')
            MarkdownTokenTypes.BACKTICK -> append('`')
            MarkdownTokenTypes.HARD_LINE_BREAK -> append("\n\n")
            MarkdownTokenTypes.EOL -> append('\n')
            MarkdownTokenTypes.WHITE_SPACE -> append(' ')
//            else -> println("Missed on ${child.type}")
        }
    }
}

fun AnnotatedString.Builder.appendAutoLink(content: String, node: ASTNode, colors: Colors) {
    val destination = node.getTextInNode(content).toString()
    pushStringAnnotation(TAG_URL, (destination))
    pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold))
    append(destination)
    pop()
    pop()
}

fun AnnotatedString.Builder.appendMarkdownLink(content: String, node: ASTNode, colors: Colors) {
    val linkText = node.findChildOfType(MarkdownElementTypes.LINK_TEXT)?.children?.innerList() ?: return
    val destination =
        node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)?.toString()
    val linkLabel = node.findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getTextInNode(content)?.toString()
    (destination ?: linkLabel)?.let { pushStringAnnotation(TAG_URL, it) }
    pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold))
    buildMarkdownAnnotatedString(content, linkText, colors)
    pop()
    (destination ?: linkLabel)?.let { pop() }
}