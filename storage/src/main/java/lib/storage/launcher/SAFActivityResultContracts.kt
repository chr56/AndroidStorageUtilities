/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.storage.launcher

import lib.storage.guessDocumentUri
import android.content.Context
import android.net.Uri
import android.util.Log
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File

/**
 * Util for [lib.activityresultcontract.ActivityResultLauncherDelegate]
 */
@Suppress("unused")
object SAFActivityResultContracts {

    /**
     * choose a file from Storage Access Framework
     *
     * __[context] must be [IOpenFileStorageAccessible]__
     *
     * @param path initial location from guessing
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun chooseFileViaSAF(
        context: Context,
        path: String,
        mimeTypes: Array<String> = arrayOf("*/*"),
    ): Uri {
        require(context is IOpenFileStorageAccessible)
        return suspendCancellableCoroutine {
            val initialUri = guessDocumentUri(context, File(path))
            context.openFileStorageAccessDelegate.launch(OpenDocumentContract.Config(mimeTypes, initialUri)) { uri ->
                if (uri != null) {
                    it.resume(uri, this::canceled)
                } else {
                    Log.i(TAG, "No file selected via SAF!")
                }
            }
        }
    }


    /**
     * choose a directory from Storage Access Framework
     *
     * __[context] must be [IOpenDirStorageAccessible]__
     *
     * @param path initial location from guessing
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun chooseDirViaSAF(
        context: Context,
        path: String,
    ): Uri {
        require(context is IOpenDirStorageAccessible)
        return suspendCancellableCoroutine {
            val initialUri = guessDocumentUri(context, File(path))
            context.openDirStorageAccessDelegate.launch(initialUri) { uri ->
                if (uri != null) {
                    it.resume(uri, this::canceled)
                } else {
                    Log.i(TAG, "No directory selected via SAF!")
                }
            }
        }
    }

    /**
     * create a file from Storage Access Framework
     *
     * __[context] must be [ICreateFileStorageAccessible]__
     *
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun createFileViaSAF(
        context: Context,
        fileName: String,
    ): Uri {
        require(context is ICreateFileStorageAccessible)
        return suspendCancellableCoroutine {
            context.createFileStorageAccessDelegate.launch(fileName) { uri ->
                if (uri != null) {
                    it.resume(uri, this::canceled)
                } else {
                    Log.i(TAG, "No file created via SAF!")
                }
            }
        }
    }

    private fun canceled(e: Throwable, value: Uri, context: CoroutineContext) {
        Log.i(TAG, "Canceled!")
        Log.v(TAG, "${e.message}\n${e.stackTraceToString()}")
    }

    @Suppress("SpellCheckingInspection")
    private const val TAG = "SAFARC"
}