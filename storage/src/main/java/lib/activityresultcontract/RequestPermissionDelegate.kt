/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class RequestPermissionDelegate : ActivityResultLauncherDelegate<String, Boolean>() {
    override val key: String = "RequestPermission"
    override val contract: ActivityResultContract<String, Boolean> = ActivityResultContracts.RequestPermission()
}

interface IRequestPermission {
    val requestPermissionDelegate: RequestPermissionDelegate
}