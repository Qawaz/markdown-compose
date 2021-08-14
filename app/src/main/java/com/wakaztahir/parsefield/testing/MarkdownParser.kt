package com.wakaztahir.parsefield.testing

fun parseMarkdown(markdown: String) {
//    val parser = Parser.builder()
//        .extensions(
//            listOf(
//                TablesExtension.create(),
//                StrikethroughExtension.create()
//            )
//        ).build()
//    val node = parser.parse(markdown)
//    node.sourceSpans
//    when (node) {
//        is BlockQuote -> AstBlockQuote
//        is BulletList -> AstBulletList(bulletMarker = node.bulletMarker)
//        is Code -> AstCode(literal = node.literal)
//        is Document -> AstDocument
//        is Emphasis -> AstEmphasis(delimiter = node.openingDelimiter)
//        is FencedCodeBlock -> AstFencedCodeBlock(
//            literal = node.literal,
//            fenceChar = node.fenceChar,
//            fenceIndent = node.fenceIndent,
//            fenceLength = node.fenceLength,
//            info = node.info
//        )
//        is HardLineBreak -> AstHardLineBreak
//        is Heading -> AstHeading(
//            level = node.level
//        )
//        is ThematicBreak -> AstThematicBreak
//        is HtmlInline -> AstHtmlInline(
//            literal = node.literal
//        )
//        is HtmlBlock -> AstHtmlBlock(
//            literal = node.literal
//        )
//        is Image -> AstImage(
//            title = node.title,
//            destination = node.destination
//        )
//        is IndentedCodeBlock -> AstIndentedCodeBlock(
//            literal = node.literal
//        )
//        is Link -> AstLink(
//            title = node.title ?: "",
//            destination = node.destination
//        )
//        is ListItem -> AstListItem
//        is OrderedList -> AstOrderedList(
//            startNumber = node.startNumber,
//            delimiter = node.delimiter
//        )
//        is Paragraph -> AstParagraph
//        is SoftLineBreak -> AstSoftLineBreak
//        is StrongEmphasis -> AstStrongEmphasis(
//            delimiter = node.openingDelimiter
//        )
//        is Text -> AstText(
//            literal = node.literal
//        )
//        is LinkReferenceDefinition -> AstLinkReferenceDefinition(
//            title = node.title ?: "",
//            destination = node.destination,
//            label = node.label
//        )
//        is TableBlock -> AstTableRoot
//        is TableHead -> AstTableHeader
//        is TableBody -> AstTableBody
//        is TableRow -> AstTableRow
//        is TableCell -> AstTableCell(
//            header = node.isHeader,
//            alignment = when (node.alignment) {
//                LEFT -> AstTableCellAlignment.LEFT
//                CENTER -> AstTableCellAlignment.CENTER
//                RIGHT -> AstTableCellAlignment.RIGHT
//                null -> AstTableCellAlignment.LEFT
//            }
//        )
//        is Strikethrough -> AstStrikethrough(
//            node.openingDelimiter
//        )
//        is CustomNode -> null
//        is CustomBlock -> null
//        else -> null
//    }
}

