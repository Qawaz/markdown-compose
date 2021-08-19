package com.wakaztahir.timeline.blockly.serializers

import com.wakaztahir.timeline.blockly.model.Block
import com.wakaztahir.timeline.blockly.model.ListBlock
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

// Surrogate
@SerialName("Block")
@Serializable
abstract class BlockSurrogate

object BlockSerializer : KSerializer<Block> {

    private val serializer = BlockSurrogate.serializer()

    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder): Block {
        return when (val surrogate = decoder.decodeSerializableValue(serializer)) {
            is ListBlockSurrogate -> {
                surrogate.toListBlock()
            }
            else -> {
                ListBlock()
            }
        }
    }

    override fun serialize(encoder: Encoder, value: Block) {
        when (value) {
            is ListBlock -> {
                encoder.encodeSerializableValue(serializer, value.toSurrogate())
            }
        }
    }

}

// Blockly Serializer

private val module = SerializersModule {
    polymorphic(BlockSurrogate::class) {
        subclass(ListBlockSurrogate::class)
    }
}

val BlocklySerializer = Json { serializersModule = module }

fun Block.Companion.serialize(block: Block): String {
    return BlocklySerializer.encodeToString(block)
}

fun Block.Companion.deserialize(json: String): Block {
    return BlocklySerializer.decodeFromString(json)
}

