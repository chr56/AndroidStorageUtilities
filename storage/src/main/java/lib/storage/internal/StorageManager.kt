/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.storage.internal

import androidx.core.content.ContextCompat
import android.content.Context
import android.os.storage.StorageManager
import android.util.Log

internal fun Context.storageManager(): StorageManager? {
    val storageManager: StorageManager? = ContextCompat.getSystemService(this, StorageManager::class.java)
    if (storageManager == null) Log.w(TAG, "No StorageManager available!")
    return storageManager
}
private const val TAG = "StorageVolumeUtil"