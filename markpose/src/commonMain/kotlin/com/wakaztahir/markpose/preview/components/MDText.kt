package com.wakaztahir.markpose.preview.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaztahir.markpose.utils.LocalReferenceLinkHandler
import com.wakaztahir.markpose.utils.TAG_IMAGE_URL
import com.wakaztahir.markpose.utils.TAG_URL
import com.wakaztahir.markpose.utils.imagePainter

@Composable
internal fun MDText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    modify: AnnotatedString.Builder.(AnnotatedString) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val referenceLinkHandler = LocalReferenceLinkHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = buildAnnotatedString { modify(text) },
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { pos ->
                layoutResult.value?.let { layoutResult ->
                    val position = layoutResult.getOffsetForPosition(pos)
                    text.getStringAnnotations(position, position)
                        .firstOrNull { a -> a.tag == TAG_URL }
                        ?.let { a ->
                            kotlin.runCatching { uriHandler.openUri(referenceLinkHandler.find(a.item)) }.onFailure {
                                it.printStackTrace()
                            }
                        }
                }
            }
        },
        style = style,
        color = color,
        inlineContent = mapOf(
            TAG_IMAGE_URL to InlineTextContent(
                Placeholder(180.sp, 180.sp, PlaceholderVerticalAlign.Bottom) // TODO, identify flexible scaling!
            ) {
                Spacer(Modifier.padding(4.dp))

                imagePainter(it)?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = "Image", // TODO
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.padding(4.dp))
            }
        ),
        onTextLayout = { layoutResult.value = it }
    )
}
