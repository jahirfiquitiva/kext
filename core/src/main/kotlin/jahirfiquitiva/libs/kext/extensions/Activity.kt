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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.utils.statusBarLight
import ca.allanwang.kau.utils.withAlpha

inline fun <reified T : View> Activity.bind(@IdRes res: Int): Lazy<T?> = lazy { findView<T>(res) }

inline fun <reified T : View> Fragment.bind(@IdRes res: Int): Lazy<T?>? =
    lazy { findView<T>(res) }

inline fun <reified T : View> View.bind(@IdRes res: Int): Lazy<T?> = lazy { findView<T>(res) }

inline fun <reified T : View> Activity.findView(@IdRes res: Int): T? = findViewById(res)

inline fun <reified T : View> Fragment.findView(@IdRes res: Int): T? = view?.findView(res)

inline fun <reified T : View> View.findView(@IdRes res: Int): T? = findViewById(res)

@Deprecated("Use enableTranslucentStatusBar()", ReplaceWith("enableTranslucentStatusBar()"))
fun Activity.setupStatusBarStyle(
    translucent: Boolean = true,
    lightMode: Boolean = primaryDarkColor.isColorLight()
                                ) {
    enableTranslucentStatusBar(translucent)
    statusBarLight = lightMode
}

fun Activity.enableTranslucentStatusBar(enable: Boolean = true) {
    if (Build.VERSION.SDK_INT >= 21) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
    if (Build.VERSION.SDK_INT >= 19) {
        val params: WindowManager.LayoutParams = window.attributes
        if (enable) {
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        } else {
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        }
        window.attributes = params
    }
    if (Build.VERSION.SDK_INT >= 21) statusBarColor = Color.TRANSPARENT
}

inline var Activity.navigationBarLight: Boolean
    @SuppressLint("InlinedApi")
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR > 0 else false
    @SuppressLint("InlinedApi")
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                if (value) flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                else flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }

val Activity.navigationBarHeight: Int
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode) return 0
        var height = 0
        val usableSize = getUsableScreenSize()
        val realScreenSize = getRealScreenSize()
        if (usableSize.x < realScreenSize.x) {
            val point = Point(realScreenSize.x - usableSize.x, usableSize.y)
            height = point.x
        }
        if (usableSize.y < realScreenSize.y) {
            val point = Point(usableSize.x, realScreenSize.y - usableSize.y)
            height = point.y
        }
        return height
    }

@Suppress("DEPRECATION")
fun Activity.themeRecents(@ColorInt recentsColor: Int, force: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && force) {
        setTaskDescription(ActivityManager.TaskDescription(null, null, recentsColor.withAlpha(1F)))
    }
}

fun Context.getUsableScreenSize(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    val display = windowManager?.defaultDisplay
    val size = Point()
    display?.getSize(size)
    return size
}

@Suppress("DEPRECATION")
fun Context.getRealScreenSize(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    val display = windowManager?.defaultDisplay
    val size = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        display?.getRealSize(size)
    } else {
        try {
            size.x = Display::class.java.getMethod("getRawWidth").invoke(display) as? Int ?: 0
            size.y = Display::class.java.getMethod("getRawHeight").invoke(display) as? Int ?: 0
        } catch (ignored: Exception) {
            size.x = display?.width ?: 0
            size.y = display?.height ?: 0
        }
    }
    return size
}
