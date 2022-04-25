package com.wakaztahir.markdowntext.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun getTimeMillis(): Long = System.currentTimeMillis()

@OptIn(ExperimentalComposeUiApi::class)
private val dialogProperties = DialogProperties(usePlatformDefaultWidth = false)

@Composable
internal actual fun ContainDialog(onDismissRequest: () -> Unit, title: String, content: @Composable () -> Unit) =
    Dialog(onDismissRequest = onDismissRequest, properties = dialogProperties, content = content)


internal actual fun IODispatcher(): CoroutineDispatcher = Dispatchers.IO