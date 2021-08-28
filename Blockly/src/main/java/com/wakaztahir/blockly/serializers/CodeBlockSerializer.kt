package com.wakaztahir.blockly.serializers

import com.wakaztahir.blockly.components.code.ace.AceEditor
import com.wakaztahir.blockly.model.CodeBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CodeBlock")
class CodeBlockSurrogate(
    val mode: AceEditor.Mode,
    val theme : AceEditor.Theme?,
    val value: String,
) : BlockSurrogate()

internal fun CodeBlock.toSurrogate(): CodeBlockSurrogate {
    return CodeBlockSurrogate(
        mode = this.mode,
        theme = this.theme,
        value = this.value
    )
}

internal fun CodeBlockSurrogate.toCodeBlock(): CodeBlock {
    return CodeBlock().apply {
        this.mode = this@toCodeBlock.mode
        this.theme = this@toCodeBlock.theme
        this.value = this@toCodeBlock.value
    }
}