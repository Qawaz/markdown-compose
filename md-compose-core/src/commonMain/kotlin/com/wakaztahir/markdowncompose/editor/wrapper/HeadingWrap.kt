package com.wakaztahir.markdowncompose.editor.wrapper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@SerialName("heading")
//@Serializable
//class HeadingWrap(val item: Wrapper, val type: HeadingType) : Wrapper {
//
//    override fun toHTML(): String {
//        return type.wrapHTML(item.toHTML())
//    }
//
//    override fun toMarkdown(): String {
//        return type.wrapMarkdown(item.toMarkdown())
//    }
//
//    override fun toString(): String {
//        return toHTML()
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is HeadingWrap) return false
//
//        if (item != other.item) return false
//        if (type != other.type) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return item.hashCode()
//    }
//
//}
//
//sealed interface HeadingType {
//
//    fun wrapHTML(html: String): String
//    fun wrapMarkdown(markdown: String): String
//
//    data object H1 : HeadingType {
//        override fun wrapHTML(html: String): String {
//            return "<h1>$html</h1>"
//        }
//
//        override fun wrapMarkdown(markdown: String): String {
//            return "# $markdown"
//        }
//    }
//
//    data object H2 : HeadingType {
//        override fun wrapHTML(html: String): String {
//            return "<h2>$html</h2>"
//        }
//
//        override fun wrapMarkdown(markdown: String): String {
//            return "## $markdown"
//        }
//    }
//
//    data object H3 : HeadingType {
//        override fun wrapHTML(html: String): String {
//            return "<h3>$html</h3>"
//        }
//
//        override fun wrapMarkdown(markdown: String): String {
//            return "### $markdown"
//        }
//    }
//
//    data object H4 : HeadingType {
//        override fun wrapHTML(html: String): String {
//            return "<h4>$html</h4>"
//        }
//
//        override fun wrapMarkdown(markdown: String): String {
//            return "#### $markdown"
//        }
//    }
//
//    data object H5 : HeadingType {
//        override fun wrapHTML(html: String): String {
//            return "<h5>$html</h5>"
//        }
//
//        override fun wrapMarkdown(markdown: String): String {
//            return "##### $markdown"
//        }
//    }
//
//    data object H6 : HeadingType {
//        override fun wrapHTML(html: String): String {
//            return "<h6>$html</h6>"
//        }
//
//        override fun wrapMarkdown(markdown: String): String {
//            return "##### $markdown"
//        }
//    }
//
//
//}