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

import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.view.View

class CustomAppBarBehavior(var scrollAllowed: Boolean = true) : AppBarLayout.Behavior() {
    init {
        setDragCallback(null)
    }
    
    override fun setDragCallback(ignored: AppBarLayout.Behavior.DragCallback?) {
        super.setDragCallback(
                object : AppBarLayout.Behavior.DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean = scrollAllowed
                })
    }
    
    override fun onStartNestedScroll(
            parent: CoordinatorLayout, child: AppBarLayout,
            directTargetChild: View, target: View, nestedScrollAxes: Int,
            type: Int
                                    ): Boolean {
        return scrollAllowed && super.onStartNestedScroll(
                parent, child, directTargetChild,
                target, nestedScrollAxes, type)
    }
}