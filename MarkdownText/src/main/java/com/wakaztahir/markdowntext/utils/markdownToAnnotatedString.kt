package com.wakaztahir.markdowntext.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.markdowntext.common.LocalCommonMarkParser
import com.wakaztahir.markdowntext.common.createDefaultParser
import com.wakaztahir.markdowntext.preview.annotation.appendMarkdownContent
import com.wakaztahir.markdowntext.preview.model.LocalMarker
import com.wakaztahir.markdowntext.preview.model.Marker

/**
 * Converts markdown to [AnnotatedString] to display in a Text | TextField Composable
 * This is best for multiple conversions because it utilizes old instances again
 * through composition local providers
 */
@Composable
fun markdownToAnnotatedString(
    markdown: String,
    marker: Marker = LocalMarker.current,
): AnnotatedString {
    return buildAnnotatedString {
        appendMarkdownContent(marker, LocalCommonMarkParser.current.parse(markdown))
    }
}

/**
 * Please prefer its composable alternative which is faster as it does not create
 * new instances each time
 * [markdownToAnnotatedString]
 */
fun convertToAnnotatedString(
    markdown: String,
    marker: Marker,
): AnnotatedString {
    return buildAnnotatedString {
        appendMarkdownContent(marker, createDefaultParser().parse(markdown))
    }
}