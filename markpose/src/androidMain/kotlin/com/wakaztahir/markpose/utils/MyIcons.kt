package com.wakaztahir.markpose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.wakaztahir.markpose.R

internal actual object MyIcons {
    actual val CircleSmall: Painter
        @Composable get() = painterResource(R.drawable.circle_small)
    actual val DragIndicator: Painter
        @Composable get() = painterResource(R.drawable.drag_indicator)
    actual val Delete: Painter
        @Composable get() = painterResource(R.drawable.delete)
    actual val Edit: Painter
        @Composable get() = painterResource(R.drawable.edit)
    actual val Check: Painter
        @Composable get() = painterResource(R.drawable.check)
}