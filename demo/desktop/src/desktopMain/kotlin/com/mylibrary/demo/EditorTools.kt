package com.mylibrary.demo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaztahir.markpose.editor.components.LocalEditor
import com.wakaztahir.markpose.editor.model.blocks.CodeBlock
import com.wakaztahir.markpose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markpose.editor.model.blocks.MathBlock
import com.wakaztahir.markpose.editor.model.blocks.TextBlock
import com.wakaztahir.markpose.editor.utils.*

enum class ToolsState {
    Hidden,
    Creator,
    Text,
}

@Composable
fun EditorTools(
    modifier: Modifier = Modifier,
    state: MutableState<ToolsState>
) {

    val editorState = LocalEditor.current
    var linkDialog by remember { mutableStateOf(false) }

    // Tools
    Column(modifier = modifier) {
        when (state.value) {
            ToolsState.Hidden -> {
            }
            ToolsState.Creator -> {
                CreatorTools(
                    onTextClick = {
                        editorState.addBlock(block = TextBlock(requestFocus = true))
                        state.value = ToolsState.Hidden
                    },
                    onListClick = {
                        editorState.addBlock(block = ListItemBlock(requestFocus = true))
                        state.value = ToolsState.Hidden
                    },
                    onMathClick = {
                        editorState.addBlock(block = MathBlock())
                        state.value = ToolsState.Hidden
                    },
                    onCodeClick = {
                        editorState.addBlock(block = CodeBlock())
                        state.value = ToolsState.Hidden
                    }
                )
            }
            ToolsState.Text -> {
                TextTools(
                    onLinkClick = { linkDialog = true }
                )
            }
        }

        // Main Options
        val toggleTools: (ToolsState) -> Unit = {
            if (state.value == it) {
                state.value = (ToolsState.Hidden)
            } else {
                state.value = (it)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            OptionButton(
                onClick = { toggleTools(ToolsState.Creator) },
                icon = Icons.Default.Add,
                contentDescription = null
            )
            OptionButton(
                onClick = { toggleTools(ToolsState.Text) },
                icon = Icons.Default.Edit,
                contentDescription = null
            )
        }
    }

    if (linkDialog && editorState.activeFormatter?.value != null) {

        val textFieldValue = editorState.activeFormatter!!.value
        val scope = rememberCoroutineScope()

        //todo check this
//        LinkDialog(
//            onDismissRequest = { linkDialog = false },
//            showTitle = textFieldValue.selection.collapsed,
//            onLinkCreated = { title, link ->
//                if (link.isNotEmpty()) {
//                    if (!title.isNullOrEmpty()) {
//                        editorState.activeFormatter?.appendLink(
//                            text = title,
//                            link = link
//                        )
//                        scope.launch(ioDispatcher()) {
//                            editorState.refreshLinkPreviews()
//                        }
//                    } else if (!textFieldValue.selection.collapsed) {
//                        editorState.activeFormatter?.appendLink(link = link)
//                        scope.launch(ioDispatcher()) {
//                            editorState.refreshLinkPreviews()
//                        }
//                    }
//                }
//            }
//        )
    }
}

@Composable
fun CreatorTools(
    onTextClick: () -> Unit,
    onListClick: () -> Unit,
    onCodeClick: () -> Unit,
    onMathClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        CreatorCard(
            name = "Text",
            icon = Icons.Default.Edit,
            iconDescription = null,
            onClick = onTextClick
        )
        CreatorCard(
            name = "TodoList",
            icon = Icons.Default.List,
            iconDescription = null,
            onClick = onListClick
        )
        CreatorCard(
            name = "Code",
            icon = Icons.Default.Star,
            iconDescription = null,
            onClick = onCodeClick
        )
        CreatorCard(
            name = "Math",
            icon = Icons.Default.DateRange,
            iconDescription = null,
            onClick = onMathClick
        )
    }
}

@Composable
fun CreatorCard(
    name: String,
    icon: ImageVector,
    iconDescription: String? = null,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = icon,
                contentDescription = iconDescription,
                tint = MaterialTheme.colors.onBackground.copy(.7f)
            )
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = name,
                style = MaterialTheme.typography.h5,
                fontSize = 18.sp,
            )
        }
    }
}


@Composable
fun OptionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null
) {
    Button(
        modifier = Modifier
            .padding(2.dp)
            .width(52.dp),
        onClick = onClick,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colors.onBackground.copy(.7f)
        )
    }
}

@Composable
fun TextTools(
    onLinkClick: () -> Unit,
) {

    val state = LocalEditor.current
    val textBlock = state.activeBlock as? TextBlock

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextToolsButton(
            text = "B",
            state = state.activeFormatter?.isBold == true,
            onClick = {
                if (state.activeFormatter?.isBold == true) {
                    state.activeFormatter?.removeBold()
                } else {
                    state.activeFormatter?.makeBold()
                }
            }
        )

        TextToolsButton(
            text = "I",
            state = state.activeFormatter?.isItalic == true,
            onClick = {
                if (state.activeFormatter?.isItalic == true) {
                    state.activeFormatter?.removeItalic()
                } else {
                    state.activeFormatter?.makeItalic()
                }
            }
        )

        TextToolsButton(
            text = "StrikeThrough",
            state = state.activeFormatter?.isStrikeThrough == true,
            onClick = {
                if (state.activeFormatter?.isStrikeThrough == true) {
                    state.activeFormatter?.removeStrikeThrough()
                } else {
                    state.activeFormatter?.makeStrikeThrough()
                }
            }
        )

        TextToolsButton(
            text = "Link",
            state = state.activeFormatter?.isLink == true,
            onClick = onLinkClick
        )
    }
}

@Composable
fun TextToolsButton(
    text: String? = null,
    icon: ImageVector? = null,
    iconDescription: String? = null,
    state: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        if (text != null) {
            Text(text = text, style = MaterialTheme.typography.h5)
        }
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
                tint = if (state) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onBackground
                }
            )
        }
    }
}
