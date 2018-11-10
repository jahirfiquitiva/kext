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
import android.view.View
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.Adapter<*>.isEmpty(): Boolean = itemCount <= 0

inline fun <reified T : View> RecyclerView.ViewHolder.bind(@IdRes res: Int): Lazy<T?> =
    lazy { itemView.findViewById<T>(res) }

val RecyclerView.ViewHolder.context: Context
    get() = itemView.context

fun RecyclerView.ViewHolder.string(@StringRes stringRes: Int, fallback: String = ""): String =
    context.string(stringRes, fallback)

fun RecyclerView.ViewHolder.stringArray(@ArrayRes arrayRes: Int): Array<String>? =
    context.stringArray(arrayRes)

fun RecyclerView.ViewHolder.boolean(@BoolRes bool: Int): Boolean = context.boolean(bool)

fun RecyclerView.ViewHolder.int(@IntegerRes id: Int): Int = context.int(id)

fun RecyclerView.ViewHolder.dimen(@DimenRes id: Int): Float = context.dimen(id)

fun RecyclerView.ViewHolder.dimenPixelSize(@DimenRes id: Int): Int = context.dimenPixelSize(id)