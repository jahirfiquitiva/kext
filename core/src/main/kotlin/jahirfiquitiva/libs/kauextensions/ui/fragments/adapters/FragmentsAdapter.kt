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
package jahirfiquitiva.libs.kauextensions.ui.fragments.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

class FragmentsAdapter(manager: FragmentManager, vararg fragments: Fragment) :
        FragmentStatePagerAdapter(manager) {
    
    val fragments = ArrayList<Fragment?>()
    
    init {
        this.fragments.clear()
        this.fragments.addAll(fragments)
    }
    
    override fun getItemPosition(obj: Any): Int {
        if (obj !is Fragment) return -1
        val index = fragments.indexOf(obj)
        return if (index < 0) PagerAdapter.POSITION_NONE
        else index
    }
    
    override fun getItem(index: Int): Fragment? {
        return try {
            fragments[index]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun addFragment(fragment: Fragment) {
        if (fragment in fragments) return
        fragments.add(fragment)
        notifyDataSetChanged()
    }
    
    fun addFragmentAt(fragment: Fragment, index: Int) {
        if (fragment in fragments) return
        fragments.add(index, fragment)
        notifyDataSetChanged()
    }
    
    fun removeFragment(fragment: Fragment) {
        if (fragment in fragments) {
            fragment.onDestroy()
            fragments.remove(fragment)
            notifyDataSetChanged()
        }
    }
    
    fun removeItemAt(index: Int) {
        fragments[index]?.let { removeFragment(it) }
    }
    
    override fun getCount(): Int = fragments.size
    
    operator fun get(index: Int): Fragment? = getItem(index)
    operator fun plus(fragments: ArrayList<Fragment>) = this.fragments.addAll(fragments)
    operator fun plusAssign(fragment: Fragment) = addFragment(fragment)
    operator fun minusAssign(fragment: Fragment) = removeFragment(fragment)
}