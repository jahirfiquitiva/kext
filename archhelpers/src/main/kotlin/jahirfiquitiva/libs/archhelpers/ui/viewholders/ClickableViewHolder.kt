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
package jahirfiquitiva.libs.archhelpers.ui.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View

open class ClickableViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setOnClickListener(item: T,
                           listener: (T, RecyclerView.ViewHolder, Boolean) -> Unit) {
        with(itemView) {
            setOnClickListener { listener(item, this@ClickableViewHolder, false) }
            setOnLongClickListener { listener(item, this@ClickableViewHolder, true);true }
        }
    }
}