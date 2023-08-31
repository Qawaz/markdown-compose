package com.wakaztahir.markdowncompose.editor.serialization

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer

fun SerializersModuleBuilder.polymorphicEditorBlockSerializer() {
    polymorphic(EditorBlock::class) {
        subclass(CodeBlock::class)
        subclass(ListItemBlock::class)
        subclass(MathBlock::class)
        subclass(TextBlock::class)
    }
}

internal open class MutableStateSerializer<T>(
    private val dataSerializer: KSerializer<T>
) : KSerializer<MutableState<T>> {

    override val descriptor: SerialDescriptor get() = dataSerializer.descriptor

    override fun deserialize(decoder: Decoder): MutableState<T> =
        mutableStateOf(dataSerializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: MutableState<T>) =
        dataSerializer.serialize(encoder, value.value)

}

internal object TFVAsTextSerializer : KSerializer<TextFieldValue> {

    private val serializer = serializer<String>()

    override val descriptor: SerialDescriptor
        get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): TextFieldValue {
        return TextFieldValue(text = serializer.deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: TextFieldValue) {
        serializer.serialize(encoder, value.text)
    }

}