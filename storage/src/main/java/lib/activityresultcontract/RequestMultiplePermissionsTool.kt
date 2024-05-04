/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class RequestMultiplePermissionsTool : ActivityResultLauncherDelegate<Array<String>, Map<String, Boolean>>() {
    override val key: String
        get() = "RequestMultiplePermissionsTool"
    override val contract: ActivityResultContract<Array<String>, Map<String, Boolean>>
        get() = ActivityResultContracts.RequestMultiplePermissions()
}
interface IRequestMultiplePermission {
    val requestMultiplePermissionsTool: RequestMultiplePermissionsTool
}