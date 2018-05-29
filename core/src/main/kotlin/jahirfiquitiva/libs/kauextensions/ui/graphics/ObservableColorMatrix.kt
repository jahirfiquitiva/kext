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
package jahirfiquitiva.libs.kauextensions.ui.graphics

import android.graphics.ColorMatrix
import android.util.Property

/**
 * Credits to Mysplash
 * @author WangDaYeeeeee
 * https://goo.gl/heH8Qv
 */
class ObservableColorMatrix : ColorMatrix() {
    
    private var saturation = 1F
    
    override fun setSaturation(sat: Float) {
        saturation = sat
        super.setSaturation(sat)
    }
    
    companion object {
        @JvmField
        val SATURATION: Property<ObservableColorMatrix, Float> =
            object : FloatPropertyCompat<ObservableColorMatrix>("saturation") {
                override fun setValue(obj: ObservableColorMatrix, value: Float) {
                    obj.setSaturation(value)
                }
                
                override operator fun get(cm: ObservableColorMatrix): Float? = cm.saturation
            }
    }
}

internal abstract class FloatPropertyCompat<T>(name: String) :
    Property<T, Float>(Float::class.java, name) {
    
    abstract fun setValue(obj: T, value: Float)
    
    override fun set(`object`: T, value: Float?) {
        setValue(`object`, value ?: 0.0F)
    }
}