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
package jahirfiquitiva.libs.archhelpers.ui.adapters.presenters

interface ListAdapterPresenter<T> {
    fun addAll(newItems: ArrayList<T>)
    fun clearList()
    fun setItems(newItems: ArrayList<T>)
    fun addItem(newItem: T)
    fun removeItem(item: T)
    fun updateItem(item: T)
    
    operator fun get(index: Int): T
    
    operator fun plus(newItems: ArrayList<T>) {
        addAll(newItems)
    }
    
    operator fun plusAssign(newItem: T) {
        addItem(newItem)
    }
    
    operator fun minusAssign(item: T) {
        removeItem(item)
    }
}