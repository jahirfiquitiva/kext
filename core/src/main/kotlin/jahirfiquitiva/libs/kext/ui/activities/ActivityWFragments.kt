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
package jahirfiquitiva.libs.kext.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.fragment.app.Fragment
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.helpers.Prefs

abstract class ActivityWFragments<P : Prefs> : ThemedActivity<P>() {
    open fun fragmentsContainer() = 0
    open fun reportResultToFragment() = false
    
    @SuppressLint("PrivateResource")
    open fun changeFragment(f: Fragment, tag: String? = null) {
        if (fragmentsContainer() == 0) return
        try {
            val manager = supportFragmentManager.beginTransaction()
            if (prefs.animationsEnabled) {
                manager.setCustomAnimations(
                    R.anim.abc_fade_in, R.anim.abc_fade_out,
                    R.anim.abc_popup_enter, R.anim.abc_popup_exit)
            }
            if (tag != null) manager.replace(fragmentsContainer(), f, tag)
            else manager.replace(fragmentsContainer(), f)
            manager.commit()
        } catch (ignored: Exception) {
        }
    }
    
    @Suppress("MemberVisibilityCanBePrivate")
    fun getCurrentFragment(): Fragment? {
        return try {
            supportFragmentManager?.findFragmentById(fragmentsContainer())
        } catch (e: Exception) {
            null
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (reportResultToFragment())
            getCurrentFragment()?.onActivityResult(requestCode, resultCode, data)
    }
}
