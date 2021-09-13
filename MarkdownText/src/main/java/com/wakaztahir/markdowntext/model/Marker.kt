package com.wakaztahir.markdowntext.model

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import com.wakaztahir.markdowntext.annotation.createDefaultInlineTextContent
import com.wakaztahir.markdowntext.utils.*
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.*
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
    internal val emphasisStyle: (Emphasis) -> SpanStyle = ::defaultEmphasisStyle,
    internal val strongEmphasisStyle: (StrongEmphasis) -> SpanStyle = ::defaultStrongEmphasisStyle,
    internal val linkStyle : (Link)->SpanStyle = ::defaultLinkStyle,
    internal val headingStyle: (Heading) -> SpanStyle = ::defaultHeadingStyle,
    internal val strikethroughStyle: (Strikethrough) -> SpanStyle = ::defaultStrikethroughStyle,
    internal val blockQuoteStyle : (BlockQuote) -> SpanStyle = ::defaultBlockQuoteStyle,
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