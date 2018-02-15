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
package jahirfiquitiva.libs.archhelpers.ui.fragments

import android.os.Bundle
import jahirfiquitiva.libs.archhelpers.ui.fragments.presenters.ViewModelFragmentPresenter
import jahirfiquitiva.libs.kauextensions.ui.fragments.Fragment

abstract class ViewModelFragment<in T> : Fragment<T>(), ViewModelFragmentPresenter<T> {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initVM()
    }
    
    private fun initVM() {
        initViewModel()
        registerObserver()
        if (autoStartLoad()) loadDataFromViewModel()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unregisterObserver()
    }
    
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && autoStartLoad() && allowReloadAfterVisibleToUser())
            loadDataFromViewModel()
    }
    
    override fun onItemClicked(item: T, longClick: Boolean) {}
    abstract fun autoStartLoad(): Boolean
    open fun allowReloadAfterVisibleToUser(): Boolean = false
}