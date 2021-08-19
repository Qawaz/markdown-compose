package com.wakaztahir.timeline.blockly.model

import com.wakaztahir.timeline.blockly.serializers.BlockSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BlockSerializer::class)
abstract class Block {
    abstract fun exportText(): String
    abstract fun exportMarkdown(): String
    abstract fun exportHTML(): String
}