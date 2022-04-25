package com.wakaztahir.markpose.editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeTheme
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import com.wakaztahir.markpose.editor.MathJax
import com.wakaztahir.markpose.editor.model.blocks.MathBlock
import com.wakaztahir.markpose.editor.utils.textFieldUndoRedoAction
import com.wakaztahir.markpose.utils.MyIcons
import com.wakaztahir.markpose.utils.getTimeMillis
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MathBlock.MathComponent(
    modifier: Modifier = Modifier,
    parser: PrettifyParser = LocalPrettifyParser.current,
    theme: CodeTheme = if (MaterialTheme.colors.isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme(),
) {
    val state = LocalEditor.current
    val block = this
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
                    painter = if (!editing) {
                        MyIcons.Edit
                    } else {
                        MyIcons.Check
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground
                )
            }
            if (editing) {
                IconButton(onClick = {
                    state.blocks.remove(this@MathComponent)
                }) {
                    Icon(
                        painter = MyIcons.Delete,
                        contentDescription = "Delete Icon",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {

            MathJax(modifier = Modifier.fillMaxWidth(), latex = latex)

            if (editing) {

                LaunchedEffect(null) {
                    textFieldValue = TextFieldValue(
                        parseCodeAsAnnotatedString(
                            parser = parser,
                            theme = theme,
                            lang = "latex",
                            code = block.latex
                        )
                    )
                }

                var lastUndoRedoTime by remember { mutableStateOf(getTimeMillis()) }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it.copy(
                            annotatedString = parseCodeAsAnnotatedString(
                                parser = parser,
                                theme = theme,
                                lang = "latex",
                                code = it.text
                            )
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
                        Text(text = "Latex", color = MaterialTheme.colors.onBackground.copy(.4f))
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