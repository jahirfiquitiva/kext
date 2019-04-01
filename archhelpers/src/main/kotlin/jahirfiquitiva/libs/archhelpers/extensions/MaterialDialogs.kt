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
package jahirfiquitiva.libs.archhelpers.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import jahirfiquitiva.libs.kext.extensions.mdDialog

inline fun AppCompatActivity.mdDialog(config: MaterialDialog.() -> Unit = {}): MaterialDialog {
    return mdDialog {
        config()
        lifecycleOwner(this@mdDialog)
    }
}

inline fun Fragment.mdDialog(config: MaterialDialog.() -> Unit = {}): MaterialDialog? {
    return context?.mdDialog {
        config()
        lifecycleOwner(this@mdDialog)
    }
}
