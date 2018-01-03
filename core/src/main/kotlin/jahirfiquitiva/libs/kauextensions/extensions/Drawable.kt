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

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v7.graphics.Palette
import ca.allanwang.kau.utils.toBitmap

fun Drawable.applyColorFilter(@ColorInt color: Int): Drawable {
    val newFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    colorFilter = newFilter
    return this
}

val Drawable.bestSwatch: Palette.Swatch?
    get() = this.toBitmap().bestSwatch