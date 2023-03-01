package com.wakaztahir.markdowncompose.mathjax

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
expect fun MathJax(modifier: Modifier = Modifier, latex: String,color : Color)