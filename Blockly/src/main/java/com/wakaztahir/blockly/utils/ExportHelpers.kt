package com.wakaztahir.blockly.utils

import com.wakaztahir.blockly.model.Block
import com.wakaztahir.blockly.model.CodeBlock
import com.wakaztahir.blockly.model.MathBlock

object BlocklyExport {
    fun exportToText(blocks: List<Block>): String {
        return blocks.joinToString("\n") { it.exportText() }
    }

    fun exportToMarkdown(blocks: List<Block>): String {
        return blocks.joinToString("\n") { it.exportMarkdown() }
    }

    fun exportToHtml(blocks: List<Block>): String {
        var html = blocks.joinToString("\n") { it.exportHTML() }
        if (blocks.indexOfFirst { it is MathBlock } > -1) {
            html += "<script src='https://cdn.jsdelivr.net/npm/mathjax@3.0.0/es5/tex-svg.js'></script>"
        }
        if (blocks.indexOfFirst { it is CodeBlock } > -1) {
            html += """
                <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/highlight.js/11.2.0/styles/default.min.css">
                <script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/11.2.0/highlight.min.js"></script>
                <script>hljs.highlightAll();</script>
            """.trimIndent()
        }
        return html
    }
}