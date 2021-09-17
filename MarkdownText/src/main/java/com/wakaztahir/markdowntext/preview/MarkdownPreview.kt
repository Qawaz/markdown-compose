package com.wakaztahir.markdowntext.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.markdowntext.common.LocalCodeTheme
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.components.MDTable
import com.wakaztahir.markdowntext.preview.model.*
import org.commonmark.ext.gfm.tables.TableBlock
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
    val isLight = MaterialTheme.colors.isLight
    val codeTheme = remember(isLight) {
        if (isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme()
    }

    CompositionLocalProvider(LocalPreviewRenderer provides renderer) {
        CompositionLocalProvider(LocalCodeTheme provides codeTheme) {
            CompositionLocalProvider(LocalMarker provides marker.apply {
                this.colors = colors
                this.typography = typography
                this.preventBulletMarker = false
            }) {
                Column(modifier = modifier) {
                    MDBlockChildren(parent = parsed)
                }
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
        is HtmlInline -> {

        }
        is HtmlBlock -> {

        }
        is TableBlock -> MDTable(node = node)
    }
}

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
                                appendMarkdownContent(marker, child.next)
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