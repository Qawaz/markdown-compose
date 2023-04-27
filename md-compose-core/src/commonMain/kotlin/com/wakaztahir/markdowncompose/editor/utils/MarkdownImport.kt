package com.wakaztahir.markdowncompose.editor.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.states.EditorState
import com.wakaztahir.markdowncompose.core.annotation.buildMarkdownAnnotatedString
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.MarkdownParser

fun EditorState.setMarkdown(markdown: String, colors: ColorScheme, typography: Typography) {
    val parser = MarkdownParser(GFMFlavourDescriptor())
    val tree = parser.buildMarkdownTreeFromString(markdown)
    for (node in tree.children) {
        if (!node.importIntoState(
                state = this,
                markdown,
                colors = colors,
                typography = typography
            )
        ) {
            for (child in node.children) {
                child.importIntoState(
                    state = this,
                    markdown,
                    colors = colors,
                    typography = typography
                )
            }
        }
    }
}

private fun ASTNode.importIntoState(
    state: EditorState,
    content: String,
    colors: ColorScheme,
    typography: Typography
): Boolean {

    val multipleTextBlocks = false

    fun appendAnnotatedString(annotatedString: AnnotatedString) {
        if (!multipleTextBlocks && state.blocks.size > 0 && state.blocks.last() is TextBlock) {
            val textBlock = state.blocks.last() as TextBlock
            textBlock.textFieldValue =
                textBlock.textFieldValue.copy(annotatedString = buildAnnotatedString {
                    if (textBlock.textFieldValue.text.isNotEmpty()) {
                        append(textBlock.textFieldValue.annotatedString)
                    }
                    append(annotatedString)
                })
        } else {
            state.blocks.add(TextBlock(value = TextFieldValue(annotatedString = annotatedString)))
        }
    }

    fun appendText(text: String) {
        appendAnnotatedString(buildAnnotatedString { append(text) })
    }

    fun appendText(text: Char) {
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
                findChildOfType(MarkdownTokenTypes.FENCE_LANG)?.getTextInNode(content)?.trim()
                    ?.toString() ?: "js"
            val start = children[2].startOffset
            val end = children[children.size - 2].endOffset
            if (lang != "latex") {
                state.blocks.add(
                    CodeBlock(
                        lang = lang,
                        value = content.subSequence(start, end).trim().toString()
                    )
                )
            } else {
                state.blocks.add(
                    MathBlock(
                        latex = content.subSequence(start, end).trim().toString()
                    )
                )
            }
        }
//        MarkdownElementTypes.ATX_1 -> MarkdownHeader(content, this, MaterialTheme.typography.titleLarge, color)
//        MarkdownElementTypes.ATX_2 -> MarkdownHeader(content, this, MaterialTheme.typography.titleMedium, color)
//        MarkdownElementTypes.ATX_3 -> MarkdownHeader(content, this, MaterialTheme.typography.titleMedium, color)
//        MarkdownElementTypes.ATX_4 -> MarkdownHeader(content, this, MaterialTheme.typography.titleMedium, color)
//        MarkdownElementTypes.ATX_5 -> MarkdownHeader(content, this, MaterialTheme.typography.titleMedium, color)
//        MarkdownElementTypes.ATX_6 -> MarkdownHeader(content, this, MaterialTheme.typography.titleMedium, color)
//        MarkdownElementTypes.BLOCK_QUOTE -> MarkdownBlockQuote(content, this, color = color)
        MarkdownElementTypes.PARAGRAPH -> {
            appendAnnotatedString(buildAnnotatedString {
                pushStyle(typography.bodyMedium.toSpanStyle())
                buildMarkdownAnnotatedString(content, this@importIntoState, colors)
                pop()
            })
        }

        MarkdownElementTypes.ORDERED_LIST -> orderedList(
            state = state,
            content = content,
            colors = colors,
            typography = typography,
            appendAnnotatedString = { appendAnnotatedString(it) }
        )

        MarkdownElementTypes.UNORDERED_LIST -> unorderedList(
            state = state,
            content = content,
            colors = colors,
            typography = typography,
            appendAnnotatedString = { appendAnnotatedString(it) }
        )

        MarkdownElementTypes.IMAGE -> {
            val link =
                findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)
                    ?.toString()
            //todo add image
        }

        MarkdownElementTypes.LINK_DEFINITION -> {
            val linkLabel =
                findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getTextInNode(content)?.toString()
            if (linkLabel != null) {
                val destination =
                    findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)
                        ?.toString()
                //todo linklabel and destination
            }
        }

        MarkdownTokenTypes.EOL -> appendText('\n')
//        MarkdownTokenTypes.HARD_LINE_BREAK -> appendText("\n")
        else -> handled = false
    }
    return handled
}

