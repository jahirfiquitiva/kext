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
package jahirfiquitiva.libs.kext.extensions

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes

@Deprecated("Careful! Use activity {} instead", ReplaceWith("activity()"))
val androidx.fragment.app.Fragment.actv: androidx.fragment.app.FragmentActivity
    get() = activity!!

@Deprecated("Careful! Use context {} instead", ReplaceWith("context()"))
val androidx.fragment.app.Fragment.ctxt: Context
    get() = context!!

fun androidx.fragment.app.Fragment.activity(
    canThrowException: Boolean = false,
    todo: (androidx.fragment.app.FragmentActivity) -> Unit
                                           ) {
    activity?.let { todo(it) } ?: if (canThrowException) throw IllegalStateException(
        "Activity was null!")
}

fun androidx.fragment.app.Fragment.context(
    canThrowException: Boolean = false,
    todo: (Context) -> Unit
                                          ) {
    context?.let { todo(it) } ?: if (canThrowException) throw IllegalStateException(
        "Context was null!")
}

fun androidx.fragment.app.Fragment.activity(safeAccess: SafeAccess<androidx.fragment.app.FragmentActivity>) {
    activity?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

fun androidx.fragment.app.Fragment.context(safeAccess: SafeAccess<Context>) {
    context?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

interface SafeAccess<in T> {
    fun ifNotNull(obj: T) {}
    fun ifNull() {}
}

fun androidx.fragment.app.Fragment.string(@StringRes res: Int, fallback: String = ""): String =
    try {
        if (res != 0) getString(res) else fallback
    } catch (ignored: Exception) {
        fallback
    }

fun androidx.fragment.app.Fragment.stringArray(@ArrayRes arrayRes: Int): Array<String>? =
    try {
        resources.getStringArray(arrayRes)
    } catch (ignored: Exception) {
        null
    }

fun androidx.fragment.app.Fragment.boolean(@BoolRes res: Int, fallback: Boolean = false) =
    try {
        resources.getBoolean(res)
    } catch (ignored: Exception) {
        fallback
    }

fun androidx.fragment.app.Fragment.int(@IntegerRes res: Int, fallback: Int = 0): Int =
    try {
        resources.getInteger(res)
    } catch (ignored: Exception) {
        fallback
    }

fun androidx.fragment.app.Fragment.dimen(@DimenRes res: Int, fallback: Float = 0.0F): Float =
    try {
        resources.getDimension(res)
    } catch (ignored: Exception) {
        fallback
    }

fun androidx.fragment.app.Fragment.dimenPixelSize(@DimenRes res: Int, fallback: Int = 0): Int =
    try {
        resources.getDimensionPixelSize(res)
    } catch (ignored: Exception) {
        fallback
    }