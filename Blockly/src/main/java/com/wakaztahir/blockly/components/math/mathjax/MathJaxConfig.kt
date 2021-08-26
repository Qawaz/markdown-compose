package com.wakaztahir.blockly.components.math.mathjax

import android.webkit.JavascriptInterface

class MathJaxConfig {
    enum class Output(var value: String) {
        SVG("output/SVG"), HTML_CSS("output/HTML-CSS"), CommonHTML("output/CommonHTML"), NativeMML("output/NativeMML");
    }

    enum class Input(var value: String) {
        TeX("input/TeX"), MathML("input/MathML"), AsciiMath("input/AsciiMath");
    }

    @get:JavascriptInterface
    var input = Input.TeX.value
        private set

    @get:JavascriptInterface
    var output = Output.SVG.value
        private set

    @get:JavascriptInterface
    var outputScale = 100

    @get:JavascriptInterface
    var minScaleAdjust = 100

    @get:JavascriptInterface
    var automaticLinebreaks = false

    @get:JavascriptInterface
    var blacker = 1


    fun setInput(input: Input) {
        this.input = input.value
    }

    fun setOutput(output: Output) {
        this.output = output.value
    }

    init {
        output = Output.SVG.value
    }
}
