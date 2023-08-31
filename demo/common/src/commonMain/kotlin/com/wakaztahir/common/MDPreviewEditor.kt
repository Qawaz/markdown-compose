package com.wakaztahir.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.markdowncompose.core.MarkdownPreviewConfiguration
import com.wakaztahir.markdowncompose.preview.MarkdownPreview

@Composable
fun MDPreviewEditor(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            var markdown by remember { mutableStateOf(InitialMarkdown) }
            OutlinedTextField(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                value = markdown,
                onValueChange = { markdown = it },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )
            MarkdownPreview(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight().verticalScroll(rememberScrollState()),
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