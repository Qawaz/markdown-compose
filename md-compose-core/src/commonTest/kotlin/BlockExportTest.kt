import com.wakaztahir.markdowncompose.editor.model.blocks.CodeBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.ListItemBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.MathBlock
import com.wakaztahir.markdowncompose.editor.model.blocks.TextBlock
import com.wakaztahir.markdowncompose.editor.states.EditorState
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockExportTest {

    private val state = EditorState()

    @Test
    fun testCodeBlock() {
        val block = CodeBlock("js", "let x = 5")
        assertEquals("""<pre><code class='language-js'>let x = 5</code></pre>""", block.exportHTML(state))
        assertEquals("""${"\n"}```js${"\n"}let x = 5${"\n"}```${"\n"}""", block.toMarkdown(state))
        assertEquals("""Code (js) : let x = 5${"\n"}""", block.exportText(state))
    }

    @Test
    fun testTextBlock() {
        val block = TextBlock("this is my text")
        assertEquals("""this is my text""", block.exportHTML(state))
        assertEquals("""this is my text""", block.toMarkdown(state))
        assertEquals("""this is my text""", block.exportText(state))
    }

    @Test
    fun testMathBlock() {
        val block = MathBlock("x = 5")
        assertEquals("""<div id='math'>x = 5</div>""", block.exportHTML(state))
        assertEquals("""${"\n"}```latex${"\n"}x = 5${"\n"}```${"\n"}""", block.toMarkdown(state))
        assertEquals("""Latex : x = 5${"\n"}""", block.exportText(state))
    }

    @Test
    fun testListItemBlock() {
        val block = ListItemBlock("My first list item")
        assertEquals("""<li>My first list item</li>""", block.exportHTML(state))
        assertEquals(""" - [ ] My first list item""", block.toMarkdown(state))
        assertEquals("""My first list item""", block.exportText(state))
    }

}