/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import androidx.documentfile.provider.DocumentFile
import android.content.Context

fun DocumentFile.getBasePath(context: Context): String? =
    uri.getBasePath(context)

fun DocumentFile.getAbsolutePath(context: Context): String? =
    uri.getAbsolutePath(context)