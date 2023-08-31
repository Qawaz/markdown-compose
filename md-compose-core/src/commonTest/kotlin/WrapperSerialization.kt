import com.wakaztahir.markdowncompose.editor.wrapper.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test
import kotlin.test.assertEquals

class WrapperSerialization {


    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Wrapper::class) {
                subclass(TextWrap::class)
                subclass(BoldWrap::class)
                subclass(ItalicWrap::class)
            }
        }
    }

    @Test
    fun testSerialization() {
        val first = ChildrenWrap(BoldWrap(ItalicWrap(TextWrap("text1"))))
        val second = ChildrenWrap(ItalicWrap(BoldWrap(TextWrap("text2"))))
        val third = ChildrenWrap(BoldWrap(TextWrap("text3")))

        assertEquals(
            """{"children":[{"type":"bold","item":{"type":"italic","item":{"type":"text","text":"text1"}}}]}""",
            json.encodeToString(first)
        )
        assertEquals(
            """{"children":[{"type":"italic","item":{"type":"bold","item":{"type":"text","text":"text2"}}}]}""",
            json.encodeToString(second)
        )
        assertEquals(
            """{"children":[{"type":"bold","item":{"type":"text","text":"text3"}}]}""",
            json.encodeToString(third)
        )

        assertEquals(
            first,
            json.decodeFromString("""{"children":[{"type":"bold","item":{"type":"italic","item":{"type":"text","text":"text1"}}}]}""")
        )
        assertEquals(
            second,
            json.decodeFromString("""{"children":[{"type":"italic","item":{"type":"bold","item":{"type":"text","text":"text2"}}}]}""")
        )
        assertEquals(
            third,
            json.decodeFromString("""{"children":[{"type":"bold","item":{"type":"text","text":"text3"}}]}""")
        )

    }

}