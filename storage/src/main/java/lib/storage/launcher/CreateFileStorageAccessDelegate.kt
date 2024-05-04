/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.storage.launcher

import lib.activityresultcontract.ActivityResultLauncherDelegate
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri

class CreateFileStorageAccessDelegate(val mimeType: String = "*/*") : ActivityResultLauncherDelegate<String, Uri?>() {
    override val key: String
        get() = "SAF/CreateFile"
    override val contract: ActivityResultContract<String, Uri?>
        get() = ActivityResultContracts.CreateDocument(mimeType)
}

interface ICreateFileStorageAccessible {
    val createFileStorageAccessDelegate: CreateFileStorageAccessDelegate
}