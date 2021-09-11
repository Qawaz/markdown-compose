package com.wakaztahir.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.example.testing.appendMarkdownContent
import com.wakaztahir.example.testing.createDefaultParser
import com.wakaztahir.example.ui.theme.MarkdownTextFieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownTextFieldTheme {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                    val parser = remember { createDefaultParser() }

                    val annotatedString = remember {
                        buildAnnotatedString {
                            appendMarkdownContent(parser.parse(SampleText))

                            toAnnotatedString()
                        }
                    }


                    SelectionContainer {
                        Text(
                            text = annotatedString,
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
        }
    }
}