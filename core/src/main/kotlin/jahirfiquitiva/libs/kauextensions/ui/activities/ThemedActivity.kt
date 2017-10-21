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
package jahirfiquitiva.libs.kauextensions.ui.activities

import android.content.Intent
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
import jahirfiquitiva.libs.kauextensions.helpers.AMOLED
import jahirfiquitiva.libs.kauextensions.helpers.AUTO_AMOLED
import jahirfiquitiva.libs.kauextensions.helpers.AUTO_DARK
import jahirfiquitiva.libs.kauextensions.helpers.DARK
import jahirfiquitiva.libs.kauextensions.helpers.LIGHT
import jahirfiquitiva.libs.kauextensions.helpers.TRANSPARENT
import java.util.*

abstract class ThemedActivity : AppCompatActivity() {
    private var lastTheme = 0
    private var coloredNavbar = false
    
    @StyleRes
    abstract fun lightTheme(): Int
    
    @StyleRes
    abstract fun darkTheme(): Int
    
    @StyleRes
    abstract fun amoledTheme(): Int
    
    @StyleRes
    abstract fun transparentTheme(): Int
    
    abstract fun autoStatusBarTint(): Boolean
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setCustomTheme()
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    
    override fun onResume() {
        super.onResume()
        if (lastTheme != konfigs.currentTheme || coloredNavbar != konfigs.hasColoredNavbar)
            onThemeChanged()
    }
    
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        lastTheme = konfigs.currentTheme
        coloredNavbar = konfigs.hasColoredNavbar
    }
    
    fun onThemeChanged() {
        postRecreate()
    }
    
    private fun postRecreate() {
        Handler().post(
                {
                    val i = Intent(this, this::class.java)
                    intent?.extras?.let { i.putExtras(it) }
                    startActivity(i)
                    overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    finish()
                    overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)
                })
    }
    
    private fun setCustomTheme() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setTheme(getCustomTheme())
        statusBarColor = primaryDarkColor
        if (autoStatusBarTint()) statusBarLight = primaryDarkColor.isColorLight
        navigationBarColor = getCorrectNavbarColor()
    }
    
    val isDarkTheme: Boolean
        get() {
            val c = Calendar.getInstance()
            val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
            return when (konfigs.currentTheme) {
                LIGHT -> false
                DARK, AMOLED -> true
                AUTO_DARK, AUTO_AMOLED -> hourOfDay !in 7..18
                else -> false
            }
        }
    
    @StyleRes
    private fun getCustomTheme(): Int {
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        return when (konfigs.currentTheme) {
            LIGHT -> lightTheme()
            DARK -> darkTheme()
            AMOLED -> amoledTheme()
            TRANSPARENT -> transparentTheme()
            AUTO_DARK -> if (hourOfDay in 7..18) lightTheme() else darkTheme()
            AUTO_AMOLED -> if (hourOfDay in 7..18) lightTheme() else amoledTheme()
            else -> lightTheme()
        }
    }
    
    @ColorInt
    private fun getCorrectNavbarColor(): Int = when {
        konfigs.currentTheme == AMOLED -> getColorFromRes(android.R.color.black)
        konfigs.hasColoredNavbar -> primaryDarkColor
        else -> getColorFromRes(android.R.color.black)
    }
}