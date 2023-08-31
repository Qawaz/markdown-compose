package com.wakaztahir.markdowncompose.editor.utils

import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import com.wakaztahir.markdowncompose.editor.wrapper.*

fun SpanStyle.wrap(child: Wrapper): Wrapper {
    var wrapper: Wrapper = child
    if (textDecoration == TextDecoration.LineThrough) {
        wrapper = StrikethroughWrap(wrapper)
    }
    if (fontStyle == FontStyle.Italic) {
        wrapper = ItalicWrap(wrapper)
    }
    if (fontWeight != null && fontWeight!!.weight > 400) {
        wrapper = BoldWrap(wrapper)
    }
    return wrapper
}

fun SpanStyle.wrap(children: List<Wrapper>): List<Wrapper> {
    return children.map { wrap(it) }
}

data class WrapperRange(val wrapper: Wrapper, val range: TextRange)

fun AnnotatedString.textWrap(start: Int, end: Int): WrapperRange {
    return WrapperRange(TextWrap(text.substring(start, end)), TextRange(start, end))
}

@OptIn(ExperimentalTextApi::class)
private fun Any.wrap(child: Wrapper): Wrapper {
    return when (this) {
        is SpanStyle -> {
            wrap(child)
        }

        is UrlAnnotation -> {
            LinkWrap(this.url, child)
        }

        else -> {
            throw IllegalStateException("Unknown type of conversion to wrapper :$this")
        }
    }
}

/**
 *
 * This converts Annotated String to our own nested data structure
 *
 * @param nestedChildren
 * when true, The output will be
 * `Start<strong>My<em>Middle</em>Text</strong>End`
 *
 * When false , The output will be
 * `Start<strong>My</strong><strong><em>Middle</em></strong><strong>Text</strong>End`
 */
@OptIn(ExperimentalTextApi::class)
internal fun AnnotatedString.toWrappers(nestedChildren: Boolean): List<Wrapper> {

    @Suppress("UNCHECKED_CAST")
    val sortedStyles: MutableList<AnnotatedString.Range<Any>> =
        spanStyles.toMutableList() as MutableList<AnnotatedString.Range<Any>>

    @Suppress("UNCHECKED_CAST")
    sortedStyles.addAll(getUrlAnnotations(0, text.length) as List<AnnotatedString.Range<Any>>)

    sortedStyles.sortBy { it.end }

    val openedWrappers = mutableListOf<WrapperRange>()

//    println("Text:$text")

    for (currStyle in sortedStyles) {

//        println("WrappersBefore:" + openedWrappers.map { it.wrapper })

//        println("CurrentStyle:" + currStyle.item.wrap("STYLE"))

        if (openedWrappers.isEmpty()) {
            openedWrappers.add(
                WrapperRange(
                    currStyle.item.wrap(TextWrap(text.substring(currStyle.start, currStyle.end))),
                    TextRange(currStyle.start, currStyle.end)
                )
            )
        } else {

            val start = openedWrappers.first().range.start
            val end = openedWrappers.last().range.end

            if (currStyle.start < start) {
                // current style start to nested first style start
                val textBefore = text.substring(currStyle.start, start)
//                println("current style start to nested first style start :$textBefore")
                openedWrappers.add(0, WrapperRange(TextWrap(textBefore), TextRange(currStyle.start, start)))
            }

            if (currStyle.start > end) {

                // last style end to current style start
                val textAfter = text.substring(end, currStyle.start)
//                println("last style end to current style start:$textAfter")
                openedWrappers.add(WrapperRange(TextWrap(textAfter), TextRange(end, currStyle.start)))

                // current style start to current style end
                val middle = text.substring(currStyle.start, currStyle.end)
//                println("current style start to current style end:$middle")
                openedWrappers.add(WrapperRange(TextWrap(middle), TextRange(currStyle.start, currStyle.end)))

            } else if (currStyle.end > end) {
                // nested last style end to current style end
                val textAfter = text.substring(end, currStyle.end)
//                println("nested last style end to current style end:$textAfter")
                openedWrappers.add(WrapperRange(TextWrap(textAfter), TextRange(end, currStyle.end)))
            }


            if (nestedChildren) {

//                println("PARENTING_WRAPPERS" + openedWrappers.map { it.wrapper })
//                println("PARENTABLE_WRAPPERS" + openedWrappers.mapNotNull { if (currStyle.contains(it.range)) it.wrapper else null })
                val children = mutableListOf<Wrapper>()

                var hasStartedPopping = false
                for (i in openedWrappers.size - 1 downTo 0) {
                    val wrap = openedWrappers[i]
                    if (currStyle.contains(wrap.range)) {
                        hasStartedPopping = true
                        children.add(0, wrap.wrapper)
                        openedWrappers.removeAt(i)
                    } else if (hasStartedPopping) {
                        break
                    }
                }

                if (children.isNotEmpty()) {
                    if (children.size == 1) {
                        openedWrappers.add(
                            WrapperRange(
                                currStyle.item.wrap(children.first()),
                                TextRange(currStyle.start, currStyle.end)
                            )
                        )
                    } else {
                        openedWrappers.add(
                            WrapperRange(
                                currStyle.item.wrap(ChildrenWrap(children) as Wrapper),
                                TextRange(currStyle.start, currStyle.end)
                            )
                        )
                    }
                }

            } else {
                val all = openedWrappers.map {
                    if (currStyle.contains(it.range)) WrapperRange(
                        currStyle.item.wrap(it.wrapper),
                        it.range
                    ) else it
                }
                openedWrappers.clear()
                openedWrappers.addAll(all)
            }


        }

//        println("WrappersAfter:" + openedWrappers.map { it.wrapper })

    }


    val firstStyleStart = openedWrappers.firstOrNull()?.range?.start ?: 0
    if (firstStyleStart > 0) {
        openedWrappers.add(0, textWrap(0, firstStyleStart))
    }

    val lastStyleIndex = openedWrappers.lastOrNull()?.range?.end ?: 0
    if (lastStyleIndex < text.length) {
        openedWrappers.add(textWrap(lastStyleIndex, text.length))
    }

    return openedWrappers.map { it.wrapper }
}