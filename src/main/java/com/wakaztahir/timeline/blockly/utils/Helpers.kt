package com.wakaztahir.timeline.blockly.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.util.*

object Helpers {
    fun getMimeType(context: Context, uri: Uri): String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            cr.getType(uri)
        } else {
            return getMimeType(uri.toString())
        }
    }

    fun getMimeType(url: String): String? {
        return try {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(url).lowercase(Locale.ROOT)
            )
        } catch (ex: Exception) {
            null
        }
    }
}