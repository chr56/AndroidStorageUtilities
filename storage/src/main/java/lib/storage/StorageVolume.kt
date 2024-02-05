/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import androidx.annotation.RequiresApi
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import java.io.File

/**
 * Primary Storage (internal storage)
 */
const val PRIMARY = "primary"

/**
 * @return StorageVolume id
 * (`/storage/<StorageVolume>/<Path>>`)
 */
@SuppressLint("ObsoleteSdkInt")
fun File.getStorageId(context: Context): String {
    return if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
        val storageVolume = this.storageVolume(context)
        val storageId = storageVolume?.storageId()
        return storageId ?: storageVolumeId(absolutePath)
    } else { // below android 7
        storageVolumeId(absolutePath)
    }
}

/**
 * @return StorageVolume id
 * (`/tree/<StorageVolume>:<Path>`)
 */
fun Uri.getStorageId(context: Context): String {
    val path = path.orEmpty()
    if (Build.VERSION.SDK_INT > VERSION_CODES.Q && this.isMediaDocument()) {
        val storageVolume = this.storageVolume(context)
        val storageId = storageVolume.storageId()
        if (storageId != null) return storageId
    }

    return when {
        isRawFile()             -> File(path).getStorageId(context)
        isDocumentProviderUri() -> {
            parseStorageVolumeId(uri = this) ?: throw IllegalArgumentException("Unknown Storage Volume (uri:$this) ")
        }
        isDownloadsDocument()   -> PRIMARY
        else                    -> throw IllegalArgumentException("Unknown Storage Volume (uri:$this) ")
    }
}

@RequiresApi(VERSION_CODES.N)
fun File.storageVolume(context: Context): StorageVolume? {
    val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    return storageManager.getStorageVolume(this)
}

/**
 * @receiver Uri should be MediaStore Uri
 */
@RequiresApi(VERSION_CODES.Q)
private fun Uri.storageVolume(context: Context): StorageVolume {
    val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    return storageManager.getStorageVolume(this)
}

private fun storageVolumeId(absolutePath: String): String =
    if (absolutePath.startsWith(Environment.getExternalStorageDirectory().absolutePath)) PRIMARY
    else {
        val id = absolutePath.substringAfter("/storage/", "").substringBefore('/')
        id.ifEmpty {
            throw IllegalArgumentException("Unknown Storage Volume (file:$absolutePath) ")
        }
    }


@RequiresApi(VERSION_CODES.N)
fun StorageVolume.storageId(): String? = when {
    this.isPrimary    -> PRIMARY
    this.uuid != null -> uuid!!
    else              -> null
}

/**
 * @return null if unavailable (for example, unmounted or unsupported)
 */
fun StorageVolume.root(): File? =
    if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
        this.directory
    } else {
        try {
            // this.javaClass.getField("mPath")
            this.javaClass.getMethod("getPathFile").invoke(this) as File
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
