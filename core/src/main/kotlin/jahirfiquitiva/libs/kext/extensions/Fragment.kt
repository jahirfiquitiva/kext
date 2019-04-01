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
package jahirfiquitiva.libs.kext.extensions

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

@Deprecated("Careful! Use activity {} instead", ReplaceWith("activity()"))
val Fragment.actv: FragmentActivity
    get() = activity!!

@Deprecated("Careful! Use context {} instead", ReplaceWith("context()"))
val Fragment.ctxt: Context
    get() = context!!

fun Fragment.activity(
    canThrowException: Boolean = false,
    todo: (FragmentActivity) -> Unit
                     ) {
    activity?.let { todo(it) } ?: if (canThrowException)
        throw IllegalStateException("Activity was null!")
}

fun Fragment.context(
    canThrowException: Boolean = false,
    todo: (Context) -> Unit
                    ) {
    context?.let { todo(it) } ?: if (canThrowException)
        throw IllegalStateException("Context was null!")
}

fun Fragment.activity(safeAccess: SafeAccess<FragmentActivity>) {
    activity?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

fun Fragment.context(safeAccess: SafeAccess<Context>) {
    context?.let { safeAccess.ifNotNull(it) } ?: safeAccess.ifNull()
}

interface SafeAccess<in T> {
    fun ifNotNull(obj: T) {}
    fun ifNull() {}
}

fun Fragment.string(@StringRes res: Int, fallback: String = ""): String =
    try {
        if (res != 0) getString(res) else fallback
    } catch (ignored: Exception) {
        fallback
    }

fun Fragment.stringArray(@ArrayRes arrayRes: Int): Array<String>? =
    try {
        resources.getStringArray(arrayRes)
    } catch (ignored: Exception) {
        null
    }

fun Fragment.boolean(@BoolRes res: Int, fallback: Boolean = false) =
    try {
        resources.getBoolean(res)
    } catch (ignored: Exception) {
        fallback
    }

fun Fragment.int(@IntegerRes res: Int, fallback: Int = 0): Int =
    try {
        resources.getInteger(res)
    } catch (ignored: Exception) {
        fallback
    }

fun Fragment.dimen(@DimenRes res: Int, fallback: Float = 0.0F): Float =
    try {
        resources.getDimension(res)
    } catch (ignored: Exception) {
        fallback
    }

fun Fragment.dimenPixelSize(@DimenRes res: Int, fallback: Int = 0): Int =
    try {
        resources.getDimensionPixelSize(res)
    } catch (ignored: Exception) {
        fallback
    }
