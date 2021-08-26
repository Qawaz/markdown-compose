package com.wakaztahir.blockly.components.math.mathjax

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.webkit.WebView
import android.webkit.WebViewClient


class MathJaxView : WebView {
    private var inputText: String? = null
    protected var mBridge: MathJaxJavaScriptBridge? = null

    /**
     * laTex can only be rendered when WebView is already loaded
     */
    private var webViewLoaded = false

    interface OnMathJaxRenderListener {
        fun onRendered()
    }

    private var onMathJaxRenderListener: OnMathJaxRenderListener? = null

    constructor(context: Context, config: MathJaxConfig? = null) : super(context) {
        init(context, config)
    }

    fun setRenderListener(onMathJaxRenderListener: OnMathJaxRenderListener?) {
        this.onMathJaxRenderListener = onMathJaxRenderListener
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun init(context: Context, config: MathJaxConfig?) {
        val verticalScrollbarsEnabled = false
        val horizontalScrollbarsEnabled = false

        // callback when WebView is loading completed
        webViewLoaded = false
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (webViewLoaded) {
                    // WebView was already finished
                    // do not load content again
                    return
                }
                webViewLoaded = true
                if (!TextUtils.isEmpty(inputText)) {
                    setInputText(inputText)
                }
            }
        }
        mBridge = MathJaxJavaScriptBridge(this)
        addJavascriptInterface(mBridge!!, "Bridge")
        addJavascriptInterface(config ?: MathJaxConfig(), "BridgeConfig")

        // be careful, we do not need internet access
        settings.blockNetworkLoads = true
        settings.javaScriptEnabled = true
        loadUrl(HTML_LOCATION)
        isVerticalScrollBarEnabled = verticalScrollbarsEnabled
        isHorizontalScrollBarEnabled = horizontalScrollbarsEnabled
        setBackgroundColor(0)
    }

    /**
     * called when webView is ready with rendering LaTex
     */
    fun rendered() {
        onMathJaxRenderListener!!.onRendered()
    }

    /**
     * change the displayed LaTex
     *
     * @param inputText formatted string
     */
    fun setInputText(inputText: String?) {
        this.inputText = inputText

        //wait for WebView to finish loading
        if (!webViewLoaded) {
            return
        }
        val laTexString: String = inputText?.let { doubleEscapeTeX(it) } ?: ""
        val javascriptCommand = "changeLatexText(\"$laTexString\")"
        evaluateJavascript(javascriptCommand, null)
    }

    /**
     * @return the current laTex-String
     * null if not set
     */
    fun getInputText(): String? {
        return inputText
    }

    private fun doubleEscapeTeX(s: String): String {
        var t = ""
        for (i in s.indices) {
            if (s[i] == '\'') t += '\\'
            if (s[i] == '\\') t += "\\"
            if (s[i] != '\n') t += s[i]
        }
        return t
    }

    companion object {
        private const val HTML_LOCATION =
            "file:///android_asset/mathjax_android.html"
    }
}
