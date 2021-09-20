package com.wakaztahir.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString
import com.wakaztahir.composejlatex.latexImageBitmap
import com.wakaztahir.example.ui.theme.MarkdownTextFieldTheme
import com.wakaztahir.markdowntext.editor.MarkdownEditor
import com.wakaztahir.markdowntext.editor.rememberParsedMarkdown
import com.wakaztahir.markdowntext.preview.MarkdownPreview
import com.wakaztahir.markdowntext.preview.model.PreviewRenderer

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalUnitApi::class, androidx.compose.ui.text.InternalTextApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkdownTextFieldTheme {


                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                    CustomEditorWithTextField()
                }
            }
        }
    }
}

/**
 * Renders a text field and compose editor vertically in a column
 * with latex and syntax highlighting support
 */
@Composable
fun CustomEditorWithTextField(){

    var markdown by remember {
        mutableStateOf(
            TextFieldValue(annotatedString = AnnotatedString(""))
        )
    }

    val parsed = rememberParsedMarkdown(markdown = markdown.text)

    val codeParser = remember { PrettifyParser() }
    val codeTheme =
        if (MaterialTheme.colors.isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme()

    Column {
        TextField(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            value = markdown,
            onValueChange = {
                markdown = it.copy(
                    annotatedString = parseCodeAsAnnotatedString(
                        codeParser,
                        codeTheme,
                        CodeLang.Markdown,
                        it.text
                    )
                )
            }
        )

        MarkdownEditor(
            parsed = parsed
        )
    }
}

/**
 * Renders a text field and compose preview vertically in a column
 * with latex and syntax highlighting support
 */
@Composable
fun CustomPreviewWithTextField(){

    var markdown by remember {
        mutableStateOf(
            TextFieldValue(annotatedString = AnnotatedString(""))
        )
    }

    val codeParser = remember { PrettifyParser() }
    val codeTheme =
        if (MaterialTheme.colors.isLight) CodeThemeType.Default.theme() else CodeThemeType.Monokai.theme()

    Column {
        TextField(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            value = markdown,
            onValueChange = {
                markdown = it.copy(
                    annotatedString = parseCodeAsAnnotatedString(
                        codeParser,
                        codeTheme,
                        CodeLang.Markdown,
                        it.text
                    )
                )
            }
        )

        MarkdownPreview(
            markdown = markdown.text,
            renderer = object : PreviewRenderer() {
                @Composable
                override fun PreviewFencedCodeBlock(
                    isParentDocument: Boolean,
                    info: String,
                    literal: String,
                    fenceChar: Char,
                    fenceIndent: Int,
                    fenceLength: Int
                ) {
                    if(info!="latex") {
                        val code = buildAnnotatedString {
                            append(
                                parseCodeAsAnnotatedString(
                                    codeParser,
                                    codeTheme,
                                    info,
                                    literal
                                )
                            )
                        }

                        Text(
                            text = code,
                        )
                    }else{
                        kotlin.runCatching {
                            latexImageBitmap(
                                latex = literal,
                                color = MaterialTheme.colors.onBackground
                            )
                        }.getOrNull()?.let {
                            Image(bitmap = it, contentDescription = null)
                        }
                    }
                }

                @Composable
                override fun PreviewIndentedCodeBlock(
                    isParentDocument: Boolean,
                    literal: String
                ) {
                    val code = buildAnnotatedString {
                        append(
                            parseCodeAsAnnotatedString(
                                codeParser,
                                codeTheme,
                                "js",
                                literal
                            )
                        )
                    }

                    Text(
                        text = code,
                    )
                }
            }
        )
    }
}