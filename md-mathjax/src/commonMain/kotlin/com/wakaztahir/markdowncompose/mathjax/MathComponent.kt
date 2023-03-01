package com.wakaztahir.markdowncompose.mathjax

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wakaztahir.helpers.currentTimeMillis
import com.wakaztahir.markdowncompose.editor.components.LocalEditor
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock
import com.wakaztahir.markdowncompose.editor.utils.textFieldUndoRedoAction
import compose.icons.MaterialDesignIcons
import compose.icons.materialdesignicons.Check
import compose.icons.materialdesignicons.PencilOutline
import compose.icons.materialdesignicons.TrashCanOutline
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathBlock.MathComponent(
    modifier: Modifier = Modifier,
//    parser: PrettifyParser = prettifyParser,
//    theme: CodeTheme = if (MaterialTheme.colorScheme.background.luminance() > 0.5) CodeThemeType.Default.theme else CodeThemeType.Monokai.theme,
) {
    val state = LocalEditor.current
    val scope = rememberCoroutineScope()
    var editing by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .alpha(0.7f)
                .zIndex(99f)
        ) {
            IconButton(
                modifier = if (editing) {
                    Modifier.padding(end = 8.dp)
                } else {
                    Modifier
                },
                onClick = {
                    editing = !editing
                }
            ) {
                Icon(
                    imageVector = if (!editing) {
                        MaterialDesignIcons.PencilOutline
                    } else {
                        MaterialDesignIcons.Check
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            if (editing) {
                IconButton(onClick = {
                    state.blocks.remove(this@MathComponent)
                }) {
                    Icon(
                        imageVector = MaterialDesignIcons.TrashCanOutline,
                        contentDescription = "Delete Icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {

            MathJax(
                modifier = Modifier.fillMaxWidth(),
                latex = latex,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (editing) {

                var lastUndoRedoTime by remember { mutableStateOf(currentTimeMillis()) }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it.copy(
//                            annotatedString = parseCodeAsAnnotatedString(
//                                parser = parser,
//                                theme = theme,
//                                lang = "latex",
//                                code = it.text
//                            )
                        )
                        scope.launch {
                            state.textFieldUndoRedoAction(
                                lastUndoRedoSaveTime = lastUndoRedoTime,
                                getCurrentTextFieldValue = { textFieldValue },
                                updateTextFieldValue = { newValue -> textFieldValue = newValue },
                                updateUndoRedoTime = { time -> lastUndoRedoTime = time }
                            )
                        }
                    },
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                    placeholder = {
                        Text(text = "Latex", color = MaterialTheme.colorScheme.onBackground.copy(.4f))
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}