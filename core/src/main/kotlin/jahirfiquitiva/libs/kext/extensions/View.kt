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

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

fun View.buildSnackbar(
    @StringRes text: Int, duration: Int = Snackbar.LENGTH_SHORT,
    builder: Snackbar.() -> Unit = {}
                      ): Snackbar {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.builder()
    return snackbar
}

fun View.buildSnackbar(
    text: String, duration: Int = Snackbar.LENGTH_SHORT,
    builder: Snackbar.() -> Unit = {}
                      ): Snackbar {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.builder()
    return snackbar
}

fun View.clearChildrenAnimations() {
    clearAnimation()
    getAllChildren(this).forEach { it.clearAnimation() }
}

private fun getAllChildren(v: View): ArrayList<View> {
    if (v !is ViewGroup) {
        val viewArrayList = ArrayList<View>()
        viewArrayList.add(v)
        return viewArrayList
    }
    val result = ArrayList<View>()
    for (i in 0 until v.childCount) {
        val child = v.getChildAt(i)
        val viewArrayList = ArrayList<View>()
        viewArrayList.add(v)
        viewArrayList.addAll(getAllChildren(child))
        result.addAll(viewArrayList)
    }
    return result
}

fun RecyclerView.Adapter<*>.isEmpty(): Boolean = itemCount <= 0

fun View.postDelayed(delay: Long, action: () -> Unit) =
    postDelayed(action, delay)