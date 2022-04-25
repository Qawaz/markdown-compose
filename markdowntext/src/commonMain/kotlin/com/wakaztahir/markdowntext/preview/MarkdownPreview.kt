package com.wakaztahir.markdowntext.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.preview.components.*
import com.wakaztahir.markdowntext.utils.LocalReferenceLinkHandler
import com.wakaztahir.markdowntext.utils.ReferenceLinkHandlerImpl
import com.wakaztahir.markdowntext.utils.fastForEach
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

@Composable
fun MarkdownPreview(
    modifier: Modifier = Modifier,
    markdown: String,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit = { append(it) }
) = runCatching {
    val parser = MarkdownParser(GFMFlavourDescriptor())
    val tree = parser.buildMarkdownTreeFromString(markdown)
    val linkHandler = ReferenceLinkHandlerImpl()

    CompositionLocalProvider(LocalReferenceLinkHandler provides linkHandler) {
        Column(modifier = modifier) {
            tree.children.fastForEach { node ->
                if (!node.handleElement(markdown, MaterialTheme.colors.onBackground, modify)) {
                    node.children.fastForEach { child ->
                        child.handleElement(markdown, MaterialTheme.colors.onBackground, modify)
                    }
                }
            }
        }
    }
}.onFailure { it.printStackTrace() }

@Composable
private fun ASTNode.handleElement(
    content: String,
    color: Color = Color.Unspecified,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit
): Boolean {
    var handled = true
    when (type) {
        MarkdownTokenTypes.TEXT -> Text(getTextInNode(content).toString(), color = color)
        MarkdownTokenTypes.EOL -> Spacer(Modifier.padding(4.dp))
        MarkdownElementTypes.CODE_FENCE -> MarkdownCodeFence(content, this, color = color, modify = modify)
        MarkdownElementTypes.ATX_1 -> MarkdownHeader(content, this, MaterialTheme.typography.h2, color)
        MarkdownElementTypes.ATX_2 -> MarkdownHeader(content, this, MaterialTheme.typography.h3, color)
        MarkdownElementTypes.ATX_3 -> MarkdownHeader(content, this, MaterialTheme.typography.h4, color)
        MarkdownElementTypes.ATX_4 -> MarkdownHeader(content, this, MaterialTheme.typography.h5, color)
        MarkdownElementTypes.ATX_5 -> MarkdownHeader(content, this, MaterialTheme.typography.h6, color)
        MarkdownElementTypes.ATX_6 -> MarkdownHeader(content, this, MaterialTheme.typography.h6, color)
        MarkdownElementTypes.BLOCK_QUOTE -> MarkdownBlockQuote(content, this, color = color)
        MarkdownElementTypes.PARAGRAPH -> MarkdownParagraph(content, this, color, modify = modify)
        MarkdownElementTypes.ORDERED_LIST -> MDOrderedList(content, this, color = color, modify = modify)
        MarkdownElementTypes.UNORDERED_LIST -> MDBulletList(content, this, color = color, modify = modify)
        MarkdownElementTypes.IMAGE -> MDImage(content, this)
        MarkdownElementTypes.LINK_DEFINITION -> {
            val linkLabel = findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getTextInNode(content)?.toString()
            if (linkLabel != null) {
                val destination = findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)?.toString()
                LocalReferenceLinkHandler.current.store(linkLabel, destination)
            }
        }
        else -> handled = false
    }
    return handled
}