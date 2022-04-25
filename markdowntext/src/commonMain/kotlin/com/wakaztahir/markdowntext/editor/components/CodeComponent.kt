package com.wakaztahir.markdowntext.editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wakaztahir.codeeditor.model.CodeLang
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeTheme
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import com.wakaztahir.markdowntext.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowntext.editor.utils.textFieldUndoRedoAction
import com.wakaztahir.markdowntext.utils.*
import kotlinx.coroutines.launch

val LocalPrettifyParser = compositionLocalOf { PrettifyParser() }

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun CodeBlock.CodeComponent(
    modifier: Modifier = Modifier,
    parser: PrettifyParser = LocalPrettifyParser.current,
    theme: CodeTheme = if (MaterialTheme.colors.isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme(),
) {

    val state = LocalEditor.current
    val block = this
    var langMenu by remember { mutableStateOf(false) }
    var lang by remember { mutableStateOf(block.lang) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box {
                MenuSelector(
                    onClick = { langMenu = true },
                    border = null,
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent),
                    fullWidth = false,
                ) {
                    Text(
                        text = CodeLang.values().firstOrNull { it.value.any { it == lang } }?.name
                            ?: block.lang
                    )
                }
                DropMenu(expanded = langMenu, onDismissRequest = { langMenu = false }) {
                    CodeLang.values().forEach { codeLang ->
                        MenuItem(
                            text = codeLang.name,
                            onClick = {
                                block.lang = codeLang.value.first()
                                lang = codeLang.value.first()
                                langMenu = false
                            }
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { state.blocks.remove(this@CodeComponent) }) {
                    Icon(
                        painter = MyIcons.Delete,
                        contentDescription = "Delete Icon",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        }

        LaunchedEffect(key1 = lang, block = {
            textFieldValue = textFieldValue.copy(
                annotatedString = parseCodeAsAnnotatedString(
                    parser = parser,
                    theme = theme,
                    lang = block.lang,
                    code = textFieldValue.text
                )
            )
        })

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
                        lang = block.lang,
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )
    }
}