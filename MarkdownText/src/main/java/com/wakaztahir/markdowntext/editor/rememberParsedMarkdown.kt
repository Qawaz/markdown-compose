package com.wakaztahir.markdowntext.editor

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.common.LocalMarker
import com.wakaztahir.markdowntext.editor.model.*
import com.wakaztahir.markdowntext.editor.model.ListBlock
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
): ParsedMarkdown {

    val parser = LocalCommonMarkParser.current

    return ParsedMarkdown(marker = marker).apply {
        val document = parser.parse(markdown)
        
        // Extracting blocks
        extractChildNodes(this, document)

    }
}

private fun extractChildNodes(
    parsed: ParsedMarkdown,
    parent: Node,
    separateHeadingParagraph: Boolean = false
) {

    var node = parent.firstChild
    while (node != null) {

        // Adding Heading & Paragraph in Text Block
        if (separateHeadingParagraph) {
            when (node) {
                is Heading, is Paragraph -> {
                    val annotatedString = buildAnnotatedString {
                        appendMarkdownNode(parsed.marker, node)
                        toAnnotatedString()
                    }
                    parsed.items.add(
                        if (node is Heading) {
                            HeadingBlock(annotatedString = annotatedString)
                        } else {
                            ParagraphBlock(annotatedString = annotatedString)
                        }
                    )
                }
            }
        } else {
            when (node) {
                is Heading, is Paragraph -> {
                    val textBlock =
                        if (parsed.items.isNotEmpty() && parsed.items.last() is TextBlock) {
                            parsed.items.last() as TextBlock
                        } else {
                            TextBlock(buildAnnotatedString { toAnnotatedString() }).apply {
                                parsed.items.add(this)
                            }
                        }
                    textBlock.text = buildAnnotatedString {
                        append(textBlock.text)
                        appendMarkdownNode(parsed.marker, node)
                        toAnnotatedString()
                    }
                }
            }
        }

        // Adding Other Blocks
        when (node) {
            is Document -> extractChildNodes(parsed = parsed, node)
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
                parsed.items.add(ListBlock())
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