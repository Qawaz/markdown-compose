package com.wakaztahir.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wakaztahir.markdowncompose.editor.components.LocalEditor
import com.wakaztahir.markdowncompose.editor.components.refreshLinkPreviews
import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.utils.*
import kotlinx.coroutines.launch

enum class ToolsState {
    Hidden,
    Creator,
    Text
}

class State {
    var toolsState by mutableStateOf(ToolsState.Hidden)
}

@Composable
fun EditorTools(
    modifier: Modifier = Modifier,
    state: State
) {

    val editorState = LocalEditor.current
    val scope = rememberCoroutineScope()
    var linkDialog by remember { mutableStateOf(false) }

    // Tools
    Column(modifier = modifier) {

        // Main Options
        val toggleTools: (ToolsState) -> Unit = {
            if (state.toolsState == it) {
                state.toolsState = (ToolsState.Hidden)
            } else {
                state.toolsState = (it)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            OptionButton(
                onClick = { toggleTools(ToolsState.Creator) },
                text = "Add"
            )
            OptionButton(
                onClick = { toggleTools(ToolsState.Text) },
                text = "Text"
            )
        }

        when (state.toolsState) {
            ToolsState.Hidden -> {
            }

            ToolsState.Creator -> {
                CreatorTools(
                    onTextClick = {
                        editorState.addBlock(block = TextBlock())
                        state.toolsState = ToolsState.Hidden
                    },
                    onListClick = {
                        editorState.addBlock(block = ListItemBlock())
                        state.toolsState = ToolsState.Hidden
                    },
                    onCodeClick = {
                        editorState.addBlock(block = CodeBlock())
                        state.toolsState = ToolsState.Hidden
                    }
                ) {
                    editorState.addBlock(block = MathBlock())
                    state.toolsState = ToolsState.Hidden
                }
            }

            ToolsState.Text -> {
                TextTools(
                    onLinkClick = { linkDialog = true }
                )
            }

        }
    }

    if (linkDialog && editorState.activeFormatter?.value != null) {

        var link by remember { mutableStateOf("") }
        val textFieldValue = editorState.activeFormatter!!.value

        //todo fix linking
        val selection = remember { textFieldValue.selection }
        Dialog(onDismissRequest = {
            if (link.isNotEmpty()) {
                if (textFieldValue.selection.collapsed) {
                    editorState.activeFormatter?.appendLink(
                        text = "Link",
                        link = link
                    )
                    scope.launch {
                        editorState.refreshLinkPreviews()
                    }
                } else if (!textFieldValue.selection.collapsed) {
                    editorState.activeFormatter?.appendLink(link = link)
                    scope.launch {
                        editorState.refreshLinkPreviews()
                    }
                }
            }
            linkDialog = false
        }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            OutlinedTextField(
                value = link,
                onValueChange = { link = it }
            )
        }
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
            onClick = onTextClick
        )
        CreatorCard(
            name = "List",
            onClick = onListClick
        )
        CreatorCard(
            name = "Code",
            onClick = onCodeClick
        )
        CreatorCard(
            name = "Math",
            onClick = onMathClick
        )
    }
}

@Composable
fun CreatorCard(
    name: String,
    onClick: () -> Unit,
) {

    Button(
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
        )
    }

}


@Composable
fun OptionButton(
    onClick: () -> Unit,
    text: String
) {
    Button(
        modifier = Modifier.padding(4.dp),
        onClick = onClick
    ) {
        Text(
            text = text
        )
    }
}

@Composable
fun TextTools(onLinkClick: () -> Unit) {

    val state = LocalEditor.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextToolsButton(
            text = "Bold",
            state = state.activeFormatter?.isBold == true
        ) {
            if (state.activeFormatter?.isBold == true) {
                state.activeFormatter?.removeBold()
            } else {
                state.activeFormatter?.makeBold()
            }
        }

        TextToolsButton(
            text = "Italic",
            state = state.activeFormatter?.isItalic == true
        ) {
            if (state.activeFormatter?.isItalic == true) {
                state.activeFormatter?.removeItalic()
            } else {
                state.activeFormatter?.makeItalic()
            }
        }

        TextToolsButton(
            text = "Overline",
            state = state.activeFormatter?.isStrikeThrough == true
        ) {
            if (state.activeFormatter?.isStrikeThrough == true) {
                state.activeFormatter?.removeStrikeThrough()
            } else {
                state.activeFormatter?.makeStrikeThrough()
            }
        }

        TextToolsButton(
            text = "Link",
            state = state.activeFormatter?.isLink == true,
            onClick = onLinkClick
        )
    }
}

@Composable
fun TextToolsButton(
    text: String,
    state: Boolean,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(
            text = text,
            color = if (state) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onPrimary.copy(.6f)
            }
        )
    }
}