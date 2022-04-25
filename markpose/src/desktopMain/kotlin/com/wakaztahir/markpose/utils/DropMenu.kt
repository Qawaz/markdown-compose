package com.wakaztahir.markpose.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun DropMenu(
    modifier: Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) = DropdownMenu(
    modifier = modifier,
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    content = content
)

@Composable
internal actual fun DropMenuItem(
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
)= DropdownMenuItem(modifier = modifier, onClick = onClick, content = content)