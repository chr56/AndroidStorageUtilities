/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.storage.launcher

import lib.activityresultcontract.ActivityResultLauncherDelegate
import androidx.activity.result.contract.ActivityResultContract
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract

class OpenDirStorageAccessDelegate : ActivityResultLauncherDelegate<Uri?, Uri?>() {
    override val key: String
        get() = "SAF/OpenDir"
    override val contract: ActivityResultContract<Uri?, Uri?>
        get() = GrandDirContract()
}

interface IOpenDirStorageAccessible {
    val openDirStorageAccessDelegate: OpenDirStorageAccessDelegate
}

@TargetApi(21)
class GrandDirContract(val intentFlags: Int = DEFAULT_FLAGS) : ActivityResultContract<Uri?, Uri?>() {
    override fun createIntent(context: Context, input: Uri?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = intentFlags
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && input != null) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, input)
            }
        }
    }

    override fun getSynchronousResult(context: Context, input: Uri?): SynchronousResult<Uri?>? = null
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data

    companion object {
        const val FLAGS_W = Intent.FLAG_GRANT_READ_URI_PERMISSION
        const val FLAGS_WR = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        const val FLAGS_WR_PERSIST = FLAGS_WR or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION


        private const val DEFAULT_FLAGS = FLAGS_WR_PERSIST
    }
}