/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.storage

import androidx.annotation.RequiresApi
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore


const val EXTERNAL_STORAGE_AUTHORITY = "com.android.externalstorage.documents"

const val DOWNLOADS_FOLDER_AUTHORITY = "com.android.providers.downloads.documents"

const val MEDIA_FOLDER_AUTHORITY = "com.android.providers.media.documents"

fun Uri.isDocumentProviderUri(): Boolean =
    authority == EXTERNAL_STORAGE_AUTHORITY

fun Uri.isDocumentProviderUriSafe(context: Context): Boolean =
    DocumentsContract.isDocumentUri(context, this)

fun Uri.isTreeDocumentFile(): Boolean =
    isDocumentProviderUri() && path?.startsWith("/tree/") == true

@RequiresApi(Build.VERSION_CODES.N)
fun Uri.isTreeDocumentFileSafe(): Boolean =
    DocumentsContract.isTreeUri(this)

fun Uri.isDownloadsDocument(): Boolean = authority == DOWNLOADS_FOLDER_AUTHORITY

fun Uri.isMediaDocument(): Boolean = authority == MEDIA_FOLDER_AUTHORITY

fun Uri.isRawFile(): Boolean = scheme == ContentResolver.SCHEME_FILE

fun Uri.isMediaFile(): Boolean = authority == MediaStore.AUTHORITY