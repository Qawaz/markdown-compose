package com.wakaztahir.blockly.model

import com.wakaztahir.blockly.serializers.BlockSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockSerializer::class)
abstract class Block {
    abstract fun exportText(): String
    abstract fun exportMarkdown(): String
    abstract fun exportHTML(): String
}