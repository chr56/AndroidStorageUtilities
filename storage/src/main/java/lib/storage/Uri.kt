/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import lib.storage.extension.EXTERNAL_STORAGE_AUTHORITY
import lib.storage.extension.isDocumentProviderUri
import lib.storage.extension.isDownloadsDocument
import lib.storage.extension.isRawFile
import lib.storage.textparser.DocumentUriPathParser.childDocumentUriAbsolutePath
import lib.storage.textparser.DocumentUriPathParser.childDocumentUriBasePath
import lib.storage.textparser.DocumentUriPathParser.documentTreeUriAbsolutePath
import lib.storage.textparser.DocumentUriPathParser.documentTreeUriBasePath
import lib.storage.textparser.DocumentUriPathParser.documentUriAbsolutePath
import lib.storage.textparser.DocumentUriPathParser.documentUriBasePath
import lib.storage.textparser.ExternalFilePathParser
import androidx.core.provider.DocumentsContractCompat
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File

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
        Build.VERSION.SDK_INT < Build.VERSION_CODES.P  -> {
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

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            Log.e("Storage", "unsupported path: $path")
            null
        }

        else                                           ->
            path.substringAfterLast(ExternalFilePathParser.primaryExternalStoragePath, "").trim('/')
    }
}


/**
 * @param uri system DocumentProviderUri (`content://com.android.externalstorage.documents/...`)
 * @return base path (relative file path from _the root of a storage volume_)
 */
fun documentProviderUriBasePath(uri: Uri, context: Context): String? {
    if (uri.authority != EXTERNAL_STORAGE_AUTHORITY) {
        Log.w("Storage", "Non-Android DocumentProvider: $uri")
    }
    return when {
        DocumentsContractCompat.isTreeUri(uri)              -> documentTreeUriBasePath(uri.pathSegments)
        DocumentsContractCompat.isDocumentUri(context, uri) -> documentUriBasePath(uri.pathSegments)
        else                                                -> childDocumentUriBasePath(uri.pathSegments) // may be a ChildDocumentUri
    }
}


/**
 * @param uri system DocumentProviderUri (`content://com.android.externalstorage.documents/...`)
 * @return absolute path
 */
fun documentProviderUriAbsolutePath(uri: Uri, context: Context): String? {
    if (uri.authority != EXTERNAL_STORAGE_AUTHORITY) {
        Log.w("Storage", "Non-Android DocumentProvider: $uri")
    }
    return when {
        DocumentsContractCompat.isDocumentUri(context, uri) -> documentUriAbsolutePath(uri.pathSegments)
        DocumentsContractCompat.isTreeUri(uri)              -> documentTreeUriAbsolutePath(uri.pathSegments)
        else                                                -> childDocumentUriAbsolutePath(uri.pathSegments) // may be a ChildDocumentUri
    }
}

/**
 * Get document uri id of file [filePath]
 * @param filePath absolute POSIX paths of target file
 * @return document uri id (<storage-id>:<base-path>), null if broken [filePath]
 */
fun documentUriId(context: Context, filePath: String): String? {
    val file = File(filePath)
    val storageId = storageVolumeIdOf(context, file)
    val basePath = ExternalFilePathParser.bashPath(file.absolutePath) ?: return null
    return "$storageId:$basePath"
}

/**
 * Use [treeUri] to build document content uri of file [filePath]
 * @param treeUri Document Tree Uri (`content://<EXTERNAL_STORAGE_AUTHORITY>/tree/...`)
 * @param filePath absolute POSIX paths of target file
 * @return document content uri (`content://<EXTERNAL_STORAGE_AUTHORITY>/tree/.../child/...`)
 */
fun childDocumentUriWithinTree(context: Context, treeUri: Uri, filePath: String): Uri? {
    val documentId = documentUriId(context, filePath) ?: return null
    return DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, documentId)
}