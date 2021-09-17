package com.wakaztahir.markdowntext.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.components.BulletListScope
import com.wakaztahir.markdowntext.preview.components.OrderedListScope
import com.wakaztahir.markdowntext.preview.components.PreviewListScope
import com.wakaztahir.markdowntext.preview.model.LocalMarker
import com.wakaztahir.markdowntext.preview.model.LocalPreviewRenderer
import com.wakaztahir.markdowntext.preview.model.Marker
import com.wakaztahir.markdowntext.preview.model.PreviewRenderer
import org.commonmark.ext.gfm.tables.*
import org.commonmark.ext.task.list.items.TaskListItemMarker
import org.commonmark.node.*

@Composable
fun MarkdownPreview(
    modifier: Modifier = Modifier,
    markdown: String,
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    marker: Marker = LocalMarker.current,
    renderer: PreviewRenderer = LocalPreviewRenderer.current,
) {
    val parser = LocalCommonMarkParser.current
    val parsed = remember(markdown) { parser.parse(markdown) }

    CompositionLocalProvider(LocalPreviewRenderer provides renderer) {
        CompositionLocalProvider(LocalMarker provides marker.apply {
            this.colors = colors
            this.typography = typography
        }) {
            Column(modifier = modifier) {
                MDBlockChildren(parent = parsed)
            }
        }
    }
}


@Composable
internal fun MDBlockChildren(parent: Node) {
    var child = parent.firstChild
    while (child != null) {
        MDBlock(node = child)
        child = child.next
    }
}

@Composable
internal fun MDBlock(node: Node) {
    val marker = LocalMarker.current
    val renderer = LocalPreviewRenderer.current
    when (node) {
        is Document -> MDBlockChildren(node)
        is BlockQuote -> renderer.PreviewBlockQuote {
            appendMarkdownContent(marker, node)
        }
        is ThematicBreak -> {
            // ignoring
        }
        is Heading -> {
            if (node.level in 0..6) {
                renderer.PreviewHeading(
                    isParentDocument = node.parent is Document,
                    level = node.level
                ) {
                    appendMarkdownContent(marker, node)
                }
            } else {
                // not a heading
                MDBlockChildren(parent = node)
            }
        }
        is Paragraph -> {
            if (node.firstChild is Image && node.firstChild == node.lastChild) {
                // Paragraph with single image
                renderer.PreviewImage(
                    destination = (node.firstChild as Image).destination ?: "",
                    title = (node.firstChild as Image).title ?: ""
                )
            } else {
                renderer.PreviewParagraph(isParentDocument = node.parent is Document) {
                    appendMarkdownContent(marker, node)
                }
            }
        }
        is FencedCodeBlock -> renderer.PreviewFencedCodeBlock(
            isParentDocument = node.parent is Document,
            info = node.info ?: "",
            literal = node.literal ?: "",
            fenceChar = node.fenceChar,
            fenceIndent = node.fenceIndent,
            fenceLength = node.fenceLength
        )
        is IndentedCodeBlock -> renderer.PreviewIndentedCodeBlock(
            isParentDocument = node.parent is Document,
            literal = node.literal ?: ""
        )
        is Image -> renderer.PreviewImage(
            destination = node.destination ?: "",
            title = node.title ?: ""
        )
        is BulletList -> renderer.PreviewBulletList(isParentDocument = node.parent is Document) {
            MDListItems(listBlock = node)
        }
        is OrderedList -> renderer.PreviewOrderedList(isParentDocument = node.parent is Document) {
            MDListItems(listBlock = node)
        }
        is HtmlInline -> renderer.PreviewHtmlInline(
            isParentDocument = node.parent is Document,
            literal = node.literal,
        )
        is HtmlBlock -> renderer.PreviewHtmlBlock(
            isParentDocument = node.parent is Document,
            literal = node.literal
        )
        is TableBlock -> {

            // rows contain list of columns
            val rows = mutableListOf<MutableList<AnnotatedString>>()

            extractNodes(
                node,
                foundSection = { section ->
                    extractNodes(section, foundRow = { row ->
                        val list = mutableListOf<AnnotatedString>()
                        extractNodes(row, foundCell = {
                            list.add(
                                buildAnnotatedString {
                                    appendMarkdownContent(marker, it)
                                }
                            )
                        })
                        rows.add(list)
                    })
                },
            )

            renderer.PreviewTable(
                isParentDocument = node.parent is Document,
                rows = rows
            )
        }
    }
}

/**
 * Traverses [ListBlock] children and calls respective
 * Composable functions to render its children
 */
@Composable
internal fun PreviewListScope.MDListItems(listBlock: ListBlock) {
    val marker = LocalMarker.current
    val renderer = LocalPreviewRenderer.current
    var number = 0
    val bulletMarker = if (listBlock is BulletList) listBlock.bulletMarker else ' '
    var listItem = listBlock.firstChild
    while (listItem != null) {
        var child = listItem.firstChild
        while (child != null) {
            when (child) {
                is BulletList -> {
                    renderer.PreviewBulletList(isParentDocument = child.parent is Document) {
                        this.MDListItems(listBlock = child as BulletList)
                    }
                }
                is OrderedList -> {
                    renderer.PreviewOrderedList(isParentDocument = child.parent is Document) {
                        this.MDListItems(listBlock = child as OrderedList)
                    }
                }
                else -> {
                    if (child is TaskListItemMarker && child.next !is BulletList && child.next !is OrderedList) {
                        if (this@MDListItems is BulletListScope) {
                            this@MDListItems.TaskListItem(child.isChecked) {
                                appendMarkdownContent(marker, child?.next)
                            }
                        }
                        child = child.next // skipping next item
                    } else {
                        when (this@MDListItems) {
                            is BulletListScope -> BulletListItem(bulletMarker) {
                                appendMarkdownContent(marker, child)
                            }
                            is OrderedListScope -> OrderedListItem(
                                number = number,
                                delimiter = (listBlock as? OrderedList)?.delimiter ?: ' '
                            ) {
                                appendMarkdownContent(marker, child)
                            }
                        }
                    }
                    number++
                }
            }
            child = child.next
        }
        listItem = listItem.next
    }
}

/**
 * A helper function to recursively extract children nodes
 * from a table
 */
private fun extractNodes(
    node: Node,
    foundSection: (Node) -> Unit = {},
    foundRow: (TableRow) -> Unit = {},
    foundCell: (TableCell) -> Unit = {}
) {
    var child = node.firstChild
    while (child != null) {
        when (child) {
            is TableHead -> foundSection(child)
            is TableBody -> foundSection(child)
            is TableRow -> foundRow(child)
            is TableCell -> foundCell(child)
        }
        child = child.next
    }
}