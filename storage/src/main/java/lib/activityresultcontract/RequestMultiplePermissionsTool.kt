/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class RequestMultiplePermissionsTool : ActivityResultContractTool<Array<String>, Map<String, Boolean>>() {
    override fun key(): String = "RequestMultiplePermissionsTool"
    override fun contract(): ActivityResultContract<Array<String>, Map<String, Boolean>> =
        ActivityResultContracts.RequestMultiplePermissions()
}
interface IRequestMultiplePermission {
    val requestMultiplePermissionsTool: RequestMultiplePermissionsTool
}