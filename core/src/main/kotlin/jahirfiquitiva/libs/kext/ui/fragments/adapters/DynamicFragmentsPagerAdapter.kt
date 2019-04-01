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
package jahirfiquitiva.libs.kext.ui.fragments.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@Deprecated("Use DynamicFragmentsPagerAdapter instead", ReplaceWith("DynamicFragmentsPagerAdapter"))
typealias FragmentsPagerAdapter = DynamicFragmentsPagerAdapter

abstract class DynamicFragmentsPagerAdapter(private val manager: FragmentManager) :
    FragmentStatePagerAdapter(manager) {
    
    companion object {
        private const val STATE_SUPER_STATE = "superState"
        private const val STATE_PAGES = "pages"
        private const val STATE_PAGE_INDEX_PREFIX = "pageIndex:"
        private const val STATE_PAGE_KEY_PREFIX = "page:"
    }
    
    private var frags = SparseArray<Fragment>()
    
    override fun saveState(): Parcelable? {
        val p = super.saveState()
        val bundle = Bundle()
        bundle.putParcelable(STATE_SUPER_STATE, p)
        
        bundle.putInt(STATE_PAGES, frags.size())
        if (0 < frags.size()) {
            for (i in 0 until frags.size()) {
                val position = frags.keyAt(i)
                bundle.putInt(createCacheIndex(i), position)
                val f = frags.get(position)
                manager.putFragment(bundle, createCacheKey(position), f)
            }
        }
        return bundle
    }
    
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        (state as? Bundle)?.let {
            val pages = it.getInt(STATE_PAGES)
            if (0 < pages) {
                for (i in 0 until pages) {
                    val position = it.getInt(createCacheIndex(i))
                    val f = manager.getFragment(it, createCacheKey(position))
                    frags.put(position, f)
                }
            }
            val p = it.getParcelable<Parcelable>(STATE_SUPER_STATE)
            super.restoreState(p, loader)
        }
    }
    
    /**
     * Get a new Fragment instance.
     * Each fragments are automatically cached in this method,
     * so you don't have to do it by yourself.
     * If you want to implement instantiation of Fragments,
     * you should override [.createItem] instead.
     *
     * {@inheritDoc}
     *
     * @param position position of the item in the adapter
     * @return fragment instance
     */
    @SuppressLint("LongLogTag")
    override fun getItem(position: Int): Fragment {
        Log.w(
            "DynamicFragmentsPagerAdapter",
            "Use getItemAt() because you're recreating the fragment, instead of getting the existent instance.")
        val f = createItem(position)
        // We should cache fragments manually to access to them later
        frags.put(position, f)
        return f
    }
    
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (0 <= frags.indexOfKey(position)) {
            frags.remove(position)
        }
        super.destroyItem(container, position, `object`)
    }
    
    /**
     * Get the item at the specified position in the adapter.
     *
     * @param position position of the item in the adapter
     * @return fragment instance
     */
    fun getItemAt(position: Int): Fragment? = try {
        frags.get(position)
    } catch (e: Exception) {
        null
    }
    
    /**
     * Create a new Fragment instance.
     * This is called inside [.getItem].
     *
     * @param position position of the item in the adapter
     * @return fragment instance
     */
    protected abstract fun createItem(position: Int): Fragment
    
    /**
     * Create an index string for caching Fragment pages.
     *
     * @param index index of the item in the adapter
     * @return key string for caching Fragment pages
     */
    private fun createCacheIndex(index: Int): String {
        return STATE_PAGE_INDEX_PREFIX + index
    }
    
    /**
     * Create a key string for caching Fragment pages.
     *
     * @param position position of the item in the adapter
     * @return key string for caching Fragment pages
     */
    private fun createCacheKey(position: Int): String {
        return STATE_PAGE_KEY_PREFIX + position
    }
    
    fun post(index: Int, what: (Fragment) -> Unit) {
        getItemAt(index)?.let { what(it) }
    }
    
    fun forEach(what: (Int, Fragment) -> Unit) {
        for (i in 0 until frags.size()) {
            getItemAt(i)?.let { what(i, it) }
        }
    }
    
    operator fun get(index: Int): Fragment? = getItemAt(index)
}
