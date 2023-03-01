package com.wakaztahir.markdowncompose.preview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.core.MarkdownPreviewConfiguration
import com.wakaztahir.markdowncompose.core.annotation.buildMarkdownAnnotatedString
import compose.icons.MaterialDesignIcons
import compose.icons.materialdesignicons.CircleSmall
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

@Composable
fun MarkdownListItems(
    configuration: MarkdownPreviewConfiguration,
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    level: Int = 0,
    color: Color,
    item: @Composable (child: ASTNode) -> Unit,
) {
    Column(modifier = modifier.padding(start = (16.dp) * level)) {
        for (child in node.children) {
            when (child.type) {
                MarkdownElementTypes.LIST_ITEM -> {
                    item(child)
                    if (child.children.isNotEmpty()) {
                        MarkdownListItems(
                            configuration,
                            node = child,
                            level = level,
                            content = content,
                            color = color,
                            item = item
                        )
                    }
                }

                MarkdownElementTypes.ORDERED_LIST -> MDOrderedList(
                    configuration,
                    content,
                    child,
                    modifier,
                    level + 1,
                    color
                )

                MarkdownElementTypes.UNORDERED_LIST -> MDBulletList(
                    configuration,
                    content,
                    child,
                    modifier,
                    level + 1,
                    color
                )
            }
        }
    }
}

@Composable
fun MDBulletList(
    configuration: MarkdownPreviewConfiguration,
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    level: Int = 0,
    color: Color,
) {
    MarkdownListItems(configuration, content, node, modifier, level, color) { child ->
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            // text inside the list item
            val text = buildAnnotatedString {
                pushStyle(MaterialTheme.typography.bodyMedium.toSpanStyle())
                buildMarkdownAnnotatedString(
                    content,
                    child.children.filter { it.type != MarkdownTokenTypes.EOL },
                    MaterialTheme.colorScheme
                )
                pop()
            }

            val bulletNode = child.findChildOfType(MarkdownTokenTypes.LIST_BULLET)
            val bullet = bulletNode?.getTextInNode(content)?.trim()
            if (bullet != null) {
                if (bullet == "*") {
                    Icon(
                        modifier = Modifier.offset(x = (-8).dp),
                        imageVector = MaterialDesignIcons.CircleSmall,
                        tint = color,
                        contentDescription = null
                    )
                } else if (bullet == "-") {
                    val checkboxNode = child.findChildOfType(GFMTokenTypes.CHECK_BOX)
                    val checked = when (checkboxNode?.getTextInNode(content)?.trim()) {
                        "[ ]", "[]" -> false
                        "[x]" -> true
                        else -> null
                    }
                    if (checked != null) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                configuration.onCheckboxClick?.invoke(
                                    it,
                                    content.substring(startIndex = 0, endIndex = checkboxNode!!.startOffset) +
                                            (if (it) "[x] " else "[ ] ") +
                                            content.substring(startIndex = checkboxNode.endOffset)
                                )
                            },
                            enabled = configuration.onCheckboxClick != null
                        )
                        Spacer(Modifier.width(4.dp))
                    } else {
                        Text("$bullet", color = color)
                    }
                } else {
                    println(bullet)
                }
            }

            MDText(
                text,
                modifier.offset(x = if (bullet != null && bullet == "*") -(12).dp else 0.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}

@Composable
fun MDOrderedList(
    configuration: MarkdownPreviewConfiguration,
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    level: Int = 0,
    color: Color = Color.Unspecified
) {
    MarkdownListItems(configuration, content, node, modifier, level, color) { child ->
        Row(Modifier.fillMaxWidth()) {
            Text("${child.findChildOfType(MarkdownTokenTypes.LIST_NUMBER)?.getTextInNode(content)} ",color = color)
            val text = buildAnnotatedString {
                pushStyle(MaterialTheme.typography.bodyMedium.toSpanStyle())
                buildMarkdownAnnotatedString(
                    content,
                    child.children.filter { it.type != MarkdownTokenTypes.EOL },
                    MaterialTheme.colorScheme
                )
                pop()
            }
            MDText(text, modifier, style = MaterialTheme.typography.bodyMedium, color = color)
        }
    }
}