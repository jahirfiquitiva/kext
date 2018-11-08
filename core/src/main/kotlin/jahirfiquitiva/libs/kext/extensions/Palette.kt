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

import androidx.palette.graphics.Palette
import java.util.Collections

fun androidx.palette.graphics.Palette.isColorDark() = !isColorLight()

fun androidx.palette.graphics.Palette.isColorLight() = bestSwatch?.rgb?.isColorLight() ?: false

val androidx.palette.graphics.Palette.bestSwatch: androidx.palette.graphics.Palette.Swatch?
    get() {
        dominantSwatch?.let { return it }
        vibrantSwatch?.let { return it }
        mutedSwatch?.let { return it }
        lightVibrantSwatch?.let { return it }
        darkVibrantSwatch?.let { return it }
        lightMutedSwatch?.let { return it }
        darkMutedSwatch?.let { return it }
        if (swatches.isNotEmpty()) return getBestPaletteSwatch(swatches)
        return null
    }

private fun getBestPaletteSwatch(swatches: List<androidx.palette.graphics.Palette.Swatch>): androidx.palette.graphics.Palette.Swatch =
    Collections.max<androidx.palette.graphics.Palette.Swatch>(swatches) { opt1, opt2 ->
        val a = opt1?.population ?: 0
        val b = opt2?.population ?: 0
        a - b
    }