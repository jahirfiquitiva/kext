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
package jahirfiquitiva.libs.kext.extensions

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.view.Menu
import android.view.MenuItem

@Deprecated("Use Menu.setItemVisibility() instead", ReplaceWith("setItemVisibility()"))
fun Menu.changeOptionVisibility(id: Int, visible: Boolean) {
    setItemVisibility(id, visible)
}

fun Menu.setItemVisibility(id: Int, visible: Boolean) {
    findItem(id)?.isVisible = visible
}

fun Menu.setItemTitle(id: Int, title: String) {
    findItem(id)?.title = title
}

@Deprecated("Use Menu.setItemIcon() instead", ReplaceWith("setItemIcon()"))
fun Menu.setOptionIcon(id: Int, @DrawableRes iconRes: Int) {
    setItemIcon(id, iconRes)
}

@Deprecated("Use Menu.setItemIcon() instead", ReplaceWith("setItemIcon()"))
fun Menu.setOptionIcon(id: Int, icon: Drawable) {
    setItemIcon(id, icon)
}

fun Menu.setItemIcon(id: Int, @DrawableRes iconRes: Int) {
    findItem(id)?.setIcon(iconRes)
}

fun Menu.setItemIcon(id: Int, icon: Drawable) {
    findItem(id)?.icon = icon
}

fun Menu.getItems(): ArrayList<MenuItem?> {
    val items = ArrayList<MenuItem?>()
    for (i in 0 until size()) {
        items += getItem(i)
    }
    return items
}

fun Menu.showAllItems() {
    getItems().forEach { it?.show() }
}

fun Menu.hideAllItems() {
    getItems().forEach { it?.hide() }
}

fun MenuItem.show() {
    isVisible = true
}

fun MenuItem.hide() {
    isVisible = false
}