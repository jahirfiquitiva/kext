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
package jahirfiquitiva.libs.kauextensions.extensions

import android.content.Context
import android.support.annotation.ArrayRes
import android.support.annotation.BoolRes
import android.support.annotation.DimenRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

@Deprecated("Use one of safeActv(...) methods", ReplaceWith("safeActv()"))
val Fragment.actv: FragmentActivity
    get() = activity ?: throw IllegalStateException("Activity was null!")

@Deprecated("Use one of safeCtxt(...) methods", ReplaceWith("safeCtxt()"))
val Fragment.ctxt: Context
    get() = context ?: throw IllegalStateException("Context was null!")

fun Fragment.actv(canThrowException: Boolean = false, todo: (FragmentActivity) -> Unit) {
    activity?.let { todo(it) } ?: if (canThrowException) throw IllegalStateException(
            "Activity was null!")
}

fun Fragment.ctxt(canThrowException: Boolean = false, todo: (Context) -> Unit) {
    context?.let { todo(it) } ?: if (canThrowException) throw IllegalStateException(
            "Context was null!")
}

fun Fragment.withActv(canThrowException: Boolean = false, todo: FragmentActivity.() -> Unit) {
    activity?.todo() ?: if (canThrowException) throw IllegalStateException("Activity was null!")
}

fun Fragment.withCtxt(canThrowException: Boolean = false, todo: Context.() -> Unit) {
    context?.todo() ?: if (canThrowException) throw IllegalStateException("Context was null!")
}

fun Fragment.actv(safeAccess: SafeAccess<FragmentActivity>) {
    activity?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

fun Fragment.ctxt(safeAccess: SafeAccess<Context>) {
    context?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

interface SafeAccess<in T> {
    fun ifNotNull(obj: T) {}
    fun ifNull() {}
}

fun Fragment.string(@StringRes stringRes: Int, fallback: String): String =
        if (stringRes > 0) getString(stringRes) else fallback

fun Fragment.stringArray(@ArrayRes arrayRes: Int): Array<String> =
        resources.getStringArray(arrayRes)

fun Fragment.boolean(@BoolRes bool: Int) = resources.getBoolean(bool)

fun Fragment.integer(@IntegerRes id: Int): Int = resources.getInteger(id)

fun Fragment.dimen(@DimenRes id: Int): Float = resources.getDimension(id)

fun Fragment.dimenPixelSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)