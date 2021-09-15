package com.wakaztahir.markdowntext.editor

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.editor.model.*
import com.wakaztahir.markdowntext.editor.model.ListBlock
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownNode
import com.wakaztahir.markdowntext.preview.model.Marker
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.*

class ParsedMarkdown(val marker: Marker) {
    val items = mutableStateListOf<EditableBlock>()
}

@Composable
fun rememberParsedMarkdown(
    markdown: String,
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    marker: Marker = LocalMarker.current.apply {
        this.colors = colors
        this.typography = typography
    },
    separateHeadingParagraph: Boolean = false,
    orderedListInsideTextBlock: Boolean = false,
): ParsedMarkdown {

    val parser = LocalCommonMarkParser.current

    return ParsedMarkdown(marker = marker).apply {
        val document = parser.parse(markdown)

        // Extracting blocks
        extractChildNodes(
            parsed = this,
            parent = document,
            separateHeadingParagraph = separateHeadingParagraph,
            orderedListInsideTextBlock = orderedListInsideTextBlock,
        )
    }
}

private fun extractChildNodes(
    parsed: ParsedMarkdown,
    parent: Node,
    separateHeadingParagraph: Boolean,
    orderedListInsideTextBlock: Boolean,
) {

    fun getLastTextBlock(): TextBlock {
        return if (parsed.items.isNotEmpty() && parsed.items.last() is TextBlock) {
            parsed.items.last() as TextBlock
        } else {
            TextBlock(AnnotatedString("")).apply { parsed.items.add(this) }
        }
    }

    var node = parent.firstChild
    while (node != null) {


        // Adding Other Blocks
        when (node) {
            is Document -> extractChildNodes(parsed, node, separateHeadingParagraph,orderedListInsideTextBlock)
            is Heading, is Paragraph -> {
                if (separateHeadingParagraph) {
                    val annotatedString = buildAnnotatedString {
                        appendMarkdownNode(parsed.marker, node)
                    }
                    parsed.items.add(
                        if (node is Heading) {
                            HeadingBlock(annotatedString)
                        } else {
                            ParagraphBlock(annotatedString)
                        }
                    )
                } else {
                    getLastTextBlock().let {
                        it.text = buildAnnotatedString {
                            append(it.text)
                            appendMarkdownNode(parsed.marker, node)
                        }
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
                parsed.items.add(ListBlock())
            }
            is OrderedList -> {
                if (orderedListInsideTextBlock) {
                    //todo if a bullet list is encountered in middle
                    //todo the bullet list should cut the current text block and append next ordered list onto the text
                    getLastTextBlock().let {
                        it.text = buildAnnotatedString {
                            append(it.text)
                            append(
                                (node as OrderedList).toAnnotatedString(
                                    parsed.marker,
                                    onBulletList = {
                                        //todo add bullet list
                                    })
                            )
                        }
                    }
                } else {
                    // todo handle ordered list
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

fun OrderedList.toAnnotatedString(
    marker: Marker,
    onBulletList: (BulletList) -> Unit
): AnnotatedString {
    val list = this
    return buildAnnotatedString {
        var child = list.firstChild
        var x = list.startNumber
        while (child != null) {
            when (child) {
                is BulletList -> onBulletList(child)
                is OrderedList -> append(
                    child.toAnnotatedString(
                        marker,
                        onBulletList = onBulletList
                    )
                )
                is ListItem -> {
                    append("$x. ${appendMarkdownContent(marker, child)}")
                    x++
                }
            }
            child = child.next
        }
    }
}