package com.wakaztahir.blockly.serializers

import com.wakaztahir.blockly.model.MathBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("MathBlock")
class MathBlockSurrogate(
    val latex: String
) : BlockSurrogate()

internal fun MathBlock.toSurrogate(): MathBlockSurrogate {
    return MathBlockSurrogate(
        latex = this.latex
    )
}

internal fun MathBlockSurrogate.toMathBlock(): MathBlock {
    return MathBlock().apply {
        this.latex = this@toMathBlock.latex
    }
}