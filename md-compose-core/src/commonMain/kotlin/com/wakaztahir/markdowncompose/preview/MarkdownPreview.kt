package com.wakaztahir.markdowncompose.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.core.MarkdownPreviewConfiguration
import com.wakaztahir.markdowncompose.preview.components.*
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode


val LocalMarkdownPreviewConfiguration = staticCompositionLocalOf<MarkdownPreviewConfiguration> {
    error("CompositionLocal MarkdownPreviewConfiguration not present")
}


@Composable
fun MarkdownPreview(
    modifier: Modifier = Modifier,
    configuration: MarkdownPreviewConfiguration = remember { MarkdownPreviewConfiguration() },
    markdown: String
) {

    val tree = remember(configuration.parser, markdown) {
        configuration.parser.buildMarkdownTreeFromString(markdown)
    }

    CompositionLocalProvider(LocalMarkdownPreviewConfiguration provides configuration) {
        Column(modifier = modifier) {
            for (node in tree.children) {
                if (!node.handleElement(
                        configuration,
                        markdown,
                        MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    for (child in node.children) {
                        child.handleElement(
                            configuration,
                            markdown,
                            MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ASTNode.handleElement(
    configuration: MarkdownPreviewConfiguration,
    content: String,
    color: Color
): Boolean {
    var handled = true
    when (type) {
        MarkdownTokenTypes.TEXT -> Text(getTextInNode(content).toString(), color = color)
        MarkdownTokenTypes.EOL -> Spacer(Modifier.padding(4.dp))
        MarkdownElementTypes.CODE_FENCE -> {
            configuration.extensions[type]?.Render(content, this, color)
        }

        MarkdownElementTypes.ATX_1 -> MarkdownHeader(
            content,
            this,
            MaterialTheme.typography.headlineLarge,
            color
        )

        MarkdownElementTypes.ATX_2 -> MarkdownHeader(
            content,
            this,
            MaterialTheme.typography.headlineMedium,
            color
        )

        MarkdownElementTypes.ATX_3 -> MarkdownHeader(
            content,
            this,
            MaterialTheme.typography.headlineSmall,
            color
        )

        MarkdownElementTypes.ATX_4 -> MarkdownHeader(
            content,
            this,
            MaterialTheme.typography.titleLarge,
            color
        )

        MarkdownElementTypes.ATX_5 -> MarkdownHeader(
            content,
            this,
            MaterialTheme.typography.titleMedium,
            color
        )

        MarkdownElementTypes.ATX_6 -> MarkdownHeader(
            content,
            this,
            MaterialTheme.typography.titleSmall,
            color
        )

        MarkdownElementTypes.BLOCK_QUOTE -> MarkdownBlockQuote(content, this, color = color)
        MarkdownElementTypes.PARAGRAPH -> MarkdownParagraph(content, this, color)
        MarkdownElementTypes.ORDERED_LIST -> MDOrderedList(configuration, content, this, color = color)
        MarkdownElementTypes.UNORDERED_LIST -> MDBulletList(configuration, content, this, color = color)
        MarkdownElementTypes.IMAGE -> MDImage(content, this)
        MarkdownElementTypes.LINK_DEFINITION -> {
            val linkLabel =
                findChildOfType(MarkdownElementTypes.LINK_LABEL)?.getTextInNode(content)
                    ?.toString()
            if (linkLabel != null) {
                val destination =
                    findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(
                        content
                    )
                        ?.toString()
                LocalMarkdownPreviewConfiguration.current.referenceLinkHandler.store(
                    linkLabel,
                    destination
                )
            }
        }

        else -> handled = false
    }
    return handled
}