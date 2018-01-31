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
package jahirfiquitiva.apps.kauextensions

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import ca.allanwang.kau.utils.toast
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.hideAllItems
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.showAllItems
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.ui.widgets.CustomSearchView

class MainActivity : AppCompatActivity() {
    
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