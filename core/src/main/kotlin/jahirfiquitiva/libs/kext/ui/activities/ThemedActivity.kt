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
package jahirfiquitiva.libs.kext.ui.activities

import android.app.ActivityManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.restart
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.utils.statusBarLight
import ca.allanwang.kau.utils.withAlpha
import jahirfiquitiva.libs.kext.extensions.getAppIconResId
import jahirfiquitiva.libs.kext.extensions.isColorLight
import jahirfiquitiva.libs.kext.extensions.navigationBarLight
import jahirfiquitiva.libs.kext.extensions.primaryColor
import jahirfiquitiva.libs.kext.extensions.primaryDarkColor
import jahirfiquitiva.libs.kext.helpers.AMOLED
import jahirfiquitiva.libs.kext.helpers.AUTO_AMOLED
import jahirfiquitiva.libs.kext.helpers.AUTO_DARK
import jahirfiquitiva.libs.kext.helpers.DARK
import jahirfiquitiva.libs.kext.helpers.LIGHT
import jahirfiquitiva.libs.kext.helpers.Prefs
import jahirfiquitiva.libs.kext.helpers.TRANSPARENT
import jahirfiquitiva.libs.kext.ui.ThemeKey
import java.util.Calendar

abstract class ThemedActivity<out P : Prefs> : AppCompatActivity() {
    private var lastTheme = 0
    private var coloredNavbar = false
    
    @StyleRes
    abstract fun lightTheme(): Int
    
    @StyleRes
    abstract fun darkTheme(): Int
    
    @StyleRes
    open fun amoledTheme(): Int = 0
    
    @StyleRes
    open fun transparentTheme(): Int = 0
    
    open fun autoTintStatusBar(): Boolean = true
    open fun autoTintNavigationBar(): Boolean = true
    
    @Deprecated("Configs has been renamed to prefs", ReplaceWith("prefs"))
    val configs
        get() = prefs
    
    abstract val prefs: P
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setCustomTheme()
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    
    override fun onResume() {
        super.onResume()
        if (lastTheme != prefs.currentTheme || coloredNavbar != prefs.hasColoredNavbar)
            onThemeChanged()
    }
    
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        lastTheme = prefs.currentTheme
        coloredNavbar = prefs.hasColoredNavbar
    }
    
    @Suppress("MemberVisibilityCanBePrivate")
    fun onThemeChanged() {
        postRecreate()
    }
    
    private fun postRecreate() {
        Handler().post { restart() }
    }
    
    @Suppress("DEPRECATION")
    private fun setCustomTheme() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setTheme(getCustomTheme())
        statusBarColor = primaryDarkColor
        if (autoTintStatusBar()) statusBarLight = primaryDarkColor.isColorLight
        val navColor = getCorrectNavbarColor()
        navigationBarColor = navColor
        if (autoTintNavigationBar()) navigationBarLight = navColor.isColorLight
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && forceTintRecents()) {
            val td = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ActivityManager.TaskDescription(
                    null, getAppIconResId(packageName) ?: 0, recentsColor().withAlpha(1F))
            } else {
                ActivityManager.TaskDescription(null, null, recentsColor().withAlpha(1F))
            }
            setTaskDescription(td)
        }
    }
    
    open fun usesDarkTheme(): Boolean {
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        return when (prefs.currentTheme) {
            LIGHT -> false
            DARK, AMOLED, TRANSPARENT -> true
            AUTO_DARK, AUTO_AMOLED -> hourOfDay !in 7..18
            else -> false
        }
    }
    
    @ThemeKey
    fun getThemeKey(): Int = prefs.currentTheme
    
    @StyleRes
    private fun getCustomTheme(): Int {
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val rightAmoledTheme = if (amoledTheme() != 0) amoledTheme() else darkTheme()
        return when (prefs.currentTheme) {
            LIGHT -> lightTheme()
            DARK -> darkTheme()
            AMOLED -> rightAmoledTheme
            TRANSPARENT -> if (transparentTheme() != 0) transparentTheme() else darkTheme()
            AUTO_DARK -> if (hourOfDay in 7..18) lightTheme() else darkTheme()
            AUTO_AMOLED -> if (hourOfDay in 7..18) lightTheme() else rightAmoledTheme
            else -> lightTheme()
        }
    }
    
    @ColorInt
    private fun getCorrectNavbarColor(): Int {
        return if ((prefs.currentTheme == AMOLED || prefs.currentTheme == TRANSPARENT)
            && !forceNavBarTint()) {
            Color.parseColor("#000000")
        } else if (prefs.hasColoredNavbar) {
            primaryDarkColor
        } else {
            Color.parseColor("#000000")
        }
    }
    
    private fun doSafely(what: () -> Unit) {
        try {
            what()
        } catch (ignored: Exception) {
        }
    }
    
    open fun forceNavBarTint(): Boolean = false
    open fun forceTintRecents(): Boolean = true
    open fun recentsColor(): Int = primaryColor
}