/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.storage.extension

import lib.storage.absolutePathOf
import lib.storage.basePathOf
import lib.storage.contentUriStorageVolumeOf
import lib.storage.mediaUriStorageVolumeOf
import lib.storage.rootDirectoryOf
import lib.storage.storageVolumeIdOf
import lib.storage.storageVolumeOf
import lib.storage.textparser.ExternalFilePathParser
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import android.content.Context
import android.net.Uri
import android.os.Build.VERSION_CODES
import android.os.storage.StorageVolume
import java.io.File


fun File.getBasePath(): String =
    ExternalFilePathParser.bashPath(absolutePath) ?: throw IllegalArgumentException("Unsupported Path: $absolutePath")

fun Uri.getBasePath(context: Context): String? = lib.storage.basePathOf(context, this)

fun Uri.getAbsolutePath(context: Context): String? = absolutePathOf(context, this)

fun DocumentFile.getBasePath(context: Context): String? = basePathOf(context, uri)

fun DocumentFile.getAbsolutePath(context: Context): String? = absolutePathOf(context, uri)


@RequiresApi(VERSION_CODES.N)
fun File.storageVolume(context: Context): StorageVolume? = storageVolumeOf(context, this)


/**
 * Resolve file path (like `/storage/<StorageVolume>/<Path>`) and return `<StorageVolume>`
 * @return StorageVolume id
 */
fun File.getStorageVolumeId(context: Context): String? = storageVolumeIdOf(context, absoluteFile)


/**
 * Resolve content uri (like `content:/<AUTHORITY>/tree/<StorageVolume>:<Path>`) and return `<StorageVolume>`
 * @return StorageVolume id (null if Uri is incorrect!)
 */
fun Uri.getStorageVolumeId(context: Context): String? = storageVolumeIdOf(context, this)


/**
 * @receiver Uri must be Document Provider Content Uri
 */
@RequiresApi(VERSION_CODES.Q)
fun Uri.contentUriStorageVolume(context: Context): StorageVolume? = contentUriStorageVolumeOf(context, this)

/**
 * @receiver Uri must be MediaStore Uri
 */
@RequiresApi(VERSION_CODES.Q)
fun Uri.mediaUriStorageVolume(context: Context): StorageVolume? = mediaUriStorageVolumeOf(context, this)


@RequiresApi(VERSION_CODES.N)
fun StorageVolume.storageId(): String? = storageVolumeIdOf(this)

/**
 * @return root directory of StorageVolume, null if unavailable (for example, unmounted or unsupported)
 */
fun StorageVolume.rootDirectory(): File? = rootDirectoryOf(this)
