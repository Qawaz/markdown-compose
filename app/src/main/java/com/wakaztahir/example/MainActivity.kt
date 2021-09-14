package com.wakaztahir.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.wakaztahir.example.ui.theme.MarkdownTextFieldTheme
import com.wakaztahir.markdowntext.preview.MarkdownPreview

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalUnitApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownTextFieldTheme {
                LazyColumn {
                    items(SampleText.shuffled()) { text ->
                        SelectionContainer {
                            MarkdownPreview(
                                markdown = text
                            )
                        }
                    }
                }
            }
        }
    }
}