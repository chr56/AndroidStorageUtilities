/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import lib.storage.textparser.DocumentUriPathParser.childDocumentUriAbsolutePath
import lib.storage.textparser.DocumentUriPathParser.childDocumentUriBasePath
import lib.storage.textparser.DocumentUriPathParser.documentTreeUriAbsolutePath
import lib.storage.textparser.DocumentUriPathParser.documentTreeUriBasePath
import lib.storage.textparser.DocumentUriPathParser.documentUriAbsolutePath
import lib.storage.textparser.DocumentUriPathParser.documentUriBasePath
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import java.io.File

/**
 * @param uri system DocumentProviderUri (`content://com.android.externalstorage.documents/...`)
 * @return base path (relative file path from _the root of a storage volume_)
 */
fun documentProviderUriBasePath(uri: Uri, context: Context): String? {
    if (uri.authority != EXTERNAL_STORAGE_AUTHORITY) {
        Log.w("Storage", "Non-Android DocumentProvider: $uri")
    }
    return when {
        DocumentsContract.isTreeUri(uri)              -> documentTreeUriBasePath(uri.pathSegments)
        DocumentsContract.isDocumentUri(context, uri) -> documentUriBasePath(uri.pathSegments)
        else                                          -> childDocumentUriBasePath(uri.pathSegments) // may be a ChildDocumentUri
    }
}

/**
 * @param uri system DocumentProviderUri (`content://com.android.externalstorage.documents/...`)
 * @return base path (relative file path from _the root of a storage volume_)
 */
fun documentProviderUriBasePathForce(uri: Uri): String? {
    return if (uri.authority == EXTERNAL_STORAGE_AUTHORITY) childDocumentUriBasePath(uri.pathSegments) else null
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
        DocumentsContract.isDocumentUri(context, uri) -> documentUriAbsolutePath(uri.pathSegments)
        DocumentsContract.isTreeUri(uri)              -> documentTreeUriAbsolutePath(uri.pathSegments)
        else                                          -> childDocumentUriAbsolutePath(uri.pathSegments) // may be a ChildDocumentUri
    }
}

/**
 * @param uri system DocumentProviderUri (`content://com.android.externalstorage.documents/...`)
 * @return absolute (relative file path from _the root of a storage volume_)
 */
fun documentProviderUriAbsolutePathForce(uri: Uri): String? {
    return if (uri.authority == EXTERNAL_STORAGE_AUTHORITY) childDocumentUriAbsolutePath(uri.pathSegments) else null
}

/**
 * Get document uri id of file [filePath]
 * @param filePath absolute POSIX paths of target file
 * @return document uri id (<storage-id>:<base-path>)
 */
fun documentUriId(context: Context, filePath: String): String {
    val file = File(filePath)
    val storageId = file.getStorageId(context)
    val basePath = file.getBasePath()
    return "$storageId:$basePath"
}

/**
 * Use [treeUri] to build document content uri of file [filePath]
 * @param treeUri Document Tree Uri (`content://<EXTERNAL_STORAGE_AUTHORITY>/tree/...`)
 * @param filePath absolute POSIX paths of target file
 * @return document content uri (`content://<EXTERNAL_STORAGE_AUTHORITY>/tree/.../child/...`)
 */
fun childDocumentUriWithinTree(context: Context, treeUri: Uri, filePath: String): Uri {
    val documentId = documentUriId(context, filePath)
    return DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
}