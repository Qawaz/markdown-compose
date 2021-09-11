package com.wakaztahir.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun Controller(textFieldValue: TextFieldValue, onUpdate: (TextFieldValue) -> Unit) {
    Row {
        OutlinedButton(onClick = {
            if (textFieldValue.selection.length > 0) {
                onUpdate(
                    textFieldValue.copy(
                        buildAnnotatedString {
                            this.addStyle(
                                SpanStyle(fontWeight = FontWeight.Bold),
                                textFieldValue.selection.start,
                                textFieldValue.selection.end
                            )
                        }.plus(textFieldValue.annotatedString),
                    )
                )
            }

        }) {
            Text(text = "Bold")
        }
        OutlinedButton(onClick = { /*TODO*/ }) {
            Text(text = "Italic")
        }

    }
}