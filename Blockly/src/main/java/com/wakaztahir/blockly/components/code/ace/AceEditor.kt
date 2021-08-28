package com.wakaztahir.blockly.components.code.ace

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

@SuppressLint("ViewConstructor")
class AceEditor(
    context: Context,
    val minLines: Int,
    val maxLines: Int,
) : WebView(context) {
    var c: Context = context
    private var loadedUI: Boolean
    internal var onLoaded: () -> Unit = {}
    internal var onChange: () -> Unit = {}

    init {
        loadedUI = false
        initialize()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialize() {
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (!loadedUI) {
                    loadedUI = true
                    onLoaded()
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }
        settings.javaScriptEnabled = true
        loadUrl("file:///android_asset/index.html")
        addJavascriptInterface(
            EditorInterface(this, minLines = minLines, maxLines = maxLines),
            "Android"
        )
    }

    enum class Theme {
        AMBIANCE, CHAOS, CHROME, CLOUDS, CLOUDS_MIDNIGHT, COBALT, CRIMSON_EDITOR, DAWN, DRACULA, DREAMWEAVER, ECLIPSE, GITHUB, GOB, GRUVBOX, IDLE_FINGERS, IPLASTIC, KATZENMILCH, KR_THEME, KUROIR, MERBIVORE, MERBIVORE_SOFT, MONO_INDUSTRIAL, MONOKAI, PASTEL_ON_DARK, SOLARIZED_DARK, SOLARIZED_LIGHT, SQLSERVER, TERMINAL, TEXTMATE, TOMORROW, TOMORROW_NIGHT, TOMORROW_NIGHT_BLUE, TOMORROW_NIGHT_BRIGHT, TOMORROW_NIGHT_EIGHTIES, TWILIGHT, VIBRANT_INK, XCODE
    }

    enum class Mode {
        ABAP, ABC, ActionScript, ADA, Apache_Conf, AsciiDoc, Assembly_x86, AutoHotKey, BatchFile, C9Search, C_Cpp, Cirru, Clojure, Cobol, coffee, ColdFusion, CSharp, CSS, Curly, D, Dart, Diff, Dockerfile, Dot, Dummy, DummySyntax, Eiffel, EJS, Elixir, Elm, Erlang, Forth, FTL, Gcode, Gherkin, Gitignore, Glsl, golang, Groovy, HAML, Handlebars, Haskell, haXe, HTML, HTML_Ruby, INI, Io, Jack, Jade, Java, JavaScript, JSON, JSONiq, JSP, JSX, Julia, Kotlin, LaTeX, LESS, Liquid, Lisp, LiveScript, LogiQL, LSL, Lua, LuaPage, Lucene, Makefile, Markdown, Mask, MATLAB, MEL, MUSHCode, MySQL, Nix, ObjectiveC, OCaml, Pascal, Perl, pgSQL, PHP, Powershell, Praat, Prolog, Properties, Protobuf, Python, R, RDoc, RHTML, Ruby, Rust, SASS, SCAD, Scala, Scheme, SCSS, SH, SJS, Smarty, snippets, Soy_Template, Space, SQL, Stylus, SVG, Tcl, Tex, Text, Textile, Toml, Twig, Typescript, Vala, VBScript, Velocity, Verilog, VHDL, XML, XQuery, YAML
    }
}

private class EditorInterface(
    val editor: AceEditor,
    @get:JavascriptInterface val minLines: Int,
    @get:JavascriptInterface val maxLines: Int,
) {
    @JavascriptInterface
    fun onChange() {
        editor.onChange()
    }
}



