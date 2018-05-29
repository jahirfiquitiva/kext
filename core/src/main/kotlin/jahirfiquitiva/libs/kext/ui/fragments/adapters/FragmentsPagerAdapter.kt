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
package jahirfiquitiva.libs.kext.ui.fragments.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

class FragmentsPagerAdapter(manager: FragmentManager, vararg fragments: Fragment) :
    FragmentStatePagerAdapter(manager) {
    
    private val frags = ArrayList<Fragment?>()
    
    init {
        this.frags.clear()
        this.frags.addAll(fragments)
    }
    
    fun getFragments(): ArrayList<Fragment?> = frags
    
    fun addAll(vararg fragments: Fragment) {
        this.frags.clear()
        this.frags.addAll(fragments)
    }
    
    fun addAll(fragments: ArrayList<Fragment>) {
        this.frags.clear()
        this.frags.addAll(fragments)
    }
    
    override fun getItemPosition(obj: Any): Int {
        if (obj !is Fragment) return -1
        val index = frags.indexOf(obj)
        return if (index < 0) PagerAdapter.POSITION_NONE
        else index
    }
    
    override fun getItem(index: Int): Fragment? {
        return try {
            frags[index]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun addFragment(fragment: Fragment) {
        if (fragment in frags) return
        frags.add(fragment)
        notifyDataSetChanged()
    }
    
    fun addFragmentAt(fragment: Fragment, index: Int) {
        if (fragment in frags) return
        frags.add(index, fragment)
        notifyDataSetChanged()
    }
    
    fun removeFragment(fragment: Fragment) {
        if (fragment in frags) {
            fragment.onDestroy()
            frags.remove(fragment)
            notifyDataSetChanged()
        }
    }
    
    fun removeItemAt(index: Int) {
        frags[index]?.let { removeFragment(it) }
    }
    
    override fun getCount(): Int = frags.size
    
    operator fun get(index: Int): Fragment? = getItem(index)
    operator fun plus(fragments: ArrayList<Fragment>) = this.frags.addAll(fragments)
    operator fun plusAssign(fragment: Fragment) = addFragment(fragment)
    operator fun minusAssign(fragment: Fragment) = removeFragment(fragment)
}