package com.wakaztahir.markdowncompose.neweditor

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowncompose.core.annotation.buildMarkdownAnnotatedString
import com.wakaztahir.markdowncompose.preview.LocalMarkdownPreviewConfiguration
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

@Composable
private fun ASTNode.handleElement(
    builder: (AnnotatedString) -> Unit,
    content: String,
    color: Color
): Boolean {
    var handled = true

    val colorScheme = MaterialTheme.colorScheme

    fun AnnotatedString.Builder.appendWithStyle(style: TextStyle, nodeChildren: List<ASTNode> = children) {
        pushStyle(style.toParagraphStyle())
        pushStyle(style.toSpanStyle().copy(color = color))
        buildMarkdownAnnotatedString(
            content,
            nodeChildren,
            colorScheme
        )
        pop()
        pop()
    }

    fun appendHeading(style: TextStyle) {
        builder(buildAnnotatedString {
            append('\n') // append padding top = 16.dp
            findChildOfType(MarkdownTokenTypes.ATX_CONTENT)?.let { con ->
                appendWithStyle(style = style, nodeChildren = con.children.filter { it.type != MarkdownTokenTypes.EOL })
            }
        })
    }

    when (type) {
        MarkdownTokenTypes.TEXT -> {
            builder(buildAnnotatedString {
                pushStyle(SpanStyle(color = color))
                append(getTextInNode(content).toString())
                pop()
            })
        }

        MarkdownTokenTypes.EOL -> builder(buildAnnotatedString {
            pushStyle(SpanStyle(color = color))
            append('\n')
            pop()
        })

        MarkdownElementTypes.CODE_FENCE -> {

        }

        MarkdownElementTypes.ATX_1 -> appendHeading(MaterialTheme.typography.headlineLarge)
        MarkdownElementTypes.ATX_2 -> appendHeading(MaterialTheme.typography.headlineMedium)
        MarkdownElementTypes.ATX_3 -> appendHeading(MaterialTheme.typography.headlineSmall)
        MarkdownElementTypes.ATX_4 -> appendHeading(MaterialTheme.typography.titleLarge)
        MarkdownElementTypes.ATX_5 -> appendHeading(MaterialTheme.typography.titleMedium)
        MarkdownElementTypes.ATX_6 -> appendHeading(MaterialTheme.typography.titleSmall)
        MarkdownElementTypes.BLOCK_QUOTE -> {

        }
        MarkdownElementTypes.PARAGRAPH -> builder(buildAnnotatedString { appendWithStyle(MaterialTheme.typography.bodyMedium) })
        MarkdownElementTypes.ORDERED_LIST -> orderedList(
            content = content,
            colors = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            appendAnnotatedString = builder
        )

        MarkdownElementTypes.UNORDERED_LIST -> unorderedList(
            content = content,
            colors = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            appendAnnotatedString = builder
        )
        MarkdownElementTypes.LINK_DEFINITION -> {
            val linkLabel =
                findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getTextInNode(content)
                    ?.toString()
            if (linkLabel != null) {
                val destination =
                    findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(
                        content
                    )?.toString()
                LocalMarkdownPreviewConfiguration.current.referenceLinkHandler.store(
                    linkLabel,
                    destination
                )
            }
        }

        else -> handled = false
    }
    return handled
}

internal fun ASTNode.listItems(
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
                content = content,
                typography,
                colors,
                level = level + 1,
                appendAnnotatedString
            )

            MarkdownElementTypes.UNORDERED_LIST -> child.unorderedList(
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
    content: String,
    typography: Typography,
    colors: ColorScheme,
    level: Int = 0,
    appendAnnotatedString: (AnnotatedString) -> Unit,
) {
    val totalListItems = children.size
    var hasStartedCheckboxes = false
    listItems(
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
                appendTextListItem("*")
                hasStartedCheckboxes = true
            } else if (bullet.trim() == "-") {
                val checkbox = findChildOfType(GFMTokenTypes.CHECK_BOX)?.getTextInNode(content)?.trim()
                if (!checkbox.isNullOrEmpty() || hasStartedCheckboxes) {
                    val isChecked: Boolean = if (checkbox.isNullOrEmpty()) {
                        false
                    } else if (checkbox == "[ ]" || checkbox == "[]") {
                        false
                    } else checkbox == "[x]"
                    appendTextListItem(if(isChecked) "- [x]" else "- [ ]")
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
    content: String,
    typography: Typography,
    colors: ColorScheme,
    level: Int = 0,
    appendAnnotatedString: (AnnotatedString) -> Unit
) {
    appendAnnotatedString(buildAnnotatedString {
        listItems(
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
}