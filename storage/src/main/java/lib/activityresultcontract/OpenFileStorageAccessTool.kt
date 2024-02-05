/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract

class OpenFileStorageAccessTool : ActivityResultContractTool<OpenDocumentContract.Config, Uri?>() {
    override fun key(): String = "OpenFile"
    override fun contract(): ActivityResultContract<OpenDocumentContract.Config, Uri?> =
        OpenDocumentContract()
}

interface IOpenFileStorageAccess {
    val openFileStorageAccessTool: OpenFileStorageAccessTool
}

@TargetApi(19)
class OpenDocumentContract : ActivityResultContract<OpenDocumentContract.Config, Uri?>() {
    override fun createIntent(context: Context, input: Config): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, input.mimeTypes)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, input.allowMultiSelect)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && input.initialUri != null) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, input.initialUri)
                addCategory(Intent.CATEGORY_OPENABLE)
            }
        }
    }

    override fun getSynchronousResult(context: Context, input: Config): SynchronousResult<Uri?>? = null
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }

    class Config(
        val mimeTypes: Array<String>,
        val initialUri: Uri? = null,
        val allowMultiSelect: Boolean = false,
    )
}