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

import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import ca.allanwang.kau.utils.isColorDark
import ca.allanwang.kau.utils.withAlpha

@ColorInt
fun Int.withAlpha(@FloatRange(from = 0.0, to = 1.0) factor: Float): Int =
        withAlpha((255 * factor).toInt())

@ColorInt
fun Int.shiftColor(@FloatRange(from = 0.0, to = 2.0) by: Float): Int {
    if (by == 1f) return this
    val alpha = Color.alpha(this)
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] *= by // value component
    return (alpha shl 24) + (0x00ffffff and Color.HSVToColor(hsv))
}

@ColorInt
fun Int.stripAlpha(): Int = Color.rgb(Color.red(this), Color.green(this), Color.blue(this))

fun Int.isColorLight(darkness: Float = 0.5F): Boolean = !isColorDark(darkness)

val Int.isColorLight: Boolean
    get() = !isColorDark

fun Int.getUriFromResource(context: Context): Uri =
        Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        context.resources.getResourcePackageName(this) + '/' +
                        context.resources.getResourceTypeName(this) + '/' +
                        context.resources.getResourceEntryName(this))