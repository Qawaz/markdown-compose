package com.wakaztahir.markdowntext.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
internal fun MenuSelector(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = ButtonDefaults.outlinedBorder,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        backgroundColor = MaterialTheme.colors.background
    ),
    content: @Composable () -> Unit,
) = CustomButton(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    elevation = elevation,
    shape = shape,
    border = border,
    contentPadding = contentPadding,
    colors = colors
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = if (fullWidth) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
            }
        ) {
            content()
        }
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null
        )
    }
}