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

package jahirfiquitiva.libs.kauextensions.ui.layouts

import android.content.Context
import android.support.annotation.IntRange
import android.support.annotation.StyleRes
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import jahirfiquitiva.libs.kauextensions.R
import jahirfiquitiva.libs.kauextensions.extensions.inflateView

/**
 * Originally created by Aidan Follestad
 */
open class SplitButtonsLayout:LinearLayout {

    var buttonCount:Int = 0
        set(@IntRange(from = 0, to = 4) value) {
            field = value
            weightSum = value.toFloat()
        }

    var itemsPaddingLeft = 0
    var itemsPaddingRight = 0
    var itemsPaddingTop = 0
    var itemsPaddingBottom = 0
    var itemsPadding = 0
    var firstItemPaddingLeft = 0
    var firstItemPaddingRight = 0
    var firstItemPaddingTop = 0
    var firstItemPaddingBottom = 0
    var firstItemPadding = 0

    @StyleRes
    var buttonsStyle = 0

    constructor(context:Context):super(context) {
        init(context, null)
    }

    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet) {
        init(context, attributeSet)
    }

    constructor(context:Context, attributeSet:AttributeSet, defStyleAttr:Int)
            :super(context, attributeSet, defStyleAttr) {
        init(context, attributeSet)
    }

    private fun init(context:Context, attributeSet:AttributeSet?) {
        attributeSet?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.SplitButtonsLayout)
            try {
                buttonsStyle = a.getInt(R.styleable.SplitButtonsLayout_buttonsStyle, 0)
                itemsPaddingLeft = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_itemsPaddingLeft, 0)
                itemsPaddingRight = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_itemsPaddingRight, 0)
                itemsPaddingTop = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_itemsPaddingTop, 0)
                itemsPaddingBottom = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_itemsPaddingBottom, 0)
                itemsPadding = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_itemsPadding, 0)
                firstItemPaddingLeft = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_firstItemPaddingLeft, 0)
                firstItemPaddingRight = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_firstItemPaddingRight, 0)
                firstItemPaddingTop = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_firstItemPaddingTop, 0)
                firstItemPaddingBottom = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_firstItemPaddingBottom, 0)
                firstItemPadding = a.getDimensionPixelSize(
                        R.styleable.SplitButtonsLayout_firstItemPadding, 0)
            } finally {
                a.recycle()
            }
        }
        orientation = HORIZONTAL
        if (isInEditMode) {
            buttonCount = 2
            addButton("Website", "https://github.com/jahirfiquitiva/KAUExtensions")
            addButton("Google+", "https://google.com/+JahirFiquitivaR")
        }
    }

    override fun setOrientation(orientation:Int) = super.setOrientation(HORIZONTAL)

    fun addButton(text:String, link:String) {
        if (hasAllButtons()) throw IllegalStateException("$buttonCount buttons already added")
        val button:AppCompatButton
        if (buttonsStyle != 0) {
            button = AppCompatButton(context, null, buttonsStyle)
        } else {
            button = context.inflateView(R.layout.item_split_button, this) as AppCompatButton
        }
        val lParams:LayoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F)
        try {
            if (childCount < 1) {
                if (firstItemPadding != 0) {
                    button.setPadding(firstItemPadding, firstItemPadding, firstItemPadding,
                                      firstItemPadding)
                } else {
                    button.setPadding(firstItemPaddingLeft, firstItemPaddingTop,
                                      firstItemPaddingRight, firstItemPaddingBottom)
                }
            } else {
                if (itemsPadding != 0) {
                    button.setPadding(itemsPadding, itemsPadding, itemsPadding, itemsPadding)
                } else {
                    button.setPadding(itemsPaddingLeft, itemsPaddingTop, itemsPaddingRight,
                                      itemsPaddingBottom)
                }
            }
            button.maxLines = 1
            button.ellipsize = TextUtils.TruncateAt.END
            button.text = text
            button.tag = link
            addView(button, lParams)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun hasAllButtons():Boolean = childCount == buttonCount
}