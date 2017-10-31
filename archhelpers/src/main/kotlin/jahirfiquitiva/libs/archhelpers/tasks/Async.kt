/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
package jahirfiquitiva.libs.archhelpers.tasks

import org.jetbrains.anko.doAsync
import java.lang.ref.WeakReference
import java.util.concurrent.Future

open class Async<Parameter, Result>(
        private val param: WeakReference<Parameter>,
        private val callback: Callback<Parameter, Result>
                                   ) {
    
    private var task: Future<*>? = null
    
    fun execute() {
        if (task != null) cancel(true)
        task = doAsync {
            val realParam = param.get()
            realParam?.let {
                val result = callback.doLoad(it)
                result?.let {
                    callback.onSuccess(it)
                } ?: callback.onError(NullPointerException("Result was null!"))
            } ?: callback.onError(NullPointerException("Parameter was null!"))
        }
    }
    
    fun cancel(interrupt: Boolean = false) {
        task?.cancel(interrupt)
        task = null
    }
    
    abstract class Callback<in Parameter, Result> {
        open fun doBefore() {}
        abstract fun doLoad(param: Parameter): Result?
        abstract fun onSuccess(result: Result)
        open fun onError(e: Exception?) = e?.printStackTrace()
    }
}