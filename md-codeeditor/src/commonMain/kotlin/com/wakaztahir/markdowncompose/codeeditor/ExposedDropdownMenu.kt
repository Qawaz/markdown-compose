package com.wakaztahir.markdowncompose.codeeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun ExposedDropdownMenuField(
    modifier: Modifier = Modifier,
    text: String,
    outlined: Boolean,
    expanded : Boolean,
    enabled: Boolean = false,
    isError: Boolean = false,
    shape: Shape = if (outlined) TextFieldDefaults.outlinedShape else TextFieldDefaults.filledShape,
    maxLines: Int = 1,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null
        )
    },
    supportingText: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = if (outlined) {
        TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = if(expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = if(expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        TextFieldDefaults.textFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = if(expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
   },
) {
    if (outlined) {
        OutlinedTextField(
            modifier = modifier.pointerHoverIcon(icon = PointerIcon.Hand,overrideDescendants = true),
            value = text,
            onValueChange = { },
            readOnly = true,
            label = label,
            enabled = enabled,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = colors,
            isError = isError,
            shape = shape,
            textStyle = textStyle,
            maxLines = maxLines,
            supportingText = supportingText,
            singleLine = true,
        )
    } else {
        TextField(
            modifier = modifier.pointerHoverIcon(icon = PointerIcon.Hand,overrideDescendants = true),
            value = text,
            onValueChange = { },
            readOnly = true,
            label = label,
            enabled = enabled,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = colors,
            isError = isError,
            shape = shape,
            textStyle = textStyle,
            maxLines = maxLines,
            supportingText = supportingText,
            singleLine = true
        )
    }
}

@Composable
internal fun ExposedDropdownMenu(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    expanded: Boolean,
    onToggleExpansion: (Boolean) -> Unit,
    field: @Composable (Modifier) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        field(
            Modifier.clickable {
                onToggleExpansion(!expanded)
            }
        )
        DropdownMenu(
            modifier = menuModifier,
            expanded = expanded,
            onDismissRequest = { onToggleExpansion(false) },
            content = content
        )
    }
}

@Composable
internal fun ExposedDropdownMenu(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    outlined: Boolean,
    expanded: Boolean,
    text: String,
    onToggleExpansion: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ExposedDropdownMenu(
        modifier = modifier,
        menuModifier = menuModifier,
        expanded = expanded,
        onToggleExpansion = onToggleExpansion,
        field = {
            ExposedDropdownMenuField(
                modifier = it,
                outlined = outlined,
                text = text,
                expanded = expanded,
            )
        },
        content = content,
    )
}