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

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.io.File
import java.io.FileOutputStream

fun Bitmap.isColorDark() = !isColorLight()

fun Bitmap.isColorLight(): Boolean = generatePalette()?.isColorLight() ?: false

fun Bitmap.generatePalette(resizeArea: Int = -1): androidx.palette.graphics.Palette? =
    try {
        androidx.palette.graphics.Palette.from(this).resizeBitmapArea(resizeArea).generate()
    } catch (e: Exception) {
        null
    }

val Bitmap.bestSwatch: androidx.palette.graphics.Palette.Swatch?
    get() = generatePalette()?.bestSwatch

fun Bitmap.createRoundedDrawable(context: Context): Drawable {
    val roundedPic = RoundedBitmapDrawableFactory.create(context.resources, this)
    roundedPic.isCircular = true
    roundedPic.setAntiAlias(true)
    return roundedPic
}

fun Bitmap.getUri(context: Context, name: String, extension: String = ".png"): Uri? {
    val iconFile = File(context.cacheDir, name + extension)
    val fos: FileOutputStream?
    try {
        fos = FileOutputStream(iconFile)
        compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        
        var uri = iconFile.getUri(context)
        if (uri == null) uri = context.getUriFromResource(context.resource(name))
        if (uri == null)
            uri = Uri.parse(
                "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/" +
                    "${context.resource(name)}")
        return uri
    } catch (e: Exception) {
        return null
    }
}