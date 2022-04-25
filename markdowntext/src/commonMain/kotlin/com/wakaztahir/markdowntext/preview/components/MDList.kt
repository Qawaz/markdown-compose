package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.core.annotation.buildMarkdownAnnotatedString
import com.wakaztahir.markdowntext.utils.MyIcons
import com.wakaztahir.markdowntext.utils.fastForEach
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

@Composable
fun MarkdownListItems(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    level: Int = 0,
    color: Color = Color.Unspecified,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit,
    item: @Composable (child: ASTNode) -> Unit,
) {
    Column(modifier = modifier.padding(start = (8.dp) * level)) {
        node.children.fastForEach { child ->
            when (child.type) {
                MarkdownElementTypes.LIST_ITEM -> {
                    item(child)
                    if (child.children.isNotEmpty()) {
                        MarkdownListItems(node = child, level = level, content = content, item = item, modify = modify)
                    }
                }
                MarkdownElementTypes.ORDERED_LIST -> MDOrderedList(
                    content,
                    child,
                    modifier,
                    level + 1,
                    color,
                    modify = modify
                )
                MarkdownElementTypes.UNORDERED_LIST -> MDBulletList(
                    content,
                    child,
                    modifier,
                    level + 1,
                    color,
                    modify = modify
                )
            }
        }
    }
}

@Composable
fun MDBulletList(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    level: Int = 0,
    color: Color = Color.Unspecified,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit,
) {
    MarkdownListItems(content, node, modifier, level, color, modify = modify) { child ->
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val bullet = child.findChildOfType(MarkdownTokenTypes.LIST_BULLET)?.getTextInNode(content)
            if (bullet != null) {
                if (bullet.trim() == "*") {
                    Icon(
                        modifier = Modifier.offset(x = (-8).dp),
                        painter = MyIcons.CircleSmall,
                        contentDescription = null
                    )
                } else if (bullet.trim() == "-") {
                    val checkbox = child.findChildOfType(GFMTokenTypes.CHECK_BOX)?.getTextInNode(content)?.trim()
                    if (checkbox != null) {
                        if (checkbox == "[ ]" || checkbox == "[]") {
                            Checkbox(checked = false, onCheckedChange = null, enabled = false)
                            Spacer(Modifier.width(8.dp))
                        } else if (checkbox == "[x]") {
                            Checkbox(checked = true, onCheckedChange = null, enabled = false)
                            Spacer(Modifier.width(8.dp))
                        }
                    } else {
                        Text("$bullet")
                    }
                }
            }
            val text = buildAnnotatedString {
                pushStyle(MaterialTheme.typography.body1.toSpanStyle())
                buildMarkdownAnnotatedString(
                    content,
                    child.children.filter { it.type != MarkdownTokenTypes.EOL },
                    MaterialTheme.colors
                )
                pop()
            }
            MDText(
                text,
                modifier.offset(x = if (bullet != null && bullet.trim() == "*") -(12).dp else 0.dp),
                style = MaterialTheme.typography.body1,
                color = color,
                modify = modify
            )
        }
    }
}

@Composable
fun MDOrderedList(
    content: String,
    node: ASTNode,
    modifier: Modifier = Modifier,
    level: Int = 0,
    color: Color = Color.Unspecified,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit,
) {
    MarkdownListItems(content, node, modifier, level, color, modify = modify) { child ->
        Row(Modifier.fillMaxWidth()) {
            Text("${child.findChildOfType(MarkdownTokenTypes.LIST_NUMBER)?.getTextInNode(content)} ")
            val text = buildAnnotatedString {
                pushStyle(MaterialTheme.typography.body1.toSpanStyle())
                buildMarkdownAnnotatedString(
                    content,
                    child.children.filter { it.type != MarkdownTokenTypes.EOL },
                    MaterialTheme.colors
                )
                pop()
            }
            MDText(text, modifier, style = MaterialTheme.typography.body1, color = color, modify = modify)
        }
    }
}


