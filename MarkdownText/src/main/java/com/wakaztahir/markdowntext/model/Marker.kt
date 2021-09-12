package com.wakaztahir.markdowntext.model

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.SpanStyle
import com.wakaztahir.markdowntext.core.createDefaultInlineTextContent
import com.wakaztahir.markdowntext.utils.*
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.Emphasis
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.StrongEmphasis
import org.commonmark.parser.Parser

/**
 * Abstract class every block model inherits from
 */
abstract class BlockData

/**
 * Marker class that provides the markdown to annotated string and annotated string to markdown
 * functionality
 */
class Marker constructor(
    // Default Variables
    internal val parser: Parser = createDefaultParser(),
    // Span Creating Functions
    internal val emphasisSpan: (Emphasis) -> SpanStyle = ::defaultEmphasisSpan,
    internal val strongEmphasisSpan: (StrongEmphasis) -> SpanStyle = ::defaultStrongEmphasisSpan,
    internal val headingSpan: (Heading) -> SpanStyle = ::defaultHeadingSpan,
    internal val strikethroughSpan: (Strikethrough) -> SpanStyle = ::defaultStrikethroughSpan,
    // Inline Text Content
    var blocks: MutableMap<String, BlockData> = mutableMapOf(),
    var inlineContent: Map<String, InlineTextContent> = createDefaultInlineTextContent(blocks)
)

/**
 * parses the [input] markdown string to [Node]
 */
fun Marker.parse(input: String): Node {
    return parser.parse(input)
}