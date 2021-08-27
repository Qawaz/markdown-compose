package com.wakaztahir.blockly.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.wakaztahir.blockly.serializers.BlockSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockSerializer::class)
class MathBlock : Block() {

    var latex by mutableStateOf("")

    override fun exportText(): String {
        return ""
    }

    override fun exportMarkdown(): String {
        return """
            ```latex
            $latex
            ```
        """.trimIndent()
    }

    override fun exportHTML(): String {
        return "<div id='math'>$latex</div>"
    }
}