private fun ASTNode.listItems(
    state: EditorState,
    content: String,
    typography: Typography,
    colors: ColorScheme,
    level: Int,
    appendAnnotatedString: (AnnotatedString) -> Unit,
    listItem: ASTNode.(index: Int) -> Unit,
) {
    var index = -1
    for (child in children) {
        index++
        when (child.type) {
            MarkdownElementTypes.LIST_ITEM -> {
                child.listItem(index)
                if (child.children.isNotEmpty()) {
                    child.listItems(
                        state,
                        content = content,
                        typography,
                        colors,
                        level = level,
                        appendAnnotatedString,
                        listItem,
                    )
                }
            }

            MarkdownElementTypes.ORDERED_LIST -> child.orderedList(
                state = state,
                content = content,
                typography,
                colors,
                level = level + 1,
                appendAnnotatedString
            )

            MarkdownElementTypes.UNORDERED_LIST -> child.unorderedList(
                state = state,
                content = content,
                typography,
                colors,
                level = level + 1,
                appendAnnotatedString = appendAnnotatedString
            )
        }
    }
}

internal fun ASTNode.unorderedList(
    state: EditorState,
    content: String,
    typography: Typography,
    colors: ColorScheme,
    level: Int = 0,
    appendAnnotatedString: (AnnotatedString) -> Unit,
) {
    val totalListItems = children.size
    var hasStartedCheckboxes = false
    listItems(
        state = state,
        content = content,
        typography,
        colors,
        level = level,
        appendAnnotatedString = appendAnnotatedString
    ) { listItemIndex ->

        fun AnnotatedString.Builder.putChildren() {
            pushStyle(typography.bodyMedium.toSpanStyle())
            buildMarkdownAnnotatedString(
                content = content,
                children = children.filter { it.type != MarkdownTokenTypes.EOL },
                colors = colors
            )
            pop()
        }

        fun getTextFieldValue(): TextFieldValue {
            return TextFieldValue(annotatedString = buildAnnotatedString {
                pushStyle(typography.bodyMedium.toSpanStyle())
                buildMarkdownAnnotatedString(
                    content = content,
                    children = children.filter { it.type != MarkdownTokenTypes.EOL },
                    colors = colors
                )
                pop()
            })
        }

        fun appendTextListItem(bullet: String) {
            appendAnnotatedString(buildAnnotatedString {
                for (i in 0 until level) append("\t")
                append(" $bullet ")
                putChildren()
                if (listItemIndex < totalListItems - 1) append('\n')
            })
        }

        val bullet = findChildOfType(MarkdownTokenTypes.LIST_BULLET)?.getTextInNode(content)
        if (bullet != null && bullet.trim().isNotEmpty()) {
            if (bullet.trim() == "*") {
                // todo list item block of type bullet is required
                state.blocks.add(
                    ListItemBlock(
                        requestFocus = false,
                        textFieldValue = getTextFieldValue(),
                        isChecked = false,
                        isIndented = level > 0
                    )
                )
                hasStartedCheckboxes = true
            } else if (bullet.trim() == "-") {
                val checkbox = findChildOfType(GFMTokenTypes.CHECK_BOX)?.getTextInNode(content)?.trim()
                if (!checkbox.isNullOrEmpty() || hasStartedCheckboxes) {
                    val isChecked: Boolean = if (checkbox.isNullOrEmpty()) {
                        false
                    } else if (checkbox == "[ ]" || checkbox == "[]") {
                        false
                    } else if (checkbox == "[x]") {
                        true
                    } else {
                        // todo checkbox's value is something else
                        false
                    }
                    state.blocks.add(
                        ListItemBlock(
                            requestFocus = false,
                            textFieldValue = getTextFieldValue(),
                            isChecked = isChecked,
                            isIndented = level > 0
                        )
                    )
                    hasStartedCheckboxes = true
                } else {
                    appendTextListItem("-")
                }
            } else {
                appendTextListItem(bullet.trim().toString())
            }
        } else {
            appendTextListItem(bullet?.toString() ?: "")
        }
    }
}

internal fun ASTNode.orderedList(
    state: EditorState,
    content: String,
    typography: Typography,
    colors: ColorScheme,
    level: Int = 0,
    appendAnnotatedString: (AnnotatedString) -> Unit
) {
    val textBlock = TextBlock()
    textBlock.textFieldValue = TextFieldValue(annotatedString = buildAnnotatedString {
        listItems(
            state = state,
            content = content,
            typography,
            colors,
            level = level,
            appendAnnotatedString
        ) {
            for (i in 0..level) {
                append("\t")
            }
            val listNumber = findChildOfType(MarkdownTokenTypes.LIST_NUMBER)?.getTextInNode(content)
            append(listNumber.toString() + " - ")
            pushStyle(typography.bodyMedium.toSpanStyle())
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