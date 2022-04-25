package com.wakaztahir.markdowntext.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

internal actual object MyIcons {
    actual val CircleSmall: Painter
        @Composable
        get() = painterResource("icons/circle_small.xml")
    actual val DragIndicator: Painter
        @Composable
        get() = painterResource("icons/drag_indicator.xml")
    actual val Delete: Painter
        @Composable
        get() = painterResource("icons/delete.xml")
    actual val Edit: Painter
        @Composable
        get() = painterResource("icons/edit.xml")
    actual val Check: Painter
        @Composable
        get() = painterResource("icons/check.xml")

}