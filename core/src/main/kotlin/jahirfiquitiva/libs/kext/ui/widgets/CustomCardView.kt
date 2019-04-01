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
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import jahirfiquitiva.libs.kext.extensions.cardBackgroundColor

open class CustomCardView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
                                                   ) :
    CardView(context, attributeSet, style) {
    
    init {
        init()
    }
    
    private fun init() {
        setBackgroundColor(0)
    }
    
    override fun setBackgroundColor(color: Int) {
        forceSetCardBackgroundColor(context.cardBackgroundColor)
    }
    
    override fun setCardBackgroundColor(@ColorInt color: Int) {
        forceSetCardBackgroundColor(context.cardBackgroundColor)
    }
    
    override fun setCardBackgroundColor(color: ColorStateList?) {
        forceSetCardBackgroundColor(ColorStateList.valueOf(context.cardBackgroundColor))
    }
    
    fun forceSetCardBackgroundColor(@ColorInt color: Int) {
        super.setCardBackgroundColor(color)
    }
    
    fun forceSetCardBackgroundColor(color: ColorStateList?) {
        super.setCardBackgroundColor(color)
    }
}
