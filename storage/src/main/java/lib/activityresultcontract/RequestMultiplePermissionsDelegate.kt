/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class RequestMultiplePermissionsDelegate : ActivityResultLauncherDelegate<Array<String>, Map<String, Boolean>>() {
    override val key: String = "RequestMultiplePermissions"
    override val contract: ActivityResultContract<Array<String>, Map<String, Boolean>> =
        ActivityResultContracts.RequestMultiplePermissions()
}

interface IRequestMultiplePermission {
    val requestMultiplePermissionsDelegate: RequestMultiplePermissionsDelegate
}