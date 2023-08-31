import com.wakaztahir.markdowncompose.editor.model.EditorBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.serialization.polymorphicEditorBlockSerializer
import com.wakaztahir.markdowncompose.editor.states.EditorState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockSerializationTest {

    private val json = Json {
        serializersModule = SerializersModule {
            polymorphicEditorBlockSerializer()
        }
    }

    @Test
    fun testCodeBlock() {
        val block = CodeBlock("js", "let x = 5")
        assertEquals("""{"lang":"js","code":"let x = 5"}""", json.encodeToString(block))
        assertEquals(json.decodeFromString("""{"lang":"js","code":"let x = 5"}"""), block)
    }

    @Test
    fun testTextBlock() {
        val block = TextBlock("this is my text")
        assertEquals("""{"text":{"children":[{"type":"text","text":"this is my text"}]}}""", json.encodeToString(block))
        val deserializedBlock =
            json.decodeFromString<TextBlock>("""{"text":{"children":[{"type":"text","text":"this is my text"}]}}""")
        assertEquals(deserializedBlock, block)
        assertEquals(deserializedBlock.textValue, block.textValue)
    }

    @Test
    fun testMathBlock() {
        val block = MathBlock("x = 5")
        assertEquals("""{"latex":"x = 5"}""", json.encodeToString(block))
        assertEquals(json.decodeFromString("""{"latex":"x = 5"}"""), block)
    }

    @Test
    fun testListItemBlock() {
        val block = ListItemBlock("My first list item")
        assertEquals(
            """{"value":{"children":[{"type":"text","text":"My first list item"}]},"is_checked":false,"indentation":0}""",
            json.encodeToString(block)
        )
        val deserializedBlock =
            json.decodeFromString<ListItemBlock>("""{"value":{"children":[{"type":"text","text":"My first list item"}]},"is_checked":false,"indentation":0}""")
        assertEquals(
            deserializedBlock,
            block
        )
        assertEquals(
            deserializedBlock.text,
            block.text
        )
        block.isIndented = true
        assertEquals(
            """{"value":{"children":[{"type":"text","text":"My first list item"}]},"is_checked":false,"indentation":1}""",
            json.encodeToString(block)
        )
    }

    @Test
    fun testMultipleBlocksSerialization() {
        val list = listOf<EditorBlock>(
            TextBlock("this is my text"),
            ListItemBlock("My first list item", isChecked = true, isIndented = true),
            MathBlock("x = 5"),
            CodeBlock("js", "let x = 5")
        )
        assertEquals(
            """[{"type":"paragraph","text":{"children":[{"type":"text","text":"this is my text"}]}},{"type":"list-item","value":{"children":[{"type":"text","text":"My first list item"}]},"is_checked":true,"indentation":1},{"type":"math","latex":"x = 5"},{"type":"code","lang":"js","code":"let x = 5"}]""",
            json.encodeToString(list)
        )
        assertEquals(
            list,
            json.decodeFromString<List<EditorBlock>>("""[{"type":"paragraph","text":{"children":[{"type":"text","text":"this is my text"}]}},{"type":"list-item","value":{"children":[{"type":"text","text":"My first list item"}]},"is_checked":true,"indentation":1},{"type":"math","latex":"x = 5"},{"type":"code","lang":"js","code":"let x = 5"}]""")
        )
    }

}