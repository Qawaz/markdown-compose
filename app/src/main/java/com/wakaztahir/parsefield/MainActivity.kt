package com.wakaztahir.parsefield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wakaztahir.parsefield.ui.components.Controller
import com.wakaztahir.parsefield.ui.theme.ParseFieldTheme
import com.wakaztahir.textparser.MarkdownText
import com.wakaztahir.timeline.blockly.components.blocks.listblock.ListBlock
import com.wakaztahir.timeline.blockly.model.ListItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParseFieldTheme {
                // rendering a list block
                val listBlock = remember {
                    com.wakaztahir.timeline.blockly.model.ListBlock().apply {
                        items.add(ListItem().apply { text = "First Item" })
                        items.add(ListItem().apply { text = "Second Item" })
                        items.add(ListItem().apply { text = "Third Item" })
                        items.add(ListItem().apply { text = "Fourth Item" })
                        items.add(ListItem().apply { text = "Fifth Item" })
                    }
                }
                Column {
                    ListBlock(
                        block = listBlock,
                        onUpdate = { /*TODO*/ },
                        onRemove = { /*TODO*/ }
                    )
                }
            }
        }
    }
}