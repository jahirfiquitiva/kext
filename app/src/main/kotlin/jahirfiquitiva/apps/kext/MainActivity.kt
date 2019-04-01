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
package jahirfiquitiva.apps.kext

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import ca.allanwang.kau.utils.toast
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.hideAllItems
import jahirfiquitiva.libs.kext.extensions.primaryColor
import jahirfiquitiva.libs.kext.extensions.showAllItems
import jahirfiquitiva.libs.kext.extensions.tint
import jahirfiquitiva.libs.kext.helpers.Prefs
import jahirfiquitiva.libs.kext.ui.activities.ThemedActivity
import jahirfiquitiva.libs.kext.ui.widgets.CustomSearchView

class MainActivity : ThemedActivity<Prefs>() {
    
    override fun lightTheme(): Int = R.style.AppTheme
    override fun darkTheme(): Int = R.style.AppTheme
    
    override val prefs: Prefs by lazy { Prefs("kext", this) }
    
    override fun recentsColor(): Int = Color.parseColor("#4285F4")
    
    private var searchView: CustomSearchView? = null
    
    private val toolbar: Toolbar? by bind(R.id.toolbar)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setSupportActionBar(toolbar)
        toolbar?.tint(Color.WHITE, Color.WHITE, Color.WHITE)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        
        menu?.let {
            // searchView = bindSearchView(it, R.id.search)
            val searchItem = it.findItem(R.id.search)
            searchView = searchItem.actionView as CustomSearchView?
            searchView?.onExpand = {
                it.hideAllItems()
                toast("Expanded")
            }
            searchView?.onCollapse = {
                it.showAllItems()
                toast("Collapsed")
            }
            searchView?.onQueryChanged = { Log.d("KAUExt", "Query changed: $it") }
            searchView?.onQuerySubmit = { Log.d("KAUExt", "Query submit: $it") }
            searchView?.bindToItem(searchItem)
            searchView?.tint(getPrimaryTextColorFor(primaryColor, 0.7F))
        }
        menu?.tint(Color.WHITE)
        toolbar?.tint(Color.WHITE, Color.WHITE, Color.WHITE)
        return super.onCreateOptionsMenu(menu)
    }
}
