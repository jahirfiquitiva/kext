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

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visible
import ca.allanwang.kau.utils.visibleIf
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor

open class EmptyViewRecyclerView:RecyclerView {
    var loadingView:View? = null
    var emptyView:View? = null
    var textView:TextView? = null
    var loadingTextRes:Int = -1
    var emptyTextRes:Int = -1

    var state:EmptyViewRecyclerView.State = State.LOADING
        get() = field
        set(value) {
            field = value
            updateStateViews()
        }

    constructor(context:Context):super(context)
    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet)
    constructor(context:Context, attributeSet:AttributeSet, defStyleAttr:Int)
            :super(context, attributeSet, defStyleAttr)

    private fun updateState() {
        if (adapter != null) {
            val items = adapter.itemCount
            if (items > 0) {
                state = State.NORMAL
            } else {
                state = State.EMPTY
            }
        } else {
            state = State.LOADING
        }
    }

    @SuppressLint("SwitchIntDef")
    private fun updateStateViews() {
        when (state) {
            State.LOADING -> {
                loadingView?.visible()
                emptyView?.gone()
                if (loadingTextRes != -1)
                    textView?.text = context.getString(loadingTextRes)
                gone()
            }
            State.NORMAL -> {
                if (adapter != null) {
                    val items = adapter.itemCount
                    if (items > 0) {
                        loadingView?.gone()
                        emptyView?.gone()
                        visible()
                    } else {
                        state = State.EMPTY
                    }
                } else {
                    state = State.LOADING
                }
            }
            State.EMPTY -> {
                loadingView?.gone()
                emptyView?.visible()
                if (emptyTextRes != -1)
                    textView?.text = context.getString(emptyTextRes)
                gone()
            }
        }
        textView?.setTextColor(context.secondaryTextColor)
        textView?.visibleIf(state != State.NORMAL)
    }

    internal val observer:RecyclerView.AdapterDataObserver = object:RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateState()
        }

        override fun onItemRangeChanged(positionStart:Int, itemCount:Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            updateState()
        }

        override fun onItemRangeInserted(positionStart:Int, itemCount:Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            updateState()
        }

        override fun onItemRangeRemoved(positionStart:Int, itemCount:Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            updateState()
        }
    }

    override fun setAdapter(adapter:Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        updateState()
    }

    enum class State {
        EMPTY, NORMAL, LOADING
    }
}