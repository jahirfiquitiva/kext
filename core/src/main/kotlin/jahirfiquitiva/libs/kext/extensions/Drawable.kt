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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette

fun Drawable.applyColorFilter(@ColorInt color: Int): Drawable {
    val newFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    colorFilter = newFilter
    return this
}

fun Drawable.generatePalette(resizeArea: Int = -1): androidx.palette.graphics.Palette? =
    this.toBitmap().generatePalette(resizeArea)

val Drawable.bestSwatch: androidx.palette.graphics.Palette.Swatch?
    get() = generatePalette()?.bestSwatch

/**
 * Extracts the bitmap of a drawable, and applies a scale if given
 * For solid colors, a 1 x 1 pixel will be generated
 */
fun Drawable.toBitmap(
    scaling: Float = 1f,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888
                     ): Bitmap {
    if (this is BitmapDrawable && bitmap != null) {
        if (scaling == 1f) return bitmap
        val width = (bitmap.width * scaling).toInt()
        val height = (bitmap.height * scaling).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
    val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0)
        Bitmap.createBitmap(1, 1, config)
    else
        Bitmap.createBitmap(
            (intrinsicWidth * scaling).toInt(), (intrinsicHeight * scaling).toInt(), config)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}