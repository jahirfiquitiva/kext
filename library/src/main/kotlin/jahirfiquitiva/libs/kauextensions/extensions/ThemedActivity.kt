/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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

import ca.allanwang.kau.utils.navigationBarColor
import jahirfiquitiva.libs.kauextensions.activities.ThemedActivity
import jahirfiquitiva.libs.kauextensions.utils.AMOLED
import jahirfiquitiva.libs.kauextensions.utils.AUTO_AMOLED
import jahirfiquitiva.libs.kauextensions.utils.AUTO_DARK
import jahirfiquitiva.libs.kauextensions.utils.DARK
import jahirfiquitiva.libs.kauextensions.utils.LIGHT
import java.util.*


fun ThemedActivity.setCustomTheme() {
    val enterAnimation = android.R.anim.fade_in
    val exitAnimation = android.R.anim.fade_out
    overridePendingTransition(enterAnimation, exitAnimation)
    setTheme(getCustomTheme())
    navigationBarColor = getNavbarColor()
}

fun ThemedActivity.isDarkTheme():Boolean {
    val c = Calendar.getInstance()
    val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
    when (konfigs.currentTheme) {
        LIGHT -> return false
        DARK, AMOLED -> return true
        AUTO_DARK, AUTO_AMOLED -> return hourOfDay !in 7..18
        else -> return false
    }
}

fun ThemedActivity.getCustomTheme():Int {
    val c = Calendar.getInstance()
    val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
    when (konfigs.currentTheme) {
        LIGHT -> return lightTheme()
        DARK -> return darkTheme()
        AMOLED -> return amoledTheme()
        AUTO_DARK -> return if (hourOfDay in 7..18) lightTheme() else darkTheme()
        AUTO_AMOLED -> return if (hourOfDay in 7..18) lightTheme() else amoledTheme()
        else -> return lightTheme()
    }
}

fun ThemedActivity.getNavbarColor():Int {
    if (konfigs.currentTheme == AMOLED)
        return getColorFromRes(android.R.color.black)
    else if (konfigs.hasColoredNavbar)
        return navigationBarColor
    else
        return getColorFromRes(android.R.color.black)
}