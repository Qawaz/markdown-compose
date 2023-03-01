package com.wakaztahir.markdowncompose.core

/**
 * Implementation for [ReferenceLinkHandler] to resolve referenced link within the Markdown
 */
class ReferenceLinkHandlerImpl : ReferenceLinkHandler {
    private val stored = mutableMapOf<String, String?>()
    override fun store(label: String, destination: String?) {
        stored[label] = destination
    }

    override fun find(label: String): String {
        return stored[label] ?: label
    }
}