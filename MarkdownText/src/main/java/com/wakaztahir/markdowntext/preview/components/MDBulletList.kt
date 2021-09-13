package com.wakaztahir.markdowntext.preview.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowntext.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.LocalMarker
import com.wakaztahir.markdowntext.preview.MarkdownText
import org.commonmark.node.BulletList

@Composable
fun MDBulletList(bulletList: BulletList, modifier: Modifier = Modifier) {
    val mdMarker = LocalMarker.current
    val marker = bulletList.bulletMarker
    val body1 = MaterialTheme.typography.body1
    MDListItems(bulletList, modifier = modifier) {
        val text = buildAnnotatedString {
            pushStyle(body1.toSpanStyle())
            append("$marker ")
            appendMarkdownContent(mdMarker, it)
            pop()
            toAnnotatedString()

        }
        // todo check for task list item marker
        MarkdownText(text, MaterialTheme.typography.body1, modifier)
    }
}