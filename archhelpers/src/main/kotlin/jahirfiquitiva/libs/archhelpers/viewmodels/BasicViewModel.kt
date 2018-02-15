/*
 * Copyright (c) 2018. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.archhelpers.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import jahirfiquitiva.libs.archhelpers.tasks.Async
import java.lang.ref.WeakReference

abstract class BasicViewModel<in Parameter, Result>(param: Parameter) : ViewModel() {
    
    fun getData(): Result? = data.value
    
    private var taskStarted = false
    private val data = MutableLiveData<Result>()
    private var task: Async<Parameter, Result>? = null
    
    private var customObserver: Observer<Result>? = null
    
    init {
        loadData(param, true)
    }
    
    fun loadData(parameter: Parameter, forceLoad: Boolean = false) {
        if (!taskStarted || forceLoad) {
            cancelTask(true)
            task = Async<Parameter, Result>(
                    WeakReference(parameter),
                    object : Async.Callback<Parameter, Result>() {
                        override fun doLoad(param: Parameter): Result? =
                                safeInternalLoad(param, forceLoad)
                        
                        override fun onSuccess(result: Result) = postResult(result)
                    })
            task?.execute()
            taskStarted = true
        }
    }
    
    private fun cancelTask(interrupt: Boolean = false) {
        task?.cancel(interrupt)
        task = null
        taskStarted = false
    }
    
    fun destroy(owner: LifecycleOwner, interrupt: Boolean = true) {
        cancelTask(interrupt)
        data.removeObservers(owner)
        customObserver = null
    }
    
    private fun safeInternalLoad(param: Parameter, forceLoad: Boolean = false): Result? {
        return if (forceLoad) internalLoad(param)
        else {
            if (isOldDataValid()) data.value
            else internalLoad(param)
        }
    }
    
    open fun postResult(result: Result) {
        data.value = null
        data.postValue(result)
        customObserver?.onChanged(result)
        taskStarted = false
    }
    
    fun observe(owner: LifecycleOwner, onUpdated: (Result) -> Unit) {
        destroy(owner, true)
        data.observe(owner, Observer<Result> { r -> r?.let { onUpdated(it) } })
    }
    
    fun extraObserve(onUpdated: (Result) -> Unit) {
        customObserver = Observer { r -> r?.let { onUpdated(it) } }
    }
    
    protected abstract fun internalLoad(param: Parameter): Result
    protected abstract fun isOldDataValid(): Boolean
}
