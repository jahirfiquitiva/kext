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
package jahirfiquitiva.libs.archhelpers.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import jahirfiquitiva.libs.archhelpers.ui.adapters.presenters.ListAdapterPresenter
import jahirfiquitiva.libs.kauextensions.extensions.clearChildrenAnimations

abstract class ListAdapter<T, VH : RecyclerView.ViewHolder>(private val maxLoad: Int = -1) :
        RecyclerView.Adapter<VH>(), ListAdapterPresenter<T> {
    private var lastAnimatedPosition = -1
    
    val list = ArrayList<T>()
    
    private var actualItemCount = maxLoad
    
    private fun resetItemCount() {
        actualItemCount = maxLoad
    }
    
    fun allowMoreItemsLoad() {
        val prevSize = itemCount
        val newCount = actualItemCount + maxLoad
        actualItemCount = if (newCount >= list.size) list.size else newCount
        notifyItemRangeInserted(prevSize, itemCount)
    }
    
    override fun getItemCount(): Int {
        return if (actualItemCount <= 0) {
            list.size
        } else {
            if (actualItemCount <= list.size) actualItemCount
            else list.size
        }
    }
    
    override fun onBindViewHolder(holder: VH, position: Int) {
        if (position in 0..itemCount) {
            if (position > lastAnimatedPosition) {
                lastAnimatedPosition = position
                doBind(holder, position, true)
            } else {
                doBind(holder, position, false)
            }
        }
    }
    
    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>?) {
        if (payloads != null) {
            if (payloads.isNotEmpty()) {
                doBind(holder, position, true)
            } else {
                onBindViewHolder(holder, position)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }
    
    abstract fun doBind(holder: VH, position: Int, shouldAnimate: Boolean)
    
    abstract fun doCreateVH(parent: ViewGroup, viewType: Int): VH
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
        parent?.let { return doCreateVH(it, viewType) } ?: return null
    }
    
    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView?.clearChildrenAnimations()
    }
    
    override fun addAll(newItems: ArrayList<T>) {
        val prevSize = itemCount
        list.addAll(newItems)
        resetItemCount()
        notifyItemRangeInserted(prevSize, newItems.size)
    }
    
    override fun clearList() {
        val size = itemCount
        list.clear()
        resetItemCount()
        notifyItemRangeRemoved(0, size)
    }
    
    override fun setItems(newItems: ArrayList<T>) {
        list.clear()
        list.addAll(newItems)
        resetItemCount()
        notifyDataSetChanged()
    }
    
    override fun addItem(newItem: T) {
        val prevSize = itemCount
        list.add(newItem)
        resetItemCount()
        notifyItemRangeInserted(prevSize, itemCount)
    }
    
    override fun removeItem(item: T) {
        val prevSize = itemCount
        val index = list.indexOf(item)
        if (index < 0) return
        list.remove(item)
        resetItemCount()
        notifyItemRangeRemoved(index, prevSize)
    }
    
    override fun updateItem(item: T) {
        val prevSize = itemCount
        val index = list.indexOf(item)
        if (index < 0) return
        resetItemCount()
        notifyItemRangeChanged(index, prevSize)
    }
    
    override fun get(index: Int): T = list[index]
}