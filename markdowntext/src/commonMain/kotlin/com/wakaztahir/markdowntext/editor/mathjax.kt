package com.wakaztahir.markdowntext.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MathJax(modifier: Modifier = Modifier, latex: String)