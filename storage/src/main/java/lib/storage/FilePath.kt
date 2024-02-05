/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import android.os.Environment
import java.io.File

/**
 * relative file path from _the root of a storage volume_
 */
fun File.getBasePath(): String = externalFileBashPath(absolutePath)

/**
 * relative file path from _the root of a storage volume_
 */
fun externalFileBashPath(absolutePath: String): String {
    val primaryStoragePath = externalStoragePath
    return if (absolutePath.startsWith(primaryStoragePath)) {
        absolutePath.substringAfter(primaryStoragePath, "").trim('/')
    } else {
        if (!absolutePath.startsWith("/storage")) {
            if (absolutePath.startsWith("/mnt")) {
                absolutePath.substringAfter("/mnt/", "").substringAfter('/')
            } else {
                throw IllegalArgumentException("Unsupported Path: $absolutePath")
            }
        } else {
            absolutePath.substringAfter("/storage/", "").substringAfter('/')
        }
    }
}


val externalStoragePath: String get() = Environment.getExternalStorageDirectory().absolutePath