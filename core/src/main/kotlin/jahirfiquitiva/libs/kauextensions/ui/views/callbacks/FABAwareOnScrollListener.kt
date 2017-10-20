/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
package jahirfiquitiva.libs.kauextensions.ui.views.callbacks

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import ca.allanwang.kau.utils.isVisible
import jahirfiquitiva.libs.kauextensions.extensions.isNotVisible

open class FABAwareOnScrollListener(
        private val fab: FloatingActionButton
                                   ) : RecyclerView.OnScrollListener() {
    
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0 && fab.isVisible) {
            fab.hide()
        } else if (dy < 0 && fab.isNotVisible) {
            fab.show()
        }
    }
}