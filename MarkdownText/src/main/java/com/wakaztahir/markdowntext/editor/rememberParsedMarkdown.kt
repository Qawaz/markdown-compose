package com.wakaztahir.markdowntext.editor

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.editor.model.*
import com.wakaztahir.markdowntext.editor.model.ListBlock
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownNode
import com.wakaztahir.markdowntext.preview.model.LocalMarker
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.task.list.items.TaskListItemMarker
import org.commonmark.node.*
import org.commonmark.node.ListItem

val LocalParsedMarkdown = compositionLocalOf { ParsedMarkdown(Marker()) }

class ParsedMarkdown(val marker: Marker) {
    val items = mutableStateListOf<EditableBlock>()
}

/**
 * Parses your [markdown] into given [parsed] or creates a new instance of [ParsedMarkdown]
 */
@Composable
fun rememberParsedMarkdown(
    markdown: String,
    parsed: ParsedMarkdown = ParsedMarkdown(LocalMarker.current),
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    separateHeadingParagraph: Boolean = false,
    bulletListInsideTextBlock: Boolean = false,
    orderedListInsideTextBlock: Boolean = true,
): ParsedMarkdown {

    val parser = LocalCommonMarkParser.current

    return parsed.apply {

        this.marker.apply {
            this.colors = colors
            this.typography = typography
        }

        val document = parser.parse(markdown)

        // Extracting blocks
        extractChildNodes(
            parsed = this,
            parent = document,
            separateHeadingParagraph = separateHeadingParagraph,
            bulletListInsideTextBlock = bulletListInsideTextBlock,
            orderedListInsideTextBlock = orderedListInsideTextBlock,
        )
    }
}

private fun extractChildNodes(
    parsed: ParsedMarkdown,
    parent: Node,
    separateHeadingParagraph: Boolean,
    bulletListInsideTextBlock: Boolean,
    orderedListInsideTextBlock: Boolean,
) {

    fun getLastTextBlock(): TextBlock {
        return if (parsed.items.isNotEmpty() && parsed.items.last() is TextBlock) {
            parsed.items.last() as TextBlock
        } else {
            TextBlock(AnnotatedString("")).apply { parsed.items.add(this) }
        }
    }

    fun getLastListBlock(): ListBlock {
        return if (parsed.items.isNotEmpty() && parsed.items.last() is ListBlock) {
            parsed.items.last() as ListBlock
        } else {
            ListBlock().apply { parsed.items.add(this) }
        }
    }

    fun appendToLastTextBlock(builder: AnnotatedString.Builder.() -> Unit) {
        getLastTextBlock().let {
            it.textValue = it.textValue.copy(buildAnnotatedString {
                append(it.textValue.annotatedString)
                builder()
            })
        }
    }

    var node = parent.firstChild
    while (node != null) {


        // Adding Other Blocks
        when (node) {
            is Document -> extractChildNodes(
                parsed,
                node,
                separateHeadingParagraph = separateHeadingParagraph,
                bulletListInsideTextBlock = bulletListInsideTextBlock,
                orderedListInsideTextBlock = orderedListInsideTextBlock
            )
            is Heading, is Paragraph -> {
                if (separateHeadingParagraph) {
                    val annotatedString = buildAnnotatedString {
                        appendMarkdownContent(parsed.marker, node)
                    }
                    parsed.items.add(
                        if (node is Heading) {
                            HeadingBlock(annotatedString, node.level)
                        } else {
                            ParagraphBlock(annotatedString)
                        }
                    )
                } else {
                    appendToLastTextBlock {
                        appendMarkdownNode(parsed.marker, node)
                    }
                }
            }
            is BlockQuote -> {
                parsed.items.add(QuoteBlock())
            }
            is ThematicBreak -> {
                // ignoring
            }
            is FencedCodeBlock -> {
                parsed.items.add(CodeBlock())
            }
            is IndentedCodeBlock -> {
                parsed.items.add(CodeBlock())
            }
            is Image -> {
                parsed.items.add(ImageBlock())
            }
            is BulletList -> {
                if (bulletListInsideTextBlock) {
                    appendToLastTextBlock {
                        append(
                            (node as BulletList).toAnnotatedList(parsed.marker)
                        )
                    }
                } else {
                    getLastListBlock().append(parsed.marker, list = node)
                }
            }
            is OrderedList -> {
                if (orderedListInsideTextBlock) {
                    appendToLastTextBlock {
                        append(
                            (node as OrderedList).toAnnotatedList(parsed.marker)
                        )
                    }
                } else {
                    getLastListBlock().append(parsed.marker, list = node)
                }
            }
            is HtmlInline -> {
                //ignoring for now
            }
            is HtmlBlock -> {
                //ignoring for now
            }
            is TableBlock -> {
                //ignoring for now
            }
        }
        node = node.next
    }
}

