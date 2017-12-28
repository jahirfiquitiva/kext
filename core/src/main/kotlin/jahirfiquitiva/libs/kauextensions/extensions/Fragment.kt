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
package jahirfiquitiva.libs.kauextensions.extensions

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

@Deprecated("Use one of safeActv(...) methods", ReplaceWith("safeActv()"))
val Fragment.actv: FragmentActivity
    get() = activity ?: throw IllegalStateException("Activity was null! D:")

@Deprecated("Use one of safeCtxt(...) methods", ReplaceWith("safeCtxt()"))
val Fragment.ctxt: Context
    get() = context ?: throw IllegalStateException("Context was null! D:")

fun Fragment.safeActv(canThrowException: Boolean = false, todo: (FragmentActivity) -> Unit) {
    activity?.let { todo(it) } ?:
            if (canThrowException) throw IllegalStateException("Activity was null! D:")
}

fun Fragment.safeCtxt(canThrowException: Boolean = false, todo: (Context) -> Unit) {
    context?.let { todo(it) } ?:
            if (canThrowException) throw IllegalStateException("Context was null! D:")
}

fun Fragment.safeActv(safeAccess: SafeAccess<FragmentActivity>) {
    activity?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

fun Fragment.safeCtxt(safeAccess: SafeAccess<Context>) {
    context?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

interface SafeAccess<in T> {
    fun ifNotNull(obj: T) {}
    fun ifNull() {}
}