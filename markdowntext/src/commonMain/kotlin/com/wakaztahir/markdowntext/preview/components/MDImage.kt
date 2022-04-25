package com.wakaztahir.markdowntext.preview.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowntext.utils.imagePainter
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

@Composable
internal fun MDImage(content: String, node: ASTNode) {
    val link = node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(content)?.toString()
        ?: return

    Spacer(Modifier.padding(4.dp))

    imagePainter(link)?.let { painter ->
        Image(
            painter = painter,
            contentDescription = "Image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(Modifier.padding(4.dp))
}