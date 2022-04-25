package com.wakaztahir.markpose.editor.utils

import androidx.compose.material.Colors
import androidx.compose.material.Typography
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markpose.editor.model.blocks.CodeBlock
import com.wakaztahir.markpose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markpose.editor.model.blocks.MathBlock
import com.wakaztahir.markpose.editor.model.blocks.TextBlock
import com.wakaztahir.markpose.editor.states.EditorState
import com.wakaztahir.markpose.core.annotation.buildMarkdownAnnotatedString
import com.wakaztahir.markpose.utils.fastForEach
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.MarkdownParser

fun EditorState.setMarkdown(markdown: String, colors: Colors, typography: Typography) {
    val parser = MarkdownParser(GFMFlavourDescriptor())
    val tree = parser.buildMarkdownTreeFromString(markdown)
    tree.children.fastForEach { node ->
        if (!node.importIntoState(state = this, markdown, colors = colors, typography = typography)) {
            node.children.fastForEach { child ->
                child.importIntoState(state = this, markdown, colors = colors, typography = typography)
            }
        }
    }
}

private fun ASTNode.importIntoState(
    state: EditorState,
    content: String,
    colors: Colors,
    typography: Typography
): Boolean {

    val multipleTextBlocks = false

    fun appendAnnotatedString(annotatedString: AnnotatedString) {
        if (!multipleTextBlocks && state.blocks.size > 0 && state.blocks.last() is TextBlock) {
            val textBlock = state.blocks.last() as TextBlock
            textBlock.textFieldValue = textBlock.textFieldValue.copy(annotatedString = buildAnnotatedString {
                if (textBlock.textFieldValue.text.isNotEmpty()) {
                    append(textBlock.textFieldValue.annotatedString)
                    append("\n\n")
                }
                append(annotatedString)
            })
        } else {
            val textBlock = TextBlock(value = TextFieldValue(annotatedString = annotatedString))
            state.blocks.add(textBlock)
        }
    }

    fun appendText(text: String) {
        appendAnnotatedString(buildAnnotatedString { append(text) })
    }

    var handled = true
    when (type) {
        MarkdownTokenTypes.TEXT -> {
            appendText(getTextInNode(content).toString())
        }
//        MarkdownTokenTypes.EOL -> Spacer(Modifier.padding(4.dp))
        MarkdownElementTypes.CODE_FENCE -> {
            val lang =
                findChildOfType(MarkdownTokenTypes.FENCE_LANG)?.getTextInNode(content)?.trim()?.toString() ?: "js"
            val start = children[2].startOffset
            val end = children[children.size - 2].endOffset
            if (lang != "latex") {
                state.blocks.add(CodeBlock(lang = lang, value = content.subSequence(start, end).trim().toString()))
            } else {
                state.blocks.add(MathBlock(latex = content.subSequence(start, end).trim().toString()))
            }
        }
//        MarkdownElementTypes.ATX_1 -> MarkdownHeader(content, this, MaterialTheme.typography.h2, color)
//        MarkdownElementTypes.ATX_2 -> MarkdownHeader(content, this, MaterialTheme.typography.h3, color)
//        MarkdownElementTypes.ATX_3 -> MarkdownHeader(content, this, MaterialTheme.typography.h4, color)
//        MarkdownElementTypes.ATX_4 -> MarkdownHeader(content, this, MaterialTheme.typography.h5, color)
//        MarkdownElementTypes.ATX_5 -> MarkdownHeader(content, this, MaterialTheme.typography.h6, color)
//        MarkdownElementTypes.ATX_6 -> MarkdownHeader(content, this, MaterialTheme.typography.h6, color)
//        MarkdownElementTypes.BLOCK_QUOTE -> MarkdownBlockQuote(content, this, color = color)
        MarkdownElementTypes.PARAGRAPH -> {
            appendAnnotatedString(buildAnnotatedString {
                pushStyle(typography.body1.toSpanStyle())
                buildMarkdownAnnotatedString(content, this@importIntoState, colors)
                pop()
            })
        }
        MarkdownElementTypes.ORDERED_LIST -> orderedList(
            state = state,
            content = content,
            colors = colors,
            typography = typography
        )
        MarkdownElementTypes.UNORDERED_LIST -> unorderedList(
            state = state,
            content = content,
            colors = colors,
            typography = typography
        )
        MarkdownElementTypes.IMAGE -> {
            val link = findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)?.toString()
            //todo add image
        }
        MarkdownElementTypes.LINK_DEFINITION -> {
            val linkLabel = findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getTextInNode(content)?.toString()
            if (linkLabel != null) {
                val destination =
                    findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)?.toString()
                //todo linklabel and destination
            }
        }
        else -> handled = false
    }
    return handled
}

private fun ASTNode.listItems(
    state: EditorState,
    content: String,
    typography: Typography,
    colors: Colors,
    level: Int,
    listItem: ASTNode.() -> Unit,
) {
    children.fastForEach { child ->
        when (child.type) {
            MarkdownElementTypes.LIST_ITEM -> {
                child.listItem()
                if (child.children.isNotEmpty()) {
                    child.listItems(state, content = content, typography, colors, level = level, listItem)
                }
            }
            MarkdownElementTypes.ORDERED_LIST -> child.orderedList(
                state = state,
                content = content,
                typography,
                colors,
                level = level + 1
            )
            MarkdownElementTypes.UNORDERED_LIST -> child.unorderedList(
                state = state,
                content = content,
                typography,
                colors,
                level = level + 1
            )
        }
    }
}

private fun ASTNode.unorderedList(
    state: EditorState,
    content: String,
    typography: Typography,
    colors: Colors,
    level: Int = 0,
) {
    listItems(state = state, content = content, typography, colors, level = level) {
        val blockItem = ListItemBlock(false)
        val bullet = findChildOfType(MarkdownTokenTypes.LIST_BULLET)?.getTextInNode(content)
        if (bullet != null) {
            if (bullet.trim() == "*") {
                blockItem.isChecked = false
            } else if (bullet.trim() == "-") {
                val checkbox = findChildOfType(GFMTokenTypes.CHECK_BOX)?.getTextInNode(content)?.trim()
                if (checkbox != null) {
                    if (checkbox == "[ ]" || checkbox == "[]") {
                        blockItem.isChecked = false
                    } else if (checkbox == "[x]") {
                        blockItem.isChecked = true
                    }
                } else {
                    // nothing to do
                }
            }
        }
        if (level > 0) {
            blockItem.isIndented = true
        }
        blockItem.textFieldValue = TextFieldValue(annotatedString = buildAnnotatedString {
            pushStyle(typography.body1.toSpanStyle())
            buildMarkdownAnnotatedString(content, children.filter { it.type != MarkdownTokenTypes.EOL }, colors)
            pop()
        })
        state.blocks.add(blockItem)
    }
}

private fun ASTNode.orderedList(
    state: EditorState,
    content: String,
    typography: Typography,
    colors: Colors,
    level: Int = 0,
) {
    val textBlock = TextBlock()
    textBlock.textFieldValue = TextFieldValue(annotatedString = buildAnnotatedString {
        listItems(state = state, content = content, typography, colors, level = level) {
            for (i in 0..level) {
                append("\t")
            }
            val listNumber = findChildOfType(MarkdownTokenTypes.LIST_NUMBER)?.getTextInNode(content)
            append(listNumber.toString() + " - ")
            pushStyle(typography.body1.toSpanStyle())
            buildMarkdownAnnotatedString(
                content,
                children.filter { it.type != MarkdownTokenTypes.EOL },
                colors
            )
            pop()
            append("\n")
        }
    })
    state.blocks.add(textBlock)
}