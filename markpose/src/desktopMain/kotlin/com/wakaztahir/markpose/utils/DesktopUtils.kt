package com.wakaztahir.markpose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

internal actual fun getTimeMillis(): Long = System.currentTimeMillis()

@Composable
internal actual fun ContainDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit
) = Dialog(onCloseRequest = onDismissRequest, title = title, content = { content() })