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

package jahirfiquitiva.libs.kauextensions.ui.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visible
import jahirfiquitiva.libs.kauextensions.R
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor

open class EmptyViewRecyclerView:RecyclerView {
    var loadingView:View? = null
    var emptyView:View? = null
    var textView:TextView? = null
    var loadingTextRes:Int = -1
    var emptyTextRes:Int = -1
    
    var state:State = State.LOADING
        set(value) {
            if (value != field) {
                field = value
                updateStateViews()
            }
        }
    
    constructor(context:Context):super(context)
    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet)
    constructor(context:Context, attributeSet:AttributeSet, defStyleAttr:Int)
            :super(context, attributeSet, defStyleAttr)
    
    private fun updateStateViews() {
        textView?.setTextColor(context.secondaryTextColor)
        when (state) {
            State.LOADING -> {
                gone()
                emptyView?.gone()
                loadingView?.visible()
                textView?.text = context.getString(
                        if (loadingTextRes != -1) loadingTextRes else R.string.loading_section)
                textView?.visible()
            }
            State.NORMAL -> {
                if (adapter != null) {
                    val items = adapter.itemCount
                    if (items > 0) {
                        loadingView?.gone()
                        emptyView?.gone()
                        textView?.gone()
                        visible()
                    } else {
                        state = State.EMPTY
                    }
                } else {
                    state = State.LOADING
                }
            }
            State.EMPTY -> {
                gone()
                loadingView?.gone()
                emptyView?.visible()
                textView?.text = context.getString(
                        if (emptyTextRes != -1) emptyTextRes else R.string.empty_section)
                textView?.visible()
            }
        }
    }
    
    private val observer:RecyclerView.AdapterDataObserver = object:RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateStateViews()
        }
        
        override fun onItemRangeChanged(positionStart:Int, itemCount:Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            updateStateViews()
        }
        
        override fun onItemRangeChanged(positionStart:Int, itemCount:Int, payload:Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            updateStateViews()
        }
        
        override fun onItemRangeInserted(positionStart:Int, itemCount:Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            updateStateViews()
        }
        
        override fun onItemRangeRemoved(positionStart:Int, itemCount:Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            updateStateViews()
        }
        
        override fun onItemRangeMoved(fromPosition:Int, toPosition:Int, itemCount:Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            updateStateViews()
        }
    }
    
    override fun setAdapter(adapter:Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        updateStateViews()
    }
    
    enum class State {
        EMPTY, NORMAL, LOADING
    }
}