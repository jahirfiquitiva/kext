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
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.kauextensions.extensions

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

fun Activity.setupStatusBarStyle(translucent:Boolean = true,
                                 lightMode:Boolean = primaryDarkColor.isColorLight()) {
    if (Build.VERSION.SDK_INT >= 21) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
    if (Build.VERSION.SDK_INT >= 19) {
        val params:WindowManager.LayoutParams = window.attributes
        if (translucent) {
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        } else {
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        }
        window.attributes = params
    }
    if (Build.VERSION.SDK_INT >= 21) {
        window.statusBarColor = Color.TRANSPARENT
        setStatusBarMode(lightMode)
    }
}

fun Activity.setStatusBarMode(lightMode:Boolean = primaryDarkColor.isColorLight()) {
    val view = window.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = view.systemUiVisibility
        if (lightMode) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        view.systemUiVisibility = flags
    }
}

fun Activity.restart() {
    val intnt = this.intent
    intnt.removeCategory(Intent.CATEGORY_LAUNCHER)
    // TODO: Fade activity restart
    startActivity(intnt)
    finish()
}

/*
fun ReleasesActivity.showChangelog(currVersion:Int, xmlRes:Int,
                                   callback:OnChangelogNeutralButtonClick? = null) {
    if (isFirstRunEver()) {
        konfig.lastVersion = currVersion
        return
    }
    if (konfig.lastVersion != currVersion) {
        ChangelogDialog.show(this, xmlRes, callback)
    }
    konfig.lastVersion = currVersion
}
*/