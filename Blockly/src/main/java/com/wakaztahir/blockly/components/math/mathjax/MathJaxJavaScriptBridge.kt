package com.wakaztahir.blockly.components.math.mathjax

import android.webkit.JavascriptInterface

class MathJaxJavaScriptBridge(owner: MathJaxView) {
    var mOwner: MathJaxView = owner

    @JavascriptInterface
    fun rendered() {
        mOwner.rendered()
    }

}
