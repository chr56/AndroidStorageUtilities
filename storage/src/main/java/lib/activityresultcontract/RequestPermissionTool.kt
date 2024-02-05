/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class RequestPermissionTool : ActivityResultContractTool<String, Boolean>() {
    override fun key(): String = "RequestPermissionTool"
    override fun contract(): ActivityResultContract<String, Boolean> =
        ActivityResultContracts.RequestPermission()
}

interface IRequestPermission {
    val requestPermissionTool: RequestPermissionTool
}