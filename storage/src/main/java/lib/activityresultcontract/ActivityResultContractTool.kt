/*
 *  Copyright (c) 2022~2024 chr_56
 */

package lib.activityresultcontract

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * register [ActivityResultContractTool] in onCreate using [register], then you can use everywhere
 */
abstract class ActivityResultContractTool<I, O> {

    abstract fun key(): String
    abstract fun contract(): ActivityResultContract<I, O>


    private var launcher: ActivityResultLauncher<I>? = null
    private var callback: ((O) -> Unit)? = null

    var busy: Boolean = false
        private set

    fun register(lifeCycle: Lifecycle, registry: ActivityResultRegistry) {
        lifeCycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                register(owner, registry)
            }
        })
    }

    private fun register(owner: LifecycleOwner, registry: ActivityResultRegistry) {
        launcher = registry.register(key(), owner, contract()) {
            val callback = this.callback
            if (callback != null) {
                callback.invoke(it)
            } else {
                throw IllegalStateException("callback of ActivityResult is null!")
            }
            this.callback = null
            busy = false
        }
    }

    @Synchronized
    fun launch(input: I, callback: (O) -> Unit) {
        val launcher = this.launcher
        if (launcher != null) {
            busy = true
            this.callback = callback
            launcher.launch(input)
        } else {
            throw IllegalStateException("ActivityResultLauncher is not correctly registered!")
        }
    }
}