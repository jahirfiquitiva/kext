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
import android.support.annotation.IdRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View

inline fun <reified T : View> RecyclerView.ViewHolder.bind(@IdRes res: Int): Lazy<T?> =
    lazy { itemView.findViewById<T>(res) }

val RecyclerView.ViewHolder.context: Context
    get() = itemView.context

fun RecyclerView.ViewHolder.string(@StringRes stringRes: Int, fallback: String = ""): String =
    itemView.context.string(stringRes, fallback)

fun RecyclerView.ViewHolder.stringArray(@ArrayRes arrayRes: Int): Array<String>? =
    itemView.context.stringArray(arrayRes)

fun RecyclerView.ViewHolder.boolean(@BoolRes bool: Int): Boolean =
    itemView.context.boolean(bool)

fun RecyclerView.ViewHolder.int(@IntegerRes id: Int): Int =
    itemView.context.int(id)

fun RecyclerView.ViewHolder.dimen(@DimenRes id: Int): Float =
    itemView.context.dimen(id)

fun RecyclerView.ViewHolder.dimenPixelSize(@DimenRes id: Int): Int =
    itemView.context.dimenPixelSize(id)