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
package jahirfiquitiva.libs.kext.ui.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.extensions.activeIconsColor
import jahirfiquitiva.libs.kext.extensions.applyColorFilter
import jahirfiquitiva.libs.kext.extensions.inactiveIconsColor

open class MaterialIconView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
                                                     ) :
    AppCompatImageView(context, attributeSet, style) {
    
    var isActive = true
        set(value) {
            field = value
            setImageDrawable(drawable)
        }
    
    init {
        init(context, attributeSet)
    }
    
    private fun init(context: Context, attributeSet: AttributeSet?) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.MaterialIconView, 0, 0)
        try {
            isActive = a.getBoolean(R.styleable.MaterialIconView_active, true)
        } finally {
            a.recycle()
        }
    }
    
    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(
            drawable?.applyColorFilter(
                if (isActive) context.activeIconsColor else context.inactiveIconsColor))
    }
}
