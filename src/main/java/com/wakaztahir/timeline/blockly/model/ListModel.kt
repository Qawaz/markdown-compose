package com.wakaztahir.timeline.blockly.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.wakaztahir.timeline.blockly.serializers.BlockSerializer
import kotlinx.serialization.Serializable


class ListItem {
    var text by mutableStateOf("")

    var isChecked by mutableStateOf(false)

    var isIndented by mutableStateOf(false)

    //Transient Properties
    var topOffset by mutableStateOf(0.dp)
    var itemHeight by mutableStateOf(0.dp)
}


@Serializable(with = BlockSerializer::class)
class ListBlock : Block() {

    var items = mutableStateListOf<ListItem>()

    override fun exportText(): String {
        var text = ""
        items.forEach {
            text += it.text + "\n"
        }
        return text
    }

    override fun exportMarkdown(): String {
        var markdown = ""
        items.forEach {
            markdown += (if (it.isIndented) "    " else "") + " - " + (if (it.isChecked) "[x]" else "[ ]") + " " + it.text + "\n"
        }
        return markdown
    }

    override fun exportHTML(): String {
        if (items.isEmpty()) {
            return ""
        }
        var html = "<ul>"
        var isIndenting = false
        items.forEach {
            if (!it.isIndented) {
                if (isIndenting) {
                    html += "</ul>"
                    isIndenting = false
                }

            } else {
                if (!isIndenting) {
                    html += "<ul>"
                    isIndenting = true
                }
            }
            html += "<li>" + it.text + "</li>"
        }
        return "$html</ul>"
    }

}