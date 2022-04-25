package com.wakaztahir.markpose.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal expect fun DropMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
)

@Composable
internal expect fun DropMenuItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
)

@Composable
internal fun MenuItem(
    icon: Painter? = null,
    iconDescription: String? = null,
    text: String,
    value: String? = null,
    onClick: () -> Unit = {}
) {
    DropMenuItem(
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