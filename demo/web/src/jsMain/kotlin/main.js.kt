import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.wakaztahir.common.MDPreviewEditor
import com.wakaztahir.common.rememberColorScheme
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        BrowserViewportWindow("ComposeMarkdown") {
            MaterialTheme(rememberColorScheme()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    MDPreviewEditor()
                }
            }
        }
    }
}


