package com.wakaztahir.markpose.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
internal fun DropMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) = DropdownMenu(
    modifier = modifier,
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    content = content
)

//todo DropdownMenu not being indexed by IDE
@Composable
internal fun DropMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    offset: DpOffset,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) = DropdownMenu(
    modifier = modifier,
    expanded = expanded,
    offset = offset,
    onDismissRequest = onDismissRequest,
    content = content
)

//todo DropdownMenuItem not being indexed by IDE
@Composable
internal fun DropItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) = DropdownMenuItem(modifier = modifier, onClick = onClick, content = content)

@Composable
internal fun MenuItem(
    icon: Painter? = null,
    iconDescription: String? = null,
    text: String,
    value: String? = null,
    onClick: () -> Unit = {}
) {
    DropdownMenuItem(
        modifier = Modifier.defaultMinSize(minWidth = 180.dp),
        onClick = onClick
    ) {
        if (icon != null) {
            Icon(painter = icon, contentDescription = iconDescription)
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = text,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
        if (value != null) {
            Text(text = value)
        }
    }
}