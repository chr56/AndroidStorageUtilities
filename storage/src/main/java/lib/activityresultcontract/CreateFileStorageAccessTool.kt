/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri

class CreateFileStorageAccessTool(val mimeType: String = "*/*") : ActivityResultContractTool<String, Uri?>() {
    override fun key(): String = "CreateFile"
    override fun contract(): ActivityResultContract<String, Uri?> =
        ActivityResultContracts.CreateDocument(mimeType)
}

interface ICreateFileStorageAccess {
    val createFileStorageAccessTool: CreateFileStorageAccessTool
}