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

    private val buttons = ArrayList<AppCompatButton>()

    var buttonCount:Int = 0
        set(@IntRange(from = 0, to = 4) value) {
            field = value
            weightSum = value.toFloat()
        }

    constructor(context:Context):super(context) {
        init()
    }

    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet) {
        init()
    }

    constructor(context:Context, attributeSet:AttributeSet, defStyleAttr:Int)
            :super(context, attributeSet, defStyleAttr) {
        init()
    }

    private fun init() {
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
        val button:AppCompatButton = context.inflateView(R.layout.item_split_button,
                                                         this) as AppCompatButton
        val lParams:LayoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1F)
        button.maxLines = 1
        button.ellipsize = TextUtils.TruncateAt.END
        button.text = text
        button.tag = link
        addView(button, lParams)
        buttons.add(button)
    }

    fun hasAllButtons():Boolean = childCount == buttonCount
}