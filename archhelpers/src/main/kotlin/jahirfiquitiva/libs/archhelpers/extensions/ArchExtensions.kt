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
package jahirfiquitiva.libs.archhelpers.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline fun <reified T : ViewModel> androidx.fragment.app.FragmentActivity.getViewModel(): T =
    ViewModelProviders.of(this)[T::class.java]

inline fun <reified T : ViewModel> androidx.fragment.app.Fragment.getViewModel(): T {
    return activity?.let {
        ViewModelProviders.of(it)[T::class.java]
    } ?: ViewModelProviders.of(this)[T::class.java]
}

inline fun <reified T : ViewModel> androidx.fragment.app.FragmentActivity.lazyViewModel(): Lazy<T> =
    lazy { getViewModel<T>() }

inline fun <reified T : ViewModel> androidx.fragment.app.Fragment.lazyViewModel(): Lazy<T> = lazy { getViewModel<T>() }