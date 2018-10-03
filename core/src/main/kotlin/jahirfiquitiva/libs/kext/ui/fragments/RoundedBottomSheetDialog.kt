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
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.extensions.usesDarkTheme

/**
 * BottomSheetDialog fragment that uses a custom
 * theme which sets a rounded background to the dialog
 * and doesn't dim the navigation bar
 *
 * Credits: https://gist.github.com/ArthurNagy/1c4a64e6c8a7ddfca58638a9453e4aed
 *
 */
open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {
    
    override fun getTheme(): Int =
        if (context?.usesDarkTheme == true) R.style.BottomSheetDialog_Dark
        else R.style.BottomSheetDialog_Light
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)
}