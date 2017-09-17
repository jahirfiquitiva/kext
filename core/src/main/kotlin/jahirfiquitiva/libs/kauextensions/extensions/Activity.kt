/*
 * Copyright (c) 2017. Jahir Fiquitiva
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

package jahirfiquitiva.libs.kauextensions.extensions

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.support.annotation.IdRes
import android.view.View
import android.view.WindowManager
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.utils.statusBarLight

/**
 * Credits: Lorenzo Quiroli - Roberto Orgiu
 * https://medium.com/@quiro91/improving-findviewbyid-with-kotlin-4cf2f8f779bb
 */
fun <T:View> Activity.bind(@IdRes res:Int):Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazyAndroid { findViewById(res) as T }
}

fun <T:View> View.bind(@IdRes res:Int):Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazyAndroid { findViewById(res) as T }
}

/**
 * Credits: Juan Ignacio Saravia
 * https://proandroiddev.com/kotlin-faster-lazy-for-android-7328ec8d8d57
 */
fun <T> lazyAndroid(initializer:() -> T):Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

@Deprecated("Use \'enableTranslucentStatusBar\'", ReplaceWith("enableTranslucentStatusBar"))
fun Activity.setupStatusBarStyle(translucent:Boolean = true,
                                 lightMode:Boolean = primaryDarkColor.isColorLight()) {
    enableTranslucentStatusBar(translucent)
    statusBarLight = lightMode
}

fun Activity.enableTranslucentStatusBar(enable:Boolean = true) {
    if (Build.VERSION.SDK_INT >= 21) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
    if (Build.VERSION.SDK_INT >= 19) {
        val params:WindowManager.LayoutParams = window.attributes
        if (enable) {
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        } else {
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        }
        window.attributes = params
    }
    if (Build.VERSION.SDK_INT >= 21) {
        statusBarColor = Color.TRANSPARENT
    }
}