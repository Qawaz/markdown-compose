import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wakaztahir.common.MDPreviewEditor
import com.wakaztahir.common.rememberColorScheme
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        BrowserViewportWindow("ComposeMarkdown") {
            MaterialTheme(rememberColorScheme()) {
                val scrollState = rememberScrollState()
                Box(modifier = Modifier.fillMaxSize()) {
                    MDPreviewEditor(modifier = Modifier.fillMaxSize().verticalScroll(scrollState))
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = scrollState),
                    )
                }
            }
        }
    }
}


