/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import lib.storage.extension.isDocumentProviderUri
import lib.storage.extension.isDownloadsDocument
import lib.storage.extension.isRawFile
import lib.storage.textparser.ExternalFilePathParser
import lib.storage.textparser.ExternalFilePathParser.primaryExternalStoragePath
import android.content.Context
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.MediaStore
import android.util.Log


fun basePathOf(context: Context, uri: Uri): String? {
    return when {
        uri.isDocumentProviderUri() -> documentProviderUriBasePath(uri = uri, context)
        uri.isRawFile()             -> {
            val path = uri.path ?: return null
            try {
                ExternalFilePathParser.bashPath(path)
            } catch (e: IllegalArgumentException) {
                Log.e("Storage", "unsupported path: $path", e)
                null
            }
        }

        uri.isDownloadsDocument()   -> parseDownloadUriBasePath(context, uri = uri)
        else                        -> null
    }
}


fun absolutePathOf(context: Context, uri: Uri): String? {
    return when {
        uri.isDocumentProviderUri() -> documentProviderUriAbsolutePath(uri = uri, context)
        uri.isRawFile()             -> uri.path
        else                        -> null
    }
}

/**
 * @author Anggrayudi Hardiannico A.
 */
private fun parseDownloadUriBasePath(context: Context, uri: Uri): String? {
    val path = uri.path ?: return null
    // content://com.android.providers.downloads.documents/tree/raw:/storage/emulated/0/Download/Denai/document/raw:/storage/emulated/0/Download/Denai
    // content://com.android.providers.downloads.documents/tree/downloads/document/raw:/storage/emulated/0/Download/Denai
    return when {
        // API 26 - 27 => content://com.android.providers.downloads.documents/document/22
        SDK_INT < VERSION_CODES.P  -> {
            if (path.matches(Regex("/document/\\d+"))) {
                val fileName =
                    context.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                            if (columnIndex != -1) {
                                return@use cursor.getString(columnIndex)
                            }
                        }
                        return@use null
                    } ?: ""
                "${Environment.DIRECTORY_DOWNLOADS}/$fileName"
            } else null
        }

        SDK_INT >= VERSION_CODES.Q -> {
            Log.e("Storage", "unsupported path: $path")
            null
        }

        else                       ->
            path.substringAfterLast(primaryExternalStoragePath, "").trim('/')
    }
}