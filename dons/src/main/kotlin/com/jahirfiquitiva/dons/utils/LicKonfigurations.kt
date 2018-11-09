/*
 * Copyright (c) 2018.
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
package com.jahirfiquitiva.dons.utils

import android.content.Context
import jahirfiquitiva.libs.kext.helpers.Prefs

@Deprecated("LicKonfigurations is a long and with-typo name.", ReplaceWith("LicPrefs"))
typealias LicKonfigurations = LicPrefs

open class LicPrefs(name: String, context: Context) : Prefs(name, context) {
    
    var functional: Boolean
        get() = prefs.getBoolean(FUNCTIONAL_APP, defaultFunctionalState())
        set(value) = prefsEditor.putBoolean(FUNCTIONAL_APP, value).apply()
    
    open fun defaultFunctionalState(): Boolean = false
    
    companion object {
        private const val FUNCTIONAL_APP = "functional_app"
    }
}