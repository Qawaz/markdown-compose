package com.wakaztahir.common

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberColorScheme(): ColorScheme = remember { darkColorScheme() }