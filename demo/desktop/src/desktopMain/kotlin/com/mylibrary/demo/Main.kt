package com.mylibrary.demo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication(
    title = "Library demo",
    state = WindowState(width = 400.dp)
) {
    val enforceDarkTheme = false
    MaterialTheme(colors = if (isSystemInDarkTheme() || enforceDarkTheme) DarkColors else LightColors) {
        Editor()
    }
}