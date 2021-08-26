package com.wakaztahir.blockly.components.math.mathjax

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient


@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class MathJaxView(
    context: Context,
    outputScale: Int = 1,
    minScale: Int = 1,
) : WebView(context) {

    companion object {
        private const val HTML_LOCATION = "file:///android_asset/math_jax.html"
    }

    private var inputText: String? = null
    private var onLoaded: () -> Unit = {}
    private var onRendered: () -> Unit = {}

    init {
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (inputText != null && inputText?.isNotEmpty() == true) {
                    setInputText(inputText)
                }
                onLoaded()
            }
        }
        addJavascriptInterface(
            MathJaxBridge(
                mathJax = this,
                outputScale = outputScale,
                minScale = minScale,
                onRendered = { onRendered() }
            ), "Bridge"
        )

        // be careful, we do not need internet access
        settings.blockNetworkLoads = true
        settings.javaScriptEnabled = true
        loadUrl(HTML_LOCATION)
        isHorizontalScrollBarEnabled = false
        setBackgroundColor(0)
    }

    // External Functions

    fun setOnLoadedListener(onLoad: () -> Unit) {
        onLoaded = onLoad
    }

    fun setOnRenderedListener(onRender: () -> Unit) {
        onRendered = onRender
    }

    fun setInputText(inputText: String?) {
        this.inputText = inputText
        val laTexString: String = inputText?.let { doubleEscapeTeX(it) } ?: ""
        evaluateJavascript("setTex(\"$laTexString\")") {
            // do something
        }
    }

    fun setTexColor(colorStr : String){
        evaluateJavascript("setTexColor(\"$colorStr\")"){
            // do something
        }
    }

    fun getInputText(): String? {
        return inputText
    }

    // Private Functions

    private fun doubleEscapeTeX(s: String): String {
        var t = ""
        for (i in s.indices) {
            if (s[i] == '\'') t += '\\'
            if (s[i] == '\\') t += "\\"
            if (s[i] != '\n') t += s[i]
        }
        return t
    }
}
