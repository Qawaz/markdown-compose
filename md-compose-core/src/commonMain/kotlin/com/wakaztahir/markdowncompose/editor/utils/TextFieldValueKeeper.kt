package com.wakaztahir.markdowncompose.editor.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue

private fun <T> List<AnnotatedString.Range<T>>.mapMoving(pointer: Int, offset: Int): List<AnnotatedString.Range<T>> {
    return mapNotNull {
        if (it.end >= pointer || it.start >= pointer) {
            AnnotatedString.Range(
                item = it.item,
                start = if (it.start >= pointer) (it.start + offset) else it.start,
                end = if (it.end >= pointer) (it.end + offset) else it.end
            ).let { range ->
//                println("Pointer:${pointer}Start:${it.start}End:${it.end}Offset:${offset}Result:${if (it.start >= pointer) (it.start + offset) else it.start}")
                if (range.start == range.end) null else range
            }
        } else it
    }
}

fun differ(value: TextFieldValue, newValue: TextFieldValue): TextFieldValue {

    val pointer = if (value.selection.reversed) value.selection.end else value.selection.start

//        println("POINTER:$pointer")

    val lengthDifference = newValue.text.length - value.text.length

//        println("LENGTHDIFF:$lengthDifference")
//
//        if(value.annotatedString.spanStyles.size == 1){
//            println("SPANB:" + value.annotatedString.spanStyles[0].let { "${it.start} - ${it.end}" })
//        }

//        val newStyles = value.annotatedString.spanStyles.mapMoving(lengthDifference)
//
//        if(newStyles.size == 1){
//            println("SPANA:" + newStyles[0].let { "${it.start} - ${it.end}" })
//        }

    return TextFieldValue(
        annotatedString = AnnotatedString(
            text = newValue.text,
            spanStyles = value.annotatedString.spanStyles.mapMoving(pointer, lengthDifference),
            paragraphStyles = value.annotatedString.paragraphStyles.mapMoving(pointer, lengthDifference),
        ),
        selection = newValue.selection
    )

}