/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import lib.storage.internal.storageManager
import androidx.annotation.RequiresApi
import android.content.Context
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import java.io.File

/**
 * Primary Storage (internal storage)
 */
const val STORAGE_VOLUME_PRIMARY = "primary"

/**
 * Resolve file path (like `/storage/<StorageVolume>/<Path>`) and return `<StorageVolume>`
 * @return StorageVolume id
 */
fun File.getStorageId(context: Context): String? =
    if (SDK_INT >= VERSION_CODES.N) {
        val storageVolume = this.storageVolume(context)
        val storageId = storageVolume?.storageId()
        storageId ?: parseStorageVolumeId(absolutePath)
    } else {
        parseStorageVolumeId(absolutePath)
    }

private fun parseStorageVolumeId(absolutePath: String): String? =
    if (absolutePath.startsWith(Environment.getExternalStorageDirectory().absolutePath)) STORAGE_VOLUME_PRIMARY
    else {
        absolutePath
            .substringAfter("/storage/", "")
            .substringBefore('/')
            .takeIf { it.isNotEmpty() }
    }


/**
 * Resolve content uri (like `content:/<AUTHORITY>/tree/<StorageVolume>:<Path>`) and return `<StorageVolume>`
 * @return StorageVolume id (null if Uri is incorrect!)
 */
fun Uri.getStorageId(context: Context): String? {
    if (SDK_INT > VERSION_CODES.Q && this.isMediaDocument()) {
        val storageVolume = this.mediaUriStorageVolume(context)
        val storageId = storageVolume?.storageId()
        if (storageId != null) return storageId
    }
    return when {
        isDocumentProviderUri() -> parseStorageVolumeId(uri = this)
        isRawFile()             -> File(path.orEmpty()).getStorageId(context)
        isDownloadsDocument()   -> STORAGE_VOLUME_PRIMARY
        else                    -> null
    }
}

@RequiresApi(VERSION_CODES.N)
fun File.storageVolume(context: Context): StorageVolume? {
    val storageManager: StorageManager? = context.storageManager()
    return storageManager?.getStorageVolume(this)
}

/**
 * @receiver Uri must be Document Provider Content Uri
 */
@RequiresApi(VERSION_CODES.Q)
private fun Uri.contentUriStorageVolume(context: Context): StorageVolume? {
    val storageManager: StorageManager? = context.storageManager()
    if (storageManager != null) {
        val id = parseStorageVolumeId(this)
        return storageManager.storageVolumes.find { it.uuid == id }
    } else {
        return null
    }
}

/**
 * @receiver Uri must be MediaStore Uri
 */
@RequiresApi(VERSION_CODES.Q)
private fun Uri.mediaUriStorageVolume(context: Context): StorageVolume? {
    val storageManager: StorageManager? = context.storageManager()
    return storageManager?.getStorageVolume(this)
}

@RequiresApi(VERSION_CODES.N)
fun StorageVolume.storageId(): String? = when {
    this.isPrimary    -> STORAGE_VOLUME_PRIMARY
    this.uuid != null -> uuid!!
    else              -> null
}

/**
 * @return root directory of StorageVolume, null if unavailable (for example, unmounted or unsupported)
 */
fun StorageVolume.rootDirectory(): File? =
    if (SDK_INT >= VERSION_CODES.R) {
        this.directory
    } else {
        try {
            this.javaClass.getMethod("getPathFile").invoke(this) as File
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

private const val TAG = "StorageVolume"