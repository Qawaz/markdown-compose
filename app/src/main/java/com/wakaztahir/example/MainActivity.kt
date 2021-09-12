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
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.wakaztahir.example.ui.theme.MarkdownTextFieldTheme
import com.wakaztahir.markdowntext.core.appendMarkdownContent
import com.wakaztahir.markdowntext.model.Marker
import com.wakaztahir.markdowntext.model.parse

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalUnitApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownTextFieldTheme {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                    val marker = remember { Marker() }

                    val annotatedString = remember {
                        buildAnnotatedString {
                            appendMarkdownContent(marker, SampleText)

                            toAnnotatedString()
                        }
                    }


                    SelectionContainer {
                        Text(
                            text = annotatedString,
                            color = MaterialTheme.colors.onBackground,
                            inlineContent = marker.inlineContent
                        )
                    }
                }
            }
        }
    }
}