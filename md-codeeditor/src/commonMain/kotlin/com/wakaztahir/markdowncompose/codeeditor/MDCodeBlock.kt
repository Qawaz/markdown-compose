package com.wakaztahir.markdowncompose.codeeditor

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import com.wakaztahir.markdowncompose.core.MarkdownPreviewConfiguration

internal val prettifyParser by lazy { PrettifyParser() }

@Composable
internal fun Code(
    configuration: MarkdownPreviewConfiguration,
    code: String,
    lang: String? = null,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Surface(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        val scroll = rememberScrollState(0)
        val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5
        val parser = prettifyParser
        val annotatedString = remember(code, lang, parser) {
            parseCodeAsAnnotatedString(
                parser,
                if (isLight) CodeThemeType.Default.theme else CodeThemeType.Monokai.theme,
                lang ?: "js",
                code
            )
        }
        Text(
            buildAnnotatedString { configuration.modify(this, annotatedString) },
            modifier = Modifier
                .horizontalScroll(scroll)
                .padding(8.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                color = color
            )
        )
    }
}