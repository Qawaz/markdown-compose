package com.wakaztahir.blockly.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.wakaztahir.blockly.components.code.ace.AceEditor
import com.wakaztahir.blockly.serializers.BlockSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable(with = BlockSerializer::class)
class CodeBlock : Block() {

    var mode by mutableStateOf(AceEditor.Mode.Text)
    var value: String = ""

    override fun exportText(): String {
        return ""
    }

    override fun exportMarkdown(): String {
        return """
            ```${mode.name.lowercase(Locale.getDefault())}
            $value
            ```
        """.trimIndent()
    }

    override fun exportHTML(): String {
        return "<pre><code class='language-${mode.name.lowercase()}'>$value</code></pre>"
    }
}