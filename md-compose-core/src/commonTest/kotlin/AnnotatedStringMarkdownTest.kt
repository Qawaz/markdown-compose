import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.wakaztahir.markdowncompose.editor.utils.toMarkdown
import kotlin.test.Test
import kotlin.test.assertEquals

class AnnotatedStringMarkdownTest {

    @Test
    fun noStylesWork() {
        assertEquals(
            "My Text",
            AnnotatedString("My Text").toMarkdown(true)
        )
        assertEquals(
            "My Text",
            AnnotatedString("My Text").toMarkdown(false)
        )
    }

    @Test
    fun singleStyleWorks() {
        with(buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append("My Text")
            pop()
        }) {
            assertEquals(
                "**My Text**",
                toMarkdown(true)
            )
            assertEquals(
                "**My Text**",
                toMarkdown(false)
            )
        }
    }

    @Test
    fun multipleCompleteStylesWork() {
        with(buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic))
            append("My Text")
            pop()
        }) {
            assertEquals(
                "***My Text***",
                toMarkdown(true)
            )
            assertEquals(
                "***My Text***",
                toMarkdown(false)
            )
        }
    }

    @Test
    fun singleMiddleStyleWorks() {
        with(buildAnnotatedString {
            append("Text")
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append("Bold")
            pop()
            append("Not")
        }) {
            assertEquals(
                "Text**Bold**Not",
                toMarkdown(true)
            )
            assertEquals(
                "Text**Bold**Not",
                toMarkdown(false)
            )
        }
    }

    @Test
    fun testMultipleChildrenWork() {
        with(buildAnnotatedString {
            append("Start")
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic))
            append("My")
            pop()
            append("Middle")
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic))
            append("Text")
            pop()
            append("End")
        }) {
            assertEquals(
                "Start***My***Middle***Text***End",
                toMarkdown(false)
            )
            assertEquals(
                "Start***My***Middle***Text***End",
                toMarkdown(true)
            )
        }

    }

    @Test
    fun testNestedStylingWorks() {
        with(buildAnnotatedString {
            append("Start")
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append("My")
            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
            append("Middle")
            pop()
            append("Text")
            pop()
            append("End")
        }) {
            assertEquals(
                "Start**My*Middle*Text**End",
                toMarkdown(true)
            )
            assertEquals(
                "Start**My*****Middle*****Text**End",
                toMarkdown(false)
            )
        }
    }

    @OptIn(ExperimentalTextApi::class)
    @Test
    fun testLink() {
        with(buildAnnotatedString {
            append("Start")
            pushUrlAnnotation(UrlAnnotation("http://google.com"))
            append("Link")
            pop()
            append("End")
        }) {
            assertEquals(
                "Start[Link](http://google.com)End",
                toMarkdown(false)
            )
            assertEquals(
                "Start[Link](http://google.com)End",
                toMarkdown(true)
            )
        }
    }

}