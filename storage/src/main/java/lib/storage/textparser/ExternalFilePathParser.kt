/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.storage.textparser

import android.os.Environment

object ExternalFilePathParser {

    /**
     * Resolve StorageVolume id from file path (like `/storage/<StorageVolume>/<BasePath>`)
     * @return StorageVolume id (null if broken input)
     */
    @JvmStatic
    fun storageVolumeId(absolutePath: String): String? {
        return if (absolutePath.startsWith(primaryExternalStoragePath)) STORAGE_VOLUME_PRIMARY
        else {
            absolutePath
                .substringAfter("/storage/", "")
                .substringBefore('/')
                .takeIf { it.isNotEmpty() }
        }
    }


    /**
     * Resolve relative path from file path (like `/storage/<StorageVolume>/<BasePath>`)
     * @return BasePath (relative file path from _the root of a storage volume_)
     */
    @JvmStatic
    fun bashPath(absolutePath: String): String? {
        return if (absolutePath.startsWith(primaryExternalStoragePath)) {
            absolutePath.substringAfter(primaryExternalStoragePath, "").trim('/')
        } else {
            if (absolutePath.startsWith("/storage")) {
                absolutePath
                    .substringAfter("/storage/", "")
                    .substringAfter('/')
            } else {
                if (absolutePath.startsWith("/mnt")) {
                    absolutePath
                        .substringAfter("/mnt/", "")
                        .substringAfter('/')
                } else {
                    null
                }
            }
        }
    }


    internal val primaryExternalStoragePath: String = Environment.getExternalStorageDirectory().absolutePath
}