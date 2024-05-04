/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class RequestPermissionTool : ActivityResultLauncherDelegate<String, Boolean>() {
    override val key: String
        get() = "RequestPermissionTool"
    override val contract: ActivityResultContract<String, Boolean>
        get() = ActivityResultContracts.RequestPermission()
}

interface IRequestPermission {
    val requestPermissionTool: RequestPermissionTool
}