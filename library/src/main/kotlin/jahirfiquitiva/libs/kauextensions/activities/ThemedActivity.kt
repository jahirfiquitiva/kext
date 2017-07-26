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

package jahirfiquitiva.libs.kauextensions.activities

import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.annotation.StyleRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.utils.statusBarLight
import jahirfiquitiva.libs.kauextensions.extensions.getColorFromRes
import jahirfiquitiva.libs.kauextensions.extensions.isColorLight
import jahirfiquitiva.libs.kauextensions.extensions.konfigs
import jahirfiquitiva.libs.kauextensions.extensions.primaryDarkColor
import jahirfiquitiva.libs.kauextensions.utils.AMOLED
import jahirfiquitiva.libs.kauextensions.utils.AUTO_AMOLED
import jahirfiquitiva.libs.kauextensions.utils.AUTO_DARK
import jahirfiquitiva.libs.kauextensions.utils.DARK
import jahirfiquitiva.libs.kauextensions.utils.LIGHT
import jahirfiquitiva.libs.kauextensions.utils.TRANSPARENT
import java.util.*

abstract class ThemedActivity:AppCompatActivity() {
    private var lastTheme = 0
    private var coloredNavbar = false

    @StyleRes
    abstract fun lightTheme():Int

    @StyleRes
    abstract fun darkTheme():Int

    @StyleRes
    abstract fun amoledTheme():Int

    @StyleRes
    abstract fun transparentTheme():Int

    abstract fun autoStatusBarTint():Boolean

    override fun onCreate(savedInstanceState:Bundle?) {
        setCustomTheme()
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        if (lastTheme != konfigs.currentTheme || coloredNavbar != konfigs.hasColoredNavbar)
            onThemeChanged()
    }

    override fun onPostCreate(savedInstanceState:Bundle?) {
        super.onPostCreate(savedInstanceState)
        lastTheme = konfigs.currentTheme
        coloredNavbar = konfigs.hasColoredNavbar
    }

    fun onThemeChanged() {
        postRecreate()
    }

    fun postRecreate() {
        Handler().post({ recreate() })
    }

    private fun setCustomTheme() {
        val enterAnimation = android.R.anim.fade_in
        val exitAnimation = android.R.anim.fade_out
        overridePendingTransition(enterAnimation, exitAnimation)
        setTheme(getCustomTheme())
        statusBarColor = primaryDarkColor
        if (autoStatusBarTint()) statusBarLight = primaryDarkColor.isColorLight
        navigationBarColor = getCorrectNavbarColor()
    }

    val isDarkTheme:Boolean
        get() {
            val c = Calendar.getInstance()
            val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
            when (konfigs.currentTheme) {
                LIGHT -> return false
                DARK, AMOLED -> return true
                AUTO_DARK, AUTO_AMOLED -> return hourOfDay !in 7..18
                else -> return false
            }
        }

    @StyleRes
    private fun getCustomTheme():Int {
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        when (konfigs.currentTheme) {
            LIGHT -> return lightTheme()
            DARK -> return darkTheme()
            AMOLED -> return amoledTheme()
            TRANSPARENT -> return transparentTheme()
            AUTO_DARK -> return if (hourOfDay in 7..18) lightTheme() else darkTheme()
            AUTO_AMOLED -> return if (hourOfDay in 7..18) lightTheme() else amoledTheme()
            else -> return lightTheme()
        }
    }

    @ColorInt
    private fun getCorrectNavbarColor():Int {
        if (konfigs.currentTheme == AMOLED)
            return getColorFromRes(android.R.color.black)
        else if (konfigs.hasColoredNavbar)
            return primaryDarkColor
        else
            return getColorFromRes(android.R.color.black)
    }

}