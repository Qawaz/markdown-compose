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

class BlockTest {

    private val state = EditorState()
    private val json = Json {
        serializersModule = SerializersModule {
            polymorphicEditorBlockSerializer()
        }
    }

    @Test
    fun testCodeBlock() {
        val block = CodeBlock("js", "let x = 5")
        assertEquals("""<pre><code class='language-js'>let x = 5</code></pre>""", block.exportHTML(state))
        assertEquals("""${"\n"}```js${"\n"}let x = 5${"\n"}```${"\n"}""", block.exportMarkdownNew(state))
        assertEquals("""Code (js) : let x = 5${"\n"}""", block.exportText(state))
        assertEquals("""{"lang":"js","code":"let x = 5"}""", json.encodeToString(block))
        assertEquals(json.decodeFromString("""{"lang":"js","code":"let x = 5"}"""), block)
    }

    @Test
    fun testTextBlock() {
        val block = TextBlock("this is my text")
        assertEquals("""this is my text""", block.exportHTML(state))
        assertEquals("""this is my text""", block.exportMarkdownNew(state))
        assertEquals("""this is my text""", block.exportText(state))
        assertEquals("""{"text":{"children":[{"type":"text","text":"this is my text"}]}}""", json.encodeToString(block))
        val deserializedBlock = json.decodeFromString<TextBlock>("""{"text":{"children":[{"type":"text","text":"this is my text"}]}}""")
        assertEquals(deserializedBlock, block)
        assertEquals(deserializedBlock.textValue, block.textValue)
    }

    @Test
    fun testMathBlock() {
        val block = MathBlock("x = 5")
        assertEquals("""<div id='math'>x = 5</div>""", block.exportHTML(state))
        assertEquals("""${"\n"}```latex${"\n"}x = 5${"\n"}```${"\n"}""", block.exportMarkdownNew(state))
        assertEquals("""Latex : x = 5${"\n"}""", block.exportText(state))
        assertEquals("""{"latex":"x = 5"}""", json.encodeToString(block))
        assertEquals(json.decodeFromString("""{"latex":"x = 5"}"""), block)
    }

    @Test
    fun testListItemBlock() {
        val block = ListItemBlock("My first list item")
        assertEquals("""<li>My first list item</li>""", block.exportHTML(state))
        assertEquals(""" - [ ] My first list item""", block.exportMarkdownNew(state))
        assertEquals("""My first list item""", block.exportText(state))
        assertEquals(
            """{"value":{"children":[{"type":"text","text":"My first list item"}]},"is_checked":false,"indentation":0}""",
            json.encodeToString(block)
        )
        val deserializedBlock = json.decodeFromString<ListItemBlock>("""{"value":{"children":[{"type":"text","text":"My first list item"}]},"is_checked":false,"indentation":0}""")
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
    fun markdownToJsonTest() {
        val markdown = """
            
        """.trimIndent()
    }

}