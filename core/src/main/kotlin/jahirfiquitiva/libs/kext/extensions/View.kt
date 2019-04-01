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

package jahirfiquitiva.libs.kext.extensions

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import jahirfiquitiva.libs.kext.R

fun View.buildSnackbar(
    @StringRes text: Int,
    @ColorInt textColor: Int = Color.WHITE,
    @ColorInt actionColor: Int = context.accentColor,
    margin: Int = 0,
    bottomMargin: Int = margin,
    duration: Int = Snackbar.LENGTH_SHORT,
    builder: Snackbar.() -> Unit = {}
                      ): Snackbar {
    return buildSnackbar(
        context.string(text), textColor, actionColor, margin, bottomMargin, duration, builder)
}

fun View.buildSnackbar(
    text: String,
    @ColorInt textColor: Int = Color.WHITE,
    @ColorInt actionColor: Int = context.accentColor,
    margin: Int = 0,
    bottomMargin: Int = margin,
    duration: Int = Snackbar.LENGTH_SHORT,
    builder: Snackbar.() -> Unit = {}
                      ): Snackbar {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.builder()
    
    val snackText: TextView? by snackbar.view.bind(R.id.snackbar_text)
    snackText?.setTextColor(textColor)
    snackText?.maxLines = 3
    
    snackbar.setActionTextColor(actionColor)
    
    try {
        val params = snackbar.view.layoutParams as? ViewGroup.MarginLayoutParams
        params?.setMargins(
            params.leftMargin + margin,
            params.topMargin,
            params.rightMargin + margin,
            params.bottomMargin + bottomMargin)
        snackbar.view.layoutParams = params
    } catch (e: Exception) {
    }
    
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

fun View.postDelayed(delay: Long, action: () -> Unit) =
    postDelayed(action, delay)

val EditText.content: String
    get() = text?.toString() ?: ""
