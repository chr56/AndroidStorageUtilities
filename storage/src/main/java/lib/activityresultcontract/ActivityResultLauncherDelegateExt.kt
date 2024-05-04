/*
 *  Copyright (c) 2023~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment

fun ComponentActivity.registerActivityResultLauncherDelegate(vararg list: ActivityResultLauncherDelegate<*, *>) {
    for (contractTool in list) {
        contractTool.register(this)
    }
}

fun Fragment.registerActivityResultLauncherDelegate(vararg list: ActivityResultLauncherDelegate<*, *>) {
    for (contractTool in list) {
        contractTool.register(this)
    }
}