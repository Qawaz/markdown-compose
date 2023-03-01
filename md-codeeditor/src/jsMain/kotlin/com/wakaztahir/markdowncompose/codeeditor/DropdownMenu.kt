package com.wakaztahir.markdowncompose.codeeditor

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun DropdownMenu(
    modifier: Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    androidx.compose.material3.DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        content = content
    )
}

@Composable
actual fun DropdownMenuItem(
    modifier: Modifier,
    onClick: () -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    text: @Composable () -> Unit,
    enabled: Boolean
) {
    androidx.compose.material3.DropdownMenuItem(
        modifier = modifier,
        onClick = onClick,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        text = text,
        enabled = enabled
    )
}