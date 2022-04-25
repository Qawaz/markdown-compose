package com.wakaztahir.markdowntext.editor.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wakaztahir.linkpreview.LinkPreview
import com.wakaztahir.linkpreview.getLinkPreview
import com.wakaztahir.markdowntext.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowntext.editor.model.blocks.TextBlock
import com.wakaztahir.markdowntext.editor.states.EditorState
import com.wakaztahir.markdowntext.utils.IODispatcher
import com.wakaztahir.markdowntext.utils.TAG_URL
import com.wakaztahir.markdowntext.utils.fastForEach
import com.wakaztahir.markdowntext.utils.imagePainter
import kotlinx.coroutines.withContext

suspend fun EditorState.addLinkPreview(url: String) = withContext(IODispatcher()) {
    val preview = kotlin.runCatching { getLinkPreview(url = url) }.getOrNull()
    if (preview?.imageUrl != null) {
        linkPreviews[url] = preview
    }
}

suspend fun EditorState.refreshLinkPreviews() {
    val annotatedString = buildAnnotatedString {
        for(i in 0 until blocks.size){
            when (val it = blocks[i]) {
                is TextBlock -> {
                    append(it.textFieldValue.annotatedString)
                }
                is ListItemBlock -> {
                    append(it.textFieldValue.annotatedString)
                }
            }
        }
    }

    val annotations = annotatedString.getStringAnnotations(tag = TAG_URL, 0, annotatedString.length)
        .associateBy { it.item }

    val iterator = linkPreviews.iterator()

    // Removing the link previews for which annotations have been removed
    while (iterator.hasNext()) {
        val item = iterator.next()
        if (!annotations.containsKey(item.key)) {
            iterator.remove()
        }
    }

    // Adding newly added annotations as link previews
    annotations.forEach {
        if (!linkPreviews.containsKey(it.key)) {
            addLinkPreview(it.key)
        }
    }
}

fun LazyListScope.linkPreviews(
    state: EditorState,
    imageSize: Dp = 80.dp,
    padding: Dp = 8.dp,
    background: Color? = null
) {

    val previewsList = state.linkPreviews.values

    items(state.linkPreviews.size, { previewsList.elementAt(it).originalUrl }) { index ->

        val preview = previewsList.elementAt(index)
        val handler = LocalUriHandler.current

        DisplayLinkPreview(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .clip(RoundedCornerShape(6.dp))
                .background(color = background ?: MaterialTheme.colors.onBackground.copy(.05f))
                .clickable {
                    kotlin.runCatching {
                        handler.openUri(preview.imageUrl!!)
                    }.onFailure {
                        it.printStackTrace()
                    }
                },
            preview = preview,
            imageSize = imageSize,
        )
    }
}


@Composable
fun DisplayLinkPreview(
    modifier: Modifier = Modifier,
    imageSize: Dp,
    preview: LinkPreview,
    maxTitle: Int = 50,
    maxDescription: Int = 120,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        imagePainter(url = preview.imageUrl!!)?.let {
            Image(
                modifier = Modifier.fillMaxHeight().widthIn(min = imageSize,max = imageSize).heightIn(max = imageSize),
                painter = it,
                contentDescription = preview.title
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = if (preview.title.length > maxTitle) preview.title.substring(
                    0,
                    maxTitle
                ) + "..." else preview.title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (preview.description.length > maxDescription) preview.description.substring(
                    0,
                    maxDescription
                ) + "..." else preview.description,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.caption
            )
        }
    }
}