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
package jahirfiquitiva.libs.kauextensions.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import jahirfiquitiva.libs.kauextensions.R
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.getSharedPrefs

open class Konfigurations(name: String, private val context: Context) {
    val prefs: SharedPreferences = context.getSharedPrefs(name)
    @SuppressLint("CommitPrefEdits")
    val prefsEditor: SharedPreferences.Editor = prefs.edit()
    
    companion object {
        fun newInstance(name: String, context: Context) = Konfigurations(name, context)
    }
    
    var isFirstRun: Boolean
        get() = prefs.getBoolean(IS_FIRST_RUN, true)
        set(value) = prefsEditor.putBoolean(IS_FIRST_RUN, value).apply()
    
    var currentTheme: Int
        get() = prefs.getInt(THEME, context.getInteger(R.integer.default_theme))
        set(theme) = prefsEditor.putInt(THEME, theme).apply()
    
    var hasColoredNavbar: Boolean
        get() = prefs.getBoolean(COLORED_NAVBAR, true)
        set(colored) = prefsEditor.putBoolean(COLORED_NAVBAR, colored).apply()
    
    var lastVersion: Int
        get() = prefs.getInt(LAST_VERSION, -1)
        set(lastVersion) = prefsEditor.putInt(LAST_VERSION, lastVersion).apply()
    
    var animationsEnabled: Boolean
        get() = prefs.getBoolean(ANIMATIONS_ENABLED, true)
        set(value) = prefsEditor.putBoolean(ANIMATIONS_ENABLED, value).apply()
    
    fun getPermissionRequested(permission: String): Boolean =
            prefs.getBoolean(buildPermissionRequestKey(permission), false)
    
    fun setPermissionRequested(permission: String, requested: Boolean) {
        prefsEditor.putBoolean(buildPermissionRequestKey(permission), requested).apply()
    }
    
    private fun buildPermissionRequestKey(permission: String): String =
            "requested_${permission.toLowerCase()}"
}