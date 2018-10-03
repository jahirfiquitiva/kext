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
package jahirfiquitiva.libs.kext.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.helpers.AMOLED
import jahirfiquitiva.libs.kext.helpers.AUTO_AMOLED
import jahirfiquitiva.libs.kext.helpers.AUTO_DARK
import jahirfiquitiva.libs.kext.helpers.DARK
import jahirfiquitiva.libs.kext.helpers.LIGHT
import jahirfiquitiva.libs.kext.helpers.TRANSPARENT
import jahirfiquitiva.libs.kext.ui.ThemeKey
import jahirfiquitiva.libs.kext.ui.activities.ThemedActivity
import java.util.Calendar

/**
 * BottomSheetDialog fragment that uses a custom
 * theme which sets a rounded background to the dialog
 * and doesn't dim the navigation bar
 *
 * Credits: https://gist.github.com/ArthurNagy/1c4a64e6c8a7ddfca58638a9453e4aed
 *
 */
open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {
    
    override fun getTheme(): Int {
        @ThemeKey val currentTheme = (activity as? ThemedActivity<*>)?.getThemeKey() ?: LIGHT
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        return when (currentTheme) {
            LIGHT -> styleForLightTheme
            DARK -> styleForDarkTheme
            AMOLED, TRANSPARENT -> styleForAmoledTheme
            AUTO_DARK -> if (hourOfDay in 7..18) styleForLightTheme else styleForDarkTheme
            AUTO_AMOLED -> if (hourOfDay in 7..18) styleForLightTheme else styleForAmoledTheme
            else -> styleForLightTheme
        }
    }
    
    @StyleRes open val styleForLightTheme: Int = R.style.BottomSheetDialog_Light
    @StyleRes open val styleForDarkTheme: Int = R.style.BottomSheetDialog_Dark
    @StyleRes open val styleForAmoledTheme: Int = R.style.BottomSheetDialog_Amoled
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)
}