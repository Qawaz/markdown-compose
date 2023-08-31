import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.wakaztahir.markdowncompose.editor.utils.toHtml
import kotlin.test.Test
import kotlin.test.assertEquals

class AnnotatedStringHTMLTest {

    @Test
    fun noStylesWork() {
        assertEquals(
            "My Text",
            AnnotatedString("My Text").toHtml(true)
        )
        assertEquals(
            "My Text",
            AnnotatedString("My Text").toHtml(false)
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
                "<strong>My Text</strong>",
                toHtml(true)
            )
            assertEquals(
                "<strong>My Text</strong>",
                toHtml(false)
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
                "<strong><em>My Text</em></strong>",
                toHtml(true)
            )
            assertEquals(
                "<strong><em>My Text</em></strong>",
                toHtml(false)
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
                "Text<strong>Bold</strong>Not",
                toHtml(true)
            )
            assertEquals(
                "Text<strong>Bold</strong>Not",
                toHtml(false)
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
                "Start<strong><em>My</em></strong>Middle<strong><em>Text</em></strong>End",
                toHtml(false)
            )
            assertEquals(
                "Start<strong><em>My</em></strong>Middle<strong><em>Text</em></strong>End",
                toHtml(true)
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
                "Start<strong>My<em>Middle</em>Text</strong>End",
                toHtml(true)
            )
            assertEquals(
                "Start<strong>My</strong><strong><em>Middle</em></strong><strong>Text</strong>End",
                toHtml(false)
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
                "Start<a href=\"http://google.com\">Link</a>End",
                toHtml(false)
            )
            assertEquals(
                "Start<a href=\"http://google.com\">Link</a>End",
                toHtml(true)
            )
        }
    }

}