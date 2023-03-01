package com.wakaztahir.markdowncompose.codeeditor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.wakaztahir.codeeditor.model.CodeLang
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeTheme
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import com.wakaztahir.helpers.DropdownMenu
import com.wakaztahir.helpers.DropdownMenuItem
import com.wakaztahir.helpers.ExposedDropdownMenu
import com.wakaztahir.helpers.currentTimeMillis
import com.wakaztahir.markdowncompose.editor.components.LocalEditor
import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.utils.textFieldUndoRedoAction
import com.wakaztahir.markdowncompose.utils.*
import compose.icons.MaterialDesignIcons
import compose.icons.materialdesignicons.TrashCanOutline
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBlock.CodeComponent(
    modifier: Modifier = Modifier,
    parser: PrettifyParser = prettifyParser,
    theme: CodeTheme = if (MaterialTheme.colorScheme.background.luminance() > 0.5) CodeThemeType.Default.theme else CodeThemeType.Monokai.theme,
) {

    val state = LocalEditor.current
    val block = this
    var langMenu by remember { mutableStateOf(false) }
    var lang by remember { mutableStateOf(block.lang) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box {
                ExposedDropdownMenu(
                    expanded = langMenu,
                    onToggleExpansion = { langMenu = !langMenu },
                    outlined = true,
                    text = CodeLang.values().firstOrNull { it.value.any { it == lang } }?.name
                        ?: block.lang
                ) {
                    DropdownMenu(expanded = langMenu, onDismissRequest = { langMenu = false }) {
                        CodeLang.values().forEach { codeLang ->
                            DropdownMenuItem(
                                text = { Text(text = codeLang.name) },
                                onClick = {
                                    block.lang = codeLang.value.first()
                                    lang = codeLang.value.first()
                                    langMenu = false
                                }
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { state.blocks.remove(this@CodeComponent) }) {
                    Icon(
                        imageVector = MaterialDesignIcons.TrashCanOutline,
                        contentDescription = "Delete Icon",
                        tint = MaterialTheme.colorScheme.onBackground
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

        var lastUndoRedoTime by remember { mutableStateOf(currentTimeMillis()) }

        var codeJob by remember { mutableStateOf<Job?>(null) }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
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