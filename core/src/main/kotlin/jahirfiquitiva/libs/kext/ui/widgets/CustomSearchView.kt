/*
 * Copyright (c) 2019. Jahir Fiquitiva
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
package jahirfiquitiva.libs.kext.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.withAlpha
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.primaryColor
import jahirfiquitiva.libs.kext.extensions.tint

class CustomSearchView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
                                                ) :
    SearchView(context, attributeSet, style) {
    
    private var tintColor: Int = 0
    
    var isOpen = false
        private set
    
    var onExpand: () -> Unit = {}
    var onCollapse: () -> Unit = {}
    
    var onQueryChanged: (query: String) -> Unit = {}
    var onQuerySubmit: (query: String) -> Unit = {}
    
    init {
        init()
    }
    
    private fun init() {
        maxWidth = Int.MAX_VALUE
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        
        imeOptions = EditorInfo.IME_ACTION_SEARCH
        setIconifiedByDefault(false)
        isIconified = false
        removeSearchIcon()
        
        super.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { onQueryChanged(it.trim()) }
                return true
            }
            
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { onQuerySubmit(it.trim()) }
                return true
            }
        })
        
        tint(context.getPrimaryTextColorFor(context.primaryColor, 0.6F))
    }
    
    private fun removeSearchIcon() {
        try {
            val magImage = findViewById<ImageView?>(androidx.appcompat.R.id.search_mag_icon)
            magImage?.setImageDrawable(null)
            magImage?.gone()
        } catch (e: Exception) {
        }
    }
    
    fun tint(@ColorInt color: Int, @ColorInt hintColor: Int = color) {
        this.tintColor = color
        
        try {
            val field =
                findViewById<EditText?>(R.id.search_src_text)
            field?.setTextColor(color)
            field?.setHintTextColor(
                if (hintColor == color) hintColor.withAlpha(0.5F) else hintColor)
            field?.tint(color)
        } catch (e: Exception) {
        }
        
        try {
            val plate =
                findViewById<LinearLayout?>(R.id.search_plate)
            plate?.background = null
        } catch (e: Exception) {
        }
        
        try {
            val closeIcon =
                findViewById<ImageView?>(R.id.search_button)
            closeIcon?.tint(color)
        } catch (e: Exception) {
        }
        
        try {
            val closeIcon =
                findViewById<ImageView?>(R.id.search_close_btn)
            closeIcon?.tint(color)
        } catch (e: Exception) {
        }
        
        try {
            val goIcon =
                findViewById<ImageView?>(R.id.search_go_btn)
            goIcon?.tint(color)
        } catch (e: Exception) {
        }
        
        try {
            val voiceIcon =
                findViewById<ImageView?>(R.id.search_voice_btn)
            voiceIcon?.tint(color)
        } catch (e: Exception) {
        }
    }
    
    fun tintWith(@ColorInt color: Int, @ColorInt hintColor: Int = color) {
        tint(color, hintColor)
    }
    
    override fun setIconified(iconify: Boolean) {
        super.setIconified(false)
    }
    
    override fun setIconifiedByDefault(iconified: Boolean) {
        super.setIconifiedByDefault(false)
    }
    
    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        this.onQuerySubmit = { listener?.onQueryTextSubmit(it) }
        this.onQueryChanged = { listener?.onQueryTextChange(it) }
    }
    
    fun bindToItem(item: MenuItem?) {
        item?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                onExpand()
                isOpen = true
                return true
            }
            
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                onCollapse()
                isOpen = false
                return true
            }
        })
    }
}
