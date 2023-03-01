package com.wakaztahir.markdowncompose.codeeditor

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun DropdownMenu(
    modifier : Modifier = Modifier,
    expanded : Boolean,
    onDismissRequest : ()->Unit,
    content : @Composable ColumnScope.()->Unit
)

@Composable
internal expect fun DropdownMenuItem(
    modifier: Modifier = Modifier,
    onClick : ()->Unit,
    leadingIcon : @Composable (()->Unit)? = null,
    trailingIcon : @Composable (()->Unit)? = null,
    text : @Composable ()->Unit,
    enabled : Boolean = true,
)