/**
 * Appends children of node [BulletList] or [OrderedList] , if they are [ListItem]
 * to [ListBlock]
 */
private fun ListBlock.append(
    marker: Marker,
    indentationLevel: Int = 0,
    list: org.commonmark.node.ListBlock
) {
    var parent = list.firstChild
    while (parent != null) {
        var child = parent.firstChild
        var number = if (list is OrderedList) list.startNumber else 0
        while (child != null) {
            when (child) {
                is OrderedList -> append(marker, indentationLevel + 1, child)
                is BulletList -> append(marker, indentationLevel + 1, child)
                is TaskListItemMarker -> {
                    if (child.next !is BulletList && child.next !is OrderedList) {
                        this.items.add(
                            TaskListItem(
                                child.isChecked,
                                indentationLevel,
                                annotatedString = buildAnnotatedString {
                                    appendMarkdownContent(
                                        marker,
                                        child.next
                                    )
                                }
                            )
                        )
                        // skipping next node as it was item and has been added to list
                        child = child.next
                    }
                }
                else -> {
                    when (list) {
                        is BulletList -> {
                            this.items.add(
                                BulletListItem(
                                    indentationLevel,
                                    list.bulletMarker.toString(),
                                    annotatedString = buildAnnotatedString {
                                        appendMarkdownContent(
                                            marker,
                                            child
                                        )
                                    }
                                )
                            )
                        }
                        is OrderedList -> {
                            this.items.add(
                                OrderedListItem(
                                    indentationLevel,
                                    number = number,
                                    delimiter = list.delimiter.toString(),
                                    annotatedString = buildAnnotatedString {
                                        appendMarkdownContent(
                                            marker,
                                            child
                                        )
                                    }
                                )
                            )
                        }
                    }
                    number++
                }
            }
            child = child.next
        }
        parent = parent.next
    }
}

/**
 * Converts a [BulletList] and its children [OrderedList] or [BulletList] or [ListItem] to [AnnotatedString]
 */
private fun BulletList.toAnnotatedList(marker: Marker): AnnotatedString {
    val list = this
    return buildAnnotatedString {
        var child = list.firstChild
        while (child != null) {
            when (child) {
                is BulletList -> append(child.toAnnotatedList(marker))
                is OrderedList -> append(child.toAnnotatedList(marker))
                is ListItem -> {
                    append("${list.bulletMarker} ${appendMarkdownContent(marker, child)}")
                }
            }
            child = child.next
        }
    }
}

/**
 * Converts a [OrderedList] and its children [OrderedList] or [BulletList] or [ListItem] to [AnnotatedString]
 */
private fun OrderedList.toAnnotatedList(marker: Marker): AnnotatedString {
    val list = this
    return buildAnnotatedString {
        var child = list.firstChild
        var x = list.startNumber
        while (child != null) {
            when (child) {
                is BulletList -> append(child.toAnnotatedList(marker))
                is OrderedList -> append(child.toAnnotatedList(marker))
                is ListItem -> {
                    append("$x. ")
                    appendMarkdownContent(marker, child)
                    x++
                }
            }
            child = child.next
        }
    }
}