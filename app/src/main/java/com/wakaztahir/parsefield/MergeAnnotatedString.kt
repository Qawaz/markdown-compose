package com.wakaztahir.parsefield

import androidx.compose.ui.text.AnnotatedString

fun mergeAnnotatedString(old: AnnotatedString, new: AnnotatedString): AnnotatedString {

    val spanStyles = old.spanStyles.toMutableList()

    val paragraphStyles = old.paragraphStyles.toMutableList()

    if (old.text != new.text) {
        val spanIterator = spanStyles.iterator()
        while (spanIterator.hasNext()) {
            val style = spanIterator.next()
            if (style.start > new.text.length) {
                spanIterator.remove()
            }
        }
    }

    return AnnotatedString(
        text = new.text,
        spanStyles = spanStyles,
        paragraphStyles = paragraphStyles
    )
}