package com.wakaztahir.parsefield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.blockly.components.blocks.textblock.TextComponent
import com.wakaztahir.blockly.components.code.CodeComponent
import com.wakaztahir.blockly.components.code.ace.AceEditor
import com.wakaztahir.blockly.components.list.ListComponent
import com.wakaztahir.blockly.components.math.MathComponent
import com.wakaztahir.blockly.model.*
import com.wakaztahir.blockly.serializers.deserialize
import com.wakaztahir.blockly.serializers.serialize
import com.wakaztahir.parsefield.ui.theme.ParseFieldTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParseFieldTheme {
                // rendering a list block
                val listBlock = remember {
                    ListBlock().apply {
                        items.add(ListItem().apply { text = "First Item" })
                        items.add(ListItem().apply { text = "Second Item" })
                        items.add(ListItem().apply { text = "Third Item" })
                        items.add(ListItem().apply { text = "Fourth Item" })
                        items.add(ListItem().apply { text = "Fifth Item" })
                    }
                }
                Column {

                    val scope = rememberCoroutineScope()

                    val initialLatex = """$$\\ x + 3 \\$$"""

                    val mathBlock = remember {
                        MathBlock().apply {
                            latex = initialLatex
                        }
                    }

                    var codeBlock by remember {
                        mutableStateOf(CodeBlock().apply {
                            value = "<div><b>Hello</b></div>"
                        })
                    }

                    MathComponent(
                        block = mathBlock
                    )
//                    CodeComponent(
//                        mode = AceEditor.Mode.LaTeX,
//                        theme = if (MaterialTheme.colors.isLight) {
//                            AceEditor.Theme.TEXTMATE
//                        } else {
//                            AceEditor.Theme.MONOKAI
//                        },
//                        value = initialLatex,
//                        onValueChange = {
//                            scope.coroutineContext.cancelChildren()
//                            scope.launch(Dispatchers.Main) {
//                                requestText {
//                                    mathBlock.latex = it
//                                }
//                            }
//                        }
//                    )
                    CodeComponent(
                        block = codeBlock,
                        theme = if (MaterialTheme.colors.isLight) {
                            AceEditor.Theme.TEXTMATE
                        } else {
                            AceEditor.Theme.MONOKAI
                        },
                        onUpdate = {}
                    )

                    LaunchedEffect(key1 = codeBlock, block = {
                        delay(10000)
                        codeBlock = Block.deserialize(Block.serialize(codeBlock)) as CodeBlock
                    })

                    TextComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    ListComponent(
                        block = listBlock,
                        onUpdate = { /*TODO*/ },
                        onRemove = { /*TODO*/ }
                    )
                }
            }
        }
    }
}