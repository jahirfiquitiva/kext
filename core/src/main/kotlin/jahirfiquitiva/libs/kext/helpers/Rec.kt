/*
 * Copyright (c) 2019. Jahir Fiquitiva
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
package jahirfiquitiva.libs.kext.helpers

import android.util.Log

open class Rec(private val tag: String = "kext", private val canLog: Boolean = true) {
    
    fun v(message: String?, throwable: Throwable? = null) {
        log(Log.VERBOSE, message, throwable)
    }
    
    fun i(message: String?, throwable: Throwable? = null) {
        log(Log.INFO, message, throwable)
    }
    
    fun d(message: String?, throwable: Throwable? = null) {
        log(Log.DEBUG, message, throwable)
    }
    
    fun w(message: String?, throwable: Throwable? = null) {
        log(Log.WARN, message, throwable)
    }
    
    fun e(message: String?, throwable: Throwable? = null) {
        log(Log.ERROR, message, throwable)
    }
    
    fun wtf(message: String?, throwable: Throwable? = null) {
        log(Log.ASSERT, message, throwable)
    }
    
    fun print(priority: Int, message: String?, throwable: Throwable? = null) {
        log(priority, message, throwable)
    }
    
    private fun log(priority: Int, message: String?, throwable: Throwable? = null) {
        val actMessage = message ?: "Null Message"
        if (canLog) {
            when {
                throwable != null -> Log.e(tag, actMessage, throwable)
                priority !in (Log.VERBOSE..Log.ERROR) -> Log.wtf(tag, actMessage)
                else -> Log.println(priority, tag, actMessage)
            }
        }
    }
}
