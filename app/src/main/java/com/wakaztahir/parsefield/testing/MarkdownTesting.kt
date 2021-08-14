package com.wakaztahir.parsefield.testing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wakaztahir.parsefield.mergeAnnotatedString
import com.wakaztahir.parsefield.ui.components.Controller
import com.wakaztahir.textparser.MarkdownText

@Composable
fun MarkdownTesting(){

    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    MarkdownText(
        markdown = """
    # Demo

    Emphasis, aka italics, with *asterisks* or _underscores_. Strong emphasis, aka bold, with **asterisks** or __underscores__. Combined emphasis with **asterisks and _underscores_**. [Links with two blocks, text in square-brackets, destination is in parentheses.](https://www.example.com). Inline `code` has `back-ticks around` it.

    1. First ordered list item
    2. Another item
        * Unordered sub-list.
    3. And another item.
        You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

    * Unordered list can use asterisks
    - Or minuses
    + Or pluses
    ---

    ```javascript
    var s = "code blocks use monospace font";
    alert(s);
    ```

    Markdown | Table | Extension
    --- | --- | ---
    *renders* | `beautiful images` | ![random image](https://picsum.photos/seed/picsum/400/400 "Text 1")
    1 | 2 | 3

    > Blockquotes are very handy in email to emulate reply text.
    > This line is part of the same quote.
    """.trimIndent()
    )

    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            value = textFieldValue,
            onValueChange = {

                textFieldValue = textFieldValue.copy(
                    mergeAnnotatedString(
                        old = textFieldValue.annotatedString,
                        new = it.annotatedString
                    ),
                    it.selection,
                    it.composition
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = Color.White)
        )

        Controller(
            textFieldValue,
            onUpdate = {
                textFieldValue = it
            }
        )
    }
}