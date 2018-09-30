/*
 * Copyright (c) 2018.
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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v4.util.LongSparseArray
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Implementation of [PagerAdapter] that
 * uses a [Fragment] to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 *
 * This version of the pager is more useful when there are a large number
 * of pages, working more like a list view.  When pages are not visible to
 * the user, their entire fragment may be destroyed, only keeping the saved
 * state of that fragment.  This allows the pager to hold on to much less
 * memory associated with each visited page as compared to
 * [FragmentPagerAdapter] at the cost of potentially more overhead when
 * switching between pages.
 *
 *
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.
 *
 *
 * Subclasses only need to implement [.getItem]
 * and [.getCount] to have a working adapter. They also should
 * override [.getItemId] if the position of the items can change.
 *
 *
 * Credits to inloop:
 * https://github.com/inloop/UpdatableFragmentStatePagerAdapter/
 *
 */
@SuppressLint("CommitTransaction")
abstract class DynamicFragmentsPagerAdapter(private val fragmentManager: FragmentManager) :
    PagerAdapter() {
    private var currentTransaction: FragmentTransaction? = null
    private var currentMainItem: Fragment? = null
    
    private val fragments = LongSparseArray<Fragment>()
    private val mSavedStates = LongSparseArray<Fragment.SavedState>()
    
    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment?
    
    override fun startUpdate(container: ViewGroup) {
        if (container.id == View.NO_ID)
            throw IllegalStateException("ViewPager with adapter $this requires a view id")
    }
    
    override fun instantiateItem(container: ViewGroup, position: Int): Fragment {
        val tag = getItemId(position)
        var fragment = fragments.get(tag)
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (fragment != null) {
            return fragment
        }
        
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }
        
        fragment = getItem(position)
        // restore state
        val savedState = mSavedStates.get(tag)
        if (savedState != null) {
            fragment?.setInitialSavedState(savedState)
        }
        fragment?.setMenuVisibility(false)
        fragment?.userVisibleHint = false
        fragments.put(tag, fragment)
        currentTransaction?.add(container.id, fragment!!, "f$tag")
        return fragment!!
    }
    
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment
        val currentPosition = getItemPosition(fragment)
        
        val index = fragments.indexOfValue(fragment)
        var fragmentKey: Long = -1
        if (index != -1) {
            fragmentKey = fragments.keyAt(index)
            fragments.removeAt(index)
        }
        
        //item hasn't been removed
        if (fragment.isAdded && currentPosition != PagerAdapter.POSITION_NONE) {
            mSavedStates.put(fragmentKey, fragmentManager.saveFragmentInstanceState(fragment))
        } else {
            mSavedStates.remove(fragmentKey)
        }
        
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }
        
        currentTransaction?.remove(fragment)
    }
    
    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment?
        if (fragment !== currentMainItem) {
            if (currentMainItem != null) {
                currentMainItem?.setMenuVisibility(false)
                currentMainItem?.userVisibleHint = false
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                fragment.userVisibleHint = true
            }
            currentMainItem = fragment
        }
    }
    
    override fun finishUpdate(container: ViewGroup) {
        if (currentTransaction != null) {
            currentTransaction?.commitNowAllowingStateLoss()
            currentTransaction = null
        }
    }
    
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }
    
    override fun saveState(): Parcelable? {
        var state: Bundle? = null
        if (mSavedStates.size() > 0) {
            // save Fragment states
            state = Bundle()
            val stateIds = LongArray(mSavedStates.size())
            for (i in 0 until mSavedStates.size()) {
                val entry = mSavedStates.valueAt(i)
                stateIds[i] = mSavedStates.keyAt(i)
                state.putParcelable(java.lang.Long.toString(stateIds[i]), entry)
            }
            state.putLongArray("states", stateIds)
        }
        for (i in 0 until fragments.size()) {
            val f = fragments.valueAt(i)
            if (f != null && f.isAdded) {
                if (state == null) {
                    state = Bundle()
                }
                val key = "f" + fragments.keyAt(i)
                fragmentManager.putFragment(state, key, f)
            }
        }
        return state
    }
    
    @SuppressLint("LongLogTag")
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state != null) {
            val bundle = state as Bundle?
            bundle?.classLoader = loader
            val fss = bundle?.getLongArray("states")
            mSavedStates.clear()
            fragments.clear()
            if (fss != null) {
                for (fs in fss) {
                    mSavedStates.put(
                        fs, bundle.getParcelable<Parcelable>(
                        fs.toString()) as Fragment.SavedState)
                }
            }
            val keys = bundle?.keySet().orEmpty()
            for (key in keys) {
                if (key.startsWith("f")) {
                    val f = fragmentManager.getFragment(bundle!!, key)
                    if (f != null) {
                        f.setMenuVisibility(false)
                        fragments.put(java.lang.Long.parseLong(key.substring(1)), f)
                    } else {
                        Log.w(TAG, "Bad fragment at key $key")
                    }
                }
            }
        }
    }
    
    /**
     * Return a unique identifier for the item at the given position.
     *
     *
     *
     * The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    open fun getItemId(position: Int): Long {
        return position.toLong()
    }
    
    fun post(index: Int, what: (Fragment) -> Unit) {
        try {
            fragments.get(getItemId(index))?.let { what(it) }
        } catch (e: Exception) {
        }
    }
    
    fun post(what: (Int, Fragment) -> Unit) {
        for (i in 0 until fragments.size()) {
            fragments.get(getItemId(i))?.let { what(i, it) }
        }
    }
    
    companion object {
        private const val TAG = "DynamicFragmentsPagerAdapter"
    }
}