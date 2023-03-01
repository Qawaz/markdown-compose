package com.wakaztahir.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.core.MarkdownPreviewConfiguration
import com.wakaztahir.markdowncompose.preview.MarkdownPreview

private val InitialMarkdown: String = """""".trimIndent()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MDPreviewEditor(modifier : Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            var markdown by remember { mutableStateOf(InitialMarkdown) }
            OutlinedTextField(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                value = markdown,
                onValueChange = { markdown = it },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onBackground
                )
            )
            MarkdownPreview(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight(),
                markdown = markdown,
                configuration = remember {
                    MarkdownPreviewConfiguration(
                        onCheckboxClick = { _, updatedMarkdown -> markdown = updatedMarkdown }
                    )
                }
            )
        }
    }
}