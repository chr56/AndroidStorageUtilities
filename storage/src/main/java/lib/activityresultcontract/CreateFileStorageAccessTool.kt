/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri

class CreateFileStorageAccessTool(val mimeType: String = "*/*") : ActivityResultLauncherDelegate<String, Uri?>() {
    override val key: String
        get() = "CreateFile"
    override val contract: ActivityResultContract<String, Uri?>
        get() = ActivityResultContracts.CreateDocument(mimeType)
}

interface ICreateFileStorageAccess {
    val createFileStorageAccessTool: CreateFileStorageAccessTool
}