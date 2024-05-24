/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import lib.storage.extension.isDocumentProviderUri
import lib.storage.extension.isDownloadsDocument
import lib.storage.extension.isMediaDocument
import lib.storage.extension.isRawFile
import lib.storage.internal.storageManager
import lib.storage.textparser.DocumentUriPathParser
import lib.storage.textparser.ExternalFilePathParser
import lib.storage.textparser.STORAGE_VOLUME_PRIMARY
import androidx.annotation.RequiresApi
import android.content.Context
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import java.io.File

/**
 * Resolve file path (like `/storage/<StorageVolume>/<Path>`) and return `<StorageVolume>`
 * @return StorageVolume id
 */
fun storageVolumeIdOf(context: Context, file: File): String? =
    if (SDK_INT >= VERSION_CODES.N) {
        val storageVolume = storageVolumeOf(context, file)
        val storageId = storageVolume?.let { storageVolumeIdOf(it) }
        storageId ?: ExternalFilePathParser.storageVolumeId(file.absolutePath)
    } else {
        ExternalFilePathParser.storageVolumeId(file.absolutePath)
    }

/**
 * Resolve content uri (like `content:/<AUTHORITY>/tree/<StorageVolume>:<Path>`) and return `<StorageVolume>`
 * @return StorageVolume id (null if Uri is incorrect!)
 */
fun storageVolumeIdOf(context: Context, uri: Uri): String? {
    if (SDK_INT > VERSION_CODES.Q && uri.isMediaDocument()) {
        val storageVolume = mediaUriStorageVolumeOf(context, uri)
        val storageId = storageVolume?.let { storageVolumeIdOf(it) }
        if (storageId != null) return storageId
    }
    return when {
        uri.isDocumentProviderUri() -> DocumentUriPathParser.storageVolumeId(uri.pathSegments)
        uri.isRawFile()             -> storageVolumeIdOf(context, File(uri.path.orEmpty()))
        uri.isDownloadsDocument()   -> STORAGE_VOLUME_PRIMARY
        else                        -> null
    }
}


@RequiresApi(VERSION_CODES.N)
fun storageVolumeOf(context: Context, file: File): StorageVolume? {
    val storageManager: StorageManager? = context.storageManager()
    return storageManager?.getStorageVolume(file)
}

/**
 * @receiver Uri must be Document Provider Content Uri
 */
@RequiresApi(VERSION_CODES.Q)
fun contentUriStorageVolumeOf(context: Context, contentUri: Uri): StorageVolume? {
    val storageManager: StorageManager? = context.storageManager()
    if (storageManager != null) {
        val id = DocumentUriPathParser.storageVolumeId(contentUri.pathSegments)
        return storageManager.storageVolumes.find { it.uuid == id }
    } else {
        return null
    }
}

/**
 * @receiver Uri must be MediaStore Uri
 */
@RequiresApi(VERSION_CODES.Q)
fun mediaUriStorageVolumeOf(context: Context, mediastoreUri: Uri): StorageVolume? {
    val storageManager: StorageManager? = context.storageManager()
    return storageManager?.getStorageVolume(mediastoreUri)
}

@RequiresApi(VERSION_CODES.N)
fun storageVolumeIdOf(storageVolume: StorageVolume): String? = when {
    storageVolume.isPrimary    -> STORAGE_VOLUME_PRIMARY
    storageVolume.uuid != null -> storageVolume.uuid!!
    else                       -> null
}

/**
 * @return root directory of StorageVolume, null if unavailable (for example, unmounted or unsupported)
 */
fun rootDirectoryOf(storageVolume: StorageVolume): File? =
    if (SDK_INT >= VERSION_CODES.R) {
        storageVolume.directory
    } else {
        try {
            storageVolume.javaClass.getMethod("getPathFile").invoke(storageVolume) as File
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

private const val TAG = "StorageVolume"