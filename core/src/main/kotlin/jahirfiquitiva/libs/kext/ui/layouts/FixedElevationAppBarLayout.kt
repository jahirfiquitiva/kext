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
package jahirfiquitiva.libs.kext.ui.layouts

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.material.appbar.AppBarLayout
import android.util.AttributeSet
import ca.allanwang.kau.utils.dpToPx
import jahirfiquitiva.libs.kext.R

open class FixedElevationAppBarLayout : AppBarLayout {
    
    private var fElevation: Int = 4
    
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }
    
    private fun init(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.FixedElevationAppBarLayout, 0, 0)
        try {
            fElevation = a.getDimensionPixelSize(
                R.styleable.FixedElevationAppBarLayout_fixedElevation, 4.dpToPx)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = 0F
            }
        } finally {
            a.recycle()
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun setElevation(elevation: Float) = super.setElevation(fElevation.toFloat())
}