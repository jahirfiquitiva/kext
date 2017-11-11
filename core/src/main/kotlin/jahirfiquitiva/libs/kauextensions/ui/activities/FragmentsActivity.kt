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

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import jahirfiquitiva.libs.kauextensions.R
import jahirfiquitiva.libs.kauextensions.extensions.konfigs

abstract class FragmentsActivity : ThemedActivity() {
    open fun fragmentsContainer(): Int = 0
    
    @SuppressLint("PrivateResource")
    open fun changeFragment(f: Fragment, tag: String? = null) {
        if (fragmentsContainer() == 0) return
        try {
            val manager = supportFragmentManager.beginTransaction()
            if (konfigs.animationsEnabled) {
                manager.setCustomAnimations(
                        R.anim.abc_fade_in, R.anim.abc_fade_out,
                        R.anim.abc_popup_enter, R.anim.abc_popup_exit)
            }
            if (tag != null) manager.replace(fragmentsContainer(), f, tag)
            else manager.replace(fragmentsContainer(), f)
            manager.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}