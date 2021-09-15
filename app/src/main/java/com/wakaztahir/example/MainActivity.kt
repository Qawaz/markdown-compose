package com.wakaztahir.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.wakaztahir.example.ui.theme.MarkdownTextFieldTheme
import com.wakaztahir.markdowntext.editor.MarkdownEditor
import com.wakaztahir.markdowntext.editor.rememberParsedMarkdown

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalUnitApi::class, androidx.compose.ui.text.InternalTextApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownTextFieldTheme {

                val parsedMarkdown = rememberParsedMarkdown(markdown = SampleText.first())
                
                MarkdownEditor(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    parsed = parsedMarkdown,
                )
            }
        }
    }
}