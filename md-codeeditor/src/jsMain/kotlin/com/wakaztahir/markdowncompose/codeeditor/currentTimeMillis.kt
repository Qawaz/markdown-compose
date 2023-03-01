package com.wakaztahir.markdowncompose.codeeditor

import kotlin.js.Date

internal actual fun currentTimeMillis(): Long = Date.now().toLong()