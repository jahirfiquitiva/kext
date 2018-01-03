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
package jahirfiquitiva.libs.kauextensions.ui.layouts

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

@CoordinatorLayout.DefaultBehavior(CustomAppBarBehavior::class)
open class DisableableAppBarLayout : AppBarLayout {
    
    var scrollAllowed: Boolean = true
        set(value) {
            field = value
            try {
                val params = layoutParams as CoordinatorLayout.LayoutParams
                params.behavior = CustomAppBarBehavior(value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    
    var isExpandedNow: Boolean = false
    
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    
    override fun setExpanded(expanded: Boolean, animate: Boolean) {
        super.setExpanded(expanded, animate)
        isExpandedNow = expanded
    }
}