package com.wakaztahir.markdowntext.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.RelocationRequester
import androidx.compose.ui.layout.relocationRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

// todo make links clickable

// todo save back the edited text to node by converting annotated string back
// to markdown when multiline styling is supported


/**
 * Compose does not support Multi Line Styling
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun EditableMarkdown(
    textValue: TextFieldValue,
    onUpdate: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    textStyle: TextStyle = LocalTextStyle.current,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colors.onBackground,
        //todo make borders transparent
//        focusedBorderColor = Color.Transparent,
//        unfocusedBorderColor = Color.Transparent
    )
) {

    val relocationRequester = remember { RelocationRequester() }

    var textFieldValue by remember { mutableStateOf(textValue) }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .relocationRequester(relocationRequester = relocationRequester),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onUpdate(it)
            relocationRequester.bringIntoView()
        },
        textStyle = textStyle,
        shape = shape,
        colors = colors,
    )
}