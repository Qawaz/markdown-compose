package com.wakaztahir.blockly.components.blocks.textblock.markdowntext

import android.content.Context
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.QuoteSpan
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.text.getSpans
import com.wakaztahir.blockly.components.blocks.textblock.markdowntext.model.EnhancedMovementMethod
import com.wakaztahir.blockly.states.TextState
import io.noties.markwon.*
import io.noties.markwon.core.spans.BulletListItemSpan
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListDrawable
import io.noties.markwon.ext.tasklist.TaskListItem
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.ext.tasklist.TaskListSpan
import org.commonmark.node.SoftLineBreak

class MarkdownEditText(
    context: Context,
    val textState: TextState,
    var taskBoxColor: Int,
    var taskBoxBackgroundColor: Int
) : AppCompatEditText(context, null) {

    private var stateListener: TextStateListener = object : TextStateListener {
        override fun onUpdate(state: TextState) {

        }
    }
    private var markwon: Markwon
    private var textWatcher: TextWatcher? = null
    private var isSelectionStyling = false

    private var bulletSpanStart = 0
    private var numberedSpanStart = 0
    private var taskSpanStart = 0

    private val textWatchers: MutableList<TextWatcher> = emptyList<TextWatcher>().toMutableList()
    var copyPasteListener: CopyPasteListener? = null


    private fun markwonBuilder(context: Context): Markwon {
        movementMethod = EnhancedMovementMethod().getsInstance()
        return Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(taskBoxColor, taskBoxColor, taskBoxBackgroundColor))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ) { visitor, _ -> visitor.forceNewLine() }
                }

                override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                    val origin = builder.getFactory(TaskListItem::class.java)

                    builder.setFactory(
                        TaskListItem::class.java
                    ) { configuration, props ->
                        val span = origin?.getSpans(configuration, props)

                        if (span !is TaskListSpan) {
                            null
                        } else {
                            val taskClick = object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    span.isDone = !span.isDone
                                    text?.setSpan(
                                        span,
                                        text?.getSpanStart(span)!!,
                                        text?.getSpanEnd(span)!!,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }

                                override fun updateDrawState(ds: TextPaint) {
                                }
                            }
                            arrayOf(span, taskClick)
                        }
                    }


                }
            })
            .build()
    }

    fun triggerStyle(textStyle: TextStyle, stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            when (textStyle) {
                TextStyle.UNORDERED_LIST -> triggerUnOrderedListStyle(stop)
                TextStyle.ORDERED_LIST -> triggerOrderedListStyle(stop)
                TextStyle.TASKS_LIST -> triggerTasksListStyle(stop)
                else -> {
                    if (isSelectionStyling) {
                        styliseText(textStyle, selectionStart, selectionEnd)
                        isSelectionStyling = false
                    } else {
                        textWatcher = object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {}

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {

                                if (before < count) {
                                    styliseText(textStyle, start)
                                }
                            }

                        }
                        addTextWatcher(textWatcher!!)
                    }
                }
            }


        }


    }

    fun triggerUnOrderedListStyle(stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            val currentLineStart = layout.getLineStart(getCurrentCursorLine())
            if (text!!.length < currentLineStart + 1 || text!!.getGivenSpansAt(
                    span = arrayOf(
                        TextStyle.UNORDERED_LIST
                    ), currentLineStart, currentLineStart + 1
                ).isEmpty()
            ) {
                if (text!!.isNotEmpty()) {
                    if (text!!.length > 1 && text!!.getGivenSpansAt(
                            span = arrayOf(
                                TextStyle.ORDERED_LIST,
                                TextStyle.TASKS_LIST,
                            ), selectionStart - 2, selectionStart
                        ).isEmpty()
                    ) {
                        if (text.toString().substring(text!!.length - 2, text!!.length) != "\n") {
                            text!!.insert(selectionStart, "\n ")
                        } else {
                            text!!.insert(selectionStart, " ")
                        }
                    } else {
                        text!!.insert(selectionStart, "\n ")
                    }

                } else {
                    text!!.insert(selectionStart, " ")
                }

                bulletSpanStart = selectionStart - 1
                text!!.setSpan(
                    BulletListItemSpan(markwon.configuration().theme(), 0),
                    bulletSpanStart,
                    selectionStart,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            addTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                var lineCount = getLineCount()
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before < count) {
                        // If there's a new line
                        if (selectionStart == selectionEnd && lineCount < getLineCount()) {
                            lineCount = getLineCount()
                            val string = text.toString()
                            // If user hit enter
                            if (string[selectionStart - 1] == '\n') {
                                bulletSpanStart = selectionStart
                                text!!.insert(selectionStart, " ")
                                text!!.setSpan(
                                    BulletListItemSpan(markwon.configuration().theme(), 0),
                                    bulletSpanStart,
                                    bulletSpanStart + 1,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            } else {
                                for (bulletSpan in text?.getGivenSpansAt(
                                    span = arrayOf(TextStyle.UNORDERED_LIST),
                                    bulletSpanStart,
                                    bulletSpanStart + 1
                                )!!) {
                                    text?.removeSpan(bulletSpan)
                                    text?.setSpan(
                                        BulletListItemSpan(markwon.configuration().theme(), 0),
                                        bulletSpanStart,
                                        selectionStart,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        }

                    }

                }

            })

        }


    }

    fun triggerOrderedListStyle(stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            var currentNum = 1
            val currentLineStart = layout.getLineStart(getCurrentCursorLine())
            if (text!!.length < currentLineStart + 1 || text!!.getGivenSpansAt(
                    span = arrayOf(
                        TextStyle.ORDERED_LIST
                    ), currentLineStart, currentLineStart + 1
                ).isEmpty()
            ) {
                if (text!!.isNotEmpty()) {
                    if (text!!.length > 1 && text!!.getGivenSpansAt(
                            span = arrayOf(
                                TextStyle.UNORDERED_LIST,
                                TextStyle.TASKS_LIST,
                            ), selectionStart - 2, selectionStart
                        ).isEmpty()
                    ) {
                        if (text.toString().substring(text!!.length - 2, text!!.length) != "\n") {
                            text!!.insert(selectionStart, "\n ")
                        } else {
                            text!!.insert(selectionStart, " ")
                        }
                    } else {
                        text!!.insert(selectionStart, "\n ")
                    }

                } else {
                    text!!.insert(selectionStart, " ")
                }

                numberedSpanStart = selectionStart - 1
                text!!.setSpan(
                    OrderedListItemSpan(markwon.configuration().theme(), "${currentNum}-"),
                    numberedSpanStart,
                    selectionStart,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            currentNum++

            addTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                var lineCount = getLineCount()
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before < count) {
                        if (selectionStart == selectionEnd && lineCount < getLineCount()) {
                            lineCount = getLineCount()
                            val string = text.toString()
                            // If user hit enter
                            if (string[selectionStart - 1] == '\n') {
                                numberedSpanStart = selectionStart
                                text!!.insert(selectionStart, " ")
                                text!!.setSpan(
                                    OrderedListItemSpan(
                                        markwon.configuration().theme(),
                                        "${currentNum}-"
                                    ),
                                    numberedSpanStart,
                                    numberedSpanStart + 1,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                currentNum++
                            } else {
                                for (numberedSpan in text?.getGivenSpansAt(
                                    span = arrayOf(TextStyle.ORDERED_LIST),
                                    numberedSpanStart,
                                    numberedSpanStart + 1
                                )!!) {
                                    val orderedSpan = numberedSpan as OrderedListItemSpan
                                    text?.removeSpan(numberedSpan)
                                    text?.setSpan(
                                        OrderedListItemSpan(
                                            markwon.configuration().theme(),
                                            orderedSpan.number
                                        ),
                                        numberedSpanStart,
                                        selectionStart,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        }

                    }
                }

            })
        }
    }

    fun triggerTasksListStyle(stop: Boolean) {

        if (stop) {
            clearTextWatchers()
        } else {
            val currentLineStart = layout.getLineStart(getCurrentCursorLine())
            if (text!!.length < currentLineStart + 1 || text!!.getGivenSpansAt(
                    span = arrayOf(
                        TextStyle.TASKS_LIST
                    ), currentLineStart, currentLineStart + 1
                ).isEmpty()
            ) {
                if (text!!.isNotEmpty()) {
                    if (text!!.length > 1 && text!!.getGivenSpansAt(
                            span = arrayOf(
                                TextStyle.ORDERED_LIST,
                                TextStyle.UNORDERED_LIST,
                            ), selectionStart - 2, selectionStart
                        ).isEmpty()
                    ) {
                        if (text.toString().substring(text!!.length - 2, text!!.length) != "\n") {
                            text!!.insert(selectionStart, "\n ")
                        } else {
                            text!!.insert(selectionStart, " ")
                        }
                    } else {
                        text!!.insert(selectionStart, "\n ")
                    }

                } else {
                    text!!.insert(selectionStart, " ")
                }
                taskSpanStart = selectionStart - 1
                setTaskSpan(
                    taskSpanStart,
                    selectionStart, false
                )
            }


            addTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                var lineCount = getLineCount()
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before < count) {
                        // If there's a new line
                        if (selectionStart == selectionEnd && lineCount < getLineCount()) {
                            lineCount = getLineCount()
                            val string = text.toString()
                            // If user hit enter
                            if (string[selectionStart - 1] == '\n') {
                                taskSpanStart = selectionStart
                                text!!.insert(selectionStart, " ")
                                setTaskSpan(
                                    taskSpanStart,
                                    taskSpanStart + 1, false
                                )
                            } else {
                                for (span in text?.getGivenSpansAt(
                                    span = arrayOf(TextStyle.TASKS_LIST),
                                    taskSpanStart,
                                    taskSpanStart + 1
                                )!!) {
                                    val taskSpan = span as TaskListSpan
                                    text?.removeSpan(span)
                                    setTaskSpan(
                                        taskSpanStart,
                                        selectionStart, taskSpan.isDone
                                    )

                                }
                            }
                        }

                    }

                }

            })

        }
    }

    fun addLinkSpan(title: String?, link: String) {
        val title1 = if (title.isNullOrEmpty()) link else title
        if (selectionStart == selectionEnd) {
            val cursorStart = selectionStart
            text!!.insert(cursorStart, title1)
            text!!.setSpan(
                LinkSpan(markwon.configuration().theme(), link, LinkResolverDef()),
                cursorStart,
                cursorStart + title1.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun setTaskSpan(start: Int, end: Int, isDone: Boolean) {
        val taskSpan = TaskListSpan(
            markwon.configuration().theme(),
            TaskListDrawable(taskBoxColor, taskBoxColor, taskBoxBackgroundColor),
            isDone
        )
        text!!.setSpan(
            taskSpan,
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        text?.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val spanStart = text?.getSpanStart(taskSpan)
                val spanEnd = text?.getSpanEnd(taskSpan)
                taskSpan.isDone = !taskSpan.isDone
                if (spanStart != null && spanEnd != null && spanStart >= 0) {
                    text!!.setSpan(
                        taskSpan,
                        spanStart,
                        spanEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

            }

            override fun updateDrawState(ds: TextPaint) {

            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    private fun styliseText(
        textStyle: TextStyle,
        start: Int
    ) {
        when (textStyle) {
            TextStyle.BOLD -> {
                text!!.setSpan(
                    StrongEmphasisSpan(),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.ITALIC -> {
                text!!.setSpan(
                    EmphasisSpan(),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.STRIKE -> {
                text!!.setSpan(
                    StrikethroughSpan(),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            }
            else -> {
            }
        }


    }

    private fun styliseText(
        textStyle: TextStyle,
        start: Int,
        end: Int
    ) {
        when (textStyle) {
            TextStyle.BOLD -> {
                text!!.setSpan(
                    StrongEmphasisSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.ITALIC -> {
                text!!.setSpan(
                    EmphasisSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            TextStyle.STRIKE -> {
                text!!.setSpan(
                    StrikethroughSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            }
            else -> {
            }
        }


    }

    fun getMarkdown(): String {
        clearTextWatchers()
        var mdText = text
        val startList = emptyList<Int>().toMutableList()
        val endList = emptyList<Int>().toMutableList()
        var i = 0
        val appliedListSpans = mutableListOf<Int>()

        filterSpans()
        for ((index, span) in text!!.getGivenSpans(
            span = TextStyle.values()
        ).withIndex()) {
            val start = text!!.getSpanStart(span)
            val end = text!!.getSpanEnd(span)
            startList.add(index, start)
            endList.add(index, end)
        }

        for ((index, start) in startList.sorted().withIndex()) {
            val end = endList.sorted()[index]
            val spannedText = end.let { text!!.substring(start, it) }
            val span = end.let {
                text!!.getGivenSpansAt(
                    span = TextStyle.values(), start, it
                )
            }

            for (selectedSpan in span) {


                if (selectedSpan is BulletListItemSpan) {
                    if (!appliedListSpans.contains(start)) {
                        val mdString = "* $spannedText"
                        mdText = SpannableStringBuilder(
                            mdText!!.replaceRange(
                                start + i,
                                end + i,
                                mdString
                            )
                        )
                        i += 2
                        appliedListSpans.add(start)
                    }

                } else if (selectedSpan is TaskListSpan) {
                    if (!appliedListSpans.contains(start)) {

                        val mdString =
                            if (selectedSpan.isDone) "* [x] $spannedText" else "* [ ] $spannedText"
                        mdText = SpannableStringBuilder(
                            mdText!!.replaceRange(
                                start + i,
                                end + i,
                                mdString
                            )
                        )
                        i += 6
                        appliedListSpans.add(start)
                    }
                } else {
                    if (spannedText.length > 1) {
                        when (selectedSpan) {
                            is StrongEmphasisSpan -> {
                                val mdString = "**$spannedText**"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 4
                            }
                            is EmphasisSpan -> {
                                val mdString = "_${spannedText}_"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 2
                            }
                            is StrikethroughSpan -> {
                                val mdString = "~~$spannedText~~"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 4
                            }
                            is OrderedListItemSpan -> {
                                val mdString = "${selectedSpan.number}$spannedText"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 2
                            }
                            is LinkSpan -> {
                                val mdString = "[$spannedText](${selectedSpan.link})"
                                mdText = SpannableStringBuilder(
                                    mdText!!.replaceRange(
                                        start + i,
                                        end + i,
                                        mdString
                                    )
                                )
                                i += 4 + (selectedSpan.link.length - spannedText.length)
                            }

                        }
                    }

                }

            }

        }
        return mdText.toString()
    }

    private fun filterSpans() {
        val spans = text?.getGivenSpans(
            span = arrayOf(
                TextStyle.BOLD,
                TextStyle.ITALIC,
                TextStyle.STRIKE,
                TextStyle.LINK
            )
        )

        if (spans != null) {
            for (span in spans) {
                val selectedSpans = text?.getGivenSpansAt(
                    span = arrayOf(span),
                    text?.getSpanStart(span)!!,
                    text?.getSpanEnd(span)!!
                )
                if (selectedSpans!!.size > 1) {
                    var smallSpanIndex = 0
                    var spanSize: Int? = null
                    for ((index, selectedSpan) in selectedSpans.withIndex()) {
                        if (text?.getSpanStart(selectedSpan) != null) {
                            val spanStart = text?.getSpanStart(selectedSpan)
                            val spanEnd = text?.getSpanEnd(selectedSpan)!!
                            if (spanSize == null) {
                                spanSize = spanEnd - spanStart!!
                                smallSpanIndex = index
                            } else {
                                if (spanEnd - spanStart!! < spanSize) {
                                    spanSize = spanEnd - spanStart
                                    smallSpanIndex = index
                                }
                            }
                        }
                    }
                    text?.removeSpan(selectedSpans[smallSpanIndex])
                }
            }
        }

        val listsSpans = text?.getGivenSpans(
            span = arrayOf(
                TextStyle.UNORDERED_LIST,
                TextStyle.TASKS_LIST
            )
        )

        if (!listsSpans.isNullOrEmpty()) {
            for (span in listsSpans) {
                val spanStart = text?.getSpanStart(span)
                val spanEnd = text?.getSpanEnd(span)

                if (spanEnd!! - spanStart!! > 1) {
                    text?.removeSpan(span)
                    text?.setSpan(
                        span,
                        spanStart,
                        spanStart + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }


    private fun Editable.getGivenSpans(vararg span: TextStyle): MutableList<Any> {
        val spanList = emptyArray<Any>().toMutableList()
        for (selectedSpan in span) {
            when (selectedSpan) {
                TextStyle.BOLD -> {
                    this.getSpans<StrongEmphasisSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ITALIC -> {
                    this.getSpans<EmphasisSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.STRIKE -> {
                    this.getSpans<StrikethroughSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.QUOTE -> {
                    this.getSpans<QuoteSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.UNORDERED_LIST -> {
                    this.getSpans<BulletListItemSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ORDERED_LIST -> {
                    this.getSpans<OrderedListItemSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.TASKS_LIST -> {
                    this.getSpans<TaskListSpan>().forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.LINK -> {
                    this.getSpans<LinkSpan>().forEach {
                        spanList.add(it)
                    }
                }
            }
        }
        return spanList
    }

    private fun Editable.getGivenSpansAt(
        vararg span: Any,
        start: Int,
        end: Int
    ): MutableList<Any> {
        val spanList = emptyArray<Any>().toMutableList()
        for (selectedSpan in span) {
            this.getSpans(start, end, selectedSpan::class.java).forEach {
                spanList.add(it)
            }
        }
        return spanList
    }

    private fun Editable.getGivenSpansAt(
        vararg span: TextStyle,
        start: Int,
        end: Int
    ): MutableList<Any> {
        val spanList = emptyArray<Any>().toMutableList()
        for (selectedSpan in span) {
            when (selectedSpan) {
                TextStyle.BOLD -> {
                    this.getSpans<StrongEmphasisSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ITALIC -> {
                    this.getSpans<EmphasisSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.STRIKE -> {
                    this.getSpans<StrikethroughSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.QUOTE -> {
                    this.getSpans<QuoteSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.UNORDERED_LIST -> {
                    this.getSpans<BulletListItemSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.ORDERED_LIST -> {
                    this.getSpans<OrderedListItemSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.TASKS_LIST -> {
                    this.getSpans<TaskListSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
                TextStyle.LINK -> {
                    this.getSpans<LinkSpan>(start, end).forEach {
                        spanList.add(it)
                    }
                }
            }
        }
        return spanList
    }

    private var selectedState: TextStyle? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart == selEnd && selStart > 0) {
            val currentLineStart = layout.getLineStart(getCurrentCursorLine())
            val listsSpans = text!!.getGivenSpansAt(
                span = arrayOf(
                    TextStyle.UNORDERED_LIST,
                    TextStyle.TASKS_LIST
                ),
                start = currentLineStart, end = currentLineStart + 1
            )
            if (listsSpans.size > 0) {
                when (listsSpans[0]) {
                    is BulletListItemSpan -> {
                        stateListener.onUpdate(textState.apply { isUnorderedList = true })
                        selectedState = TextStyle.UNORDERED_LIST
                    }
                    is OrderedListItemSpan -> {
                        stateListener.onUpdate(textState.apply { isOrderedList = true })
                        selectedState = TextStyle.ORDERED_LIST
                    }
                    is TaskListSpan -> {
                        stateListener.onUpdate(textState.apply { isTaskList = true })
                        selectedState = TextStyle.TASKS_LIST
                    }
                }
            } else {
                val selectedSpans = text!!.getGivenSpansAt(
                    span = arrayOf(
                        TextStyle.BOLD,
                        TextStyle.ITALIC,
                        TextStyle.STRIKE
                    ),
                    start = selStart - 1, end = selStart
                )
                if (selectedSpans.size > 0) {
                    for (span in selectedSpans.distinctBy { it.javaClass }) {
                        when (span) {
                            is StrongEmphasisSpan -> {
                                stateListener.onUpdate(textState.apply { isBold = true })
                                selectedState = TextStyle.BOLD
                            }
                            is EmphasisSpan -> {
                                stateListener.onUpdate(textState.apply { isItalic = true })
                                selectedState = TextStyle.ITALIC
                            }
                            is StrikethroughSpan -> {
                                stateListener.onUpdate(textState.apply { isStrikeThrough = true })
                                selectedState = TextStyle.STRIKE
                            }
                        }
                    }
                } else {
                    when (selectedState) {
                        TextStyle.BOLD -> {
                            stateListener.onUpdate(textState.apply { isBold = false })
                        }
                        TextStyle.ITALIC -> {
                            stateListener.onUpdate(textState.apply { isItalic = false })
                        }
                        TextStyle.STRIKE -> {
                            stateListener.onUpdate(textState.apply { isStrikeThrough = false })
                        }
                        TextStyle.UNORDERED_LIST -> {
                            stateListener.onUpdate(textState.apply { isUnorderedList = false })
                        }
                        TextStyle.ORDERED_LIST -> {
                            stateListener.onUpdate(textState.apply { isOrderedList = false })
                        }
                        TextStyle.TASKS_LIST -> {
                            stateListener.onUpdate(textState.apply { isTaskList = false })
                        }
                        else -> {
                        }
                    }
                }
            }

        } else if (selStart != selEnd) {
            isSelectionStyling = true
        }
    }

    private fun addTextWatcher(textWatcher: TextWatcher) {
        textWatchers.add(textWatcher)
        addTextChangedListener(textWatcher)
    }

    private fun clearTextWatchers() {
        for (textWatcher in textWatchers) {
            removeTextChangedListener(textWatcher)
        }
    }

    private fun getCurrentCursorLine(): Int {
        return if (selectionStart != -1) layout.getLineForOffset(selectionStart) else -1
    }

    private fun getLineCharPosition(line: Int): Int {
        var chars = 1
        return if (line == 0) {
            0
        } else {
            for (i in 0 until line) {
                chars += text!!.lines()[i].length
            }
            chars
        }
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        when (id) {
            android.R.id.cut -> onCut()
            android.R.id.copy -> onCopy()
            android.R.id.paste -> onPaste()

        }
        return super.onTextContextMenuItem(id)
    }

    fun onCut() {
        copyPasteListener?.onCut()
    }

    fun onCopy() {
        copyPasteListener?.onCopy()
    }

    fun onPaste() {
        copyPasteListener?.onPaste()
    }

    //Renders given md string
    fun setMarkdown(md: String) {
        this.text = SpannableStringBuilder(markwon.toMarkdown(md))
    }

    interface CopyPasteListener {
        fun onCut()
        fun onCopy()
        fun onPaste()
    }

    fun setOnTextStateChangeListener(l: TextStateListener) {
        stateListener = l
    }

    interface TextStateListener {
        fun onUpdate(state: TextState)
    }

    init {
        markwon = markwonBuilder(context)
    }
}
