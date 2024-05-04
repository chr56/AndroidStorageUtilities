/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * Delegate of [ActivityResultLauncher], 
 * which supports inplace callback.
 * 
 * **You need use [register] methods register this delegate first before use.**
 * 
 * Use [launch] to start with [ActivityResultCallback]
 * 
 */
abstract class ActivityResultLauncherDelegate<I, O> {

    abstract val key: String
    abstract val contract: ActivityResultContract<I, O>


    private var launcher: ActivityResultLauncher<I>? = null
    private var _callback: ActivityResultCallback<O>? = null

    var busy: Boolean = false
        private set

    fun register(activity: ComponentActivity) =
        register(activity.lifecycle, activity.activityResultRegistry)

    fun register(fragment: Fragment) {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                launcher = fragment.registerForActivityResult(contract, ::execute)
            }
        })
    }


    fun register(lifecycleOwner: LifecycleOwner, caller: ActivityResultCaller) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                register(caller)
            }
        })
    }

    fun register(caller: ActivityResultCaller) {
        caller.registerForActivityResult(contract, ::execute)
    }


    fun register(lifeCycle: Lifecycle, registry: ActivityResultRegistry) {
        lifeCycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                register(owner, registry)
            }
        })
    }

    internal fun register(owner: LifecycleOwner, registry: ActivityResultRegistry) {
        launcher = registry.register(key, owner, contract, ::execute)
    }

    @Synchronized
    fun launch(input: I, callback: ActivityResultCallback<O>) {
        val launcher = launcher
        if (launcher != null) {
            busy = true
            _callback = callback
            launcher.launch(input)
        } else {
            throw IllegalStateException("ActivityResultLauncher is not correctly registered!")
        }
    }

    @Synchronized
    private fun execute(result: O) {
        val callback = _callback
        if (callback != null) {
            callback.onActivityResult(result)
        } else {
            throw IllegalStateException("callback of ActivityResult is null!")
        }
        _callback = null
        busy = false
    }
}