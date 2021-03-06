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
package jahirfiquitiva.libs.kext.extensions

import android.content.Context
import android.os.Build
import android.text.Html
import androidx.annotation.StringRes

fun Context.formatHtml(@StringRes res: Int): CharSequence = string(res).formatHtml()

@Suppress("DEPRECATION")
fun String.formatHtml(): CharSequence =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this)
    }

fun String.hasContent() = isNotBlank() && isNotEmpty()
fun CharSequence.hasContent() = toString().hasContent()

fun String.formatCorrectly() =
    replace("[^\\w\\s]+".toRegex(), " ").trim().replace(" +".toRegex(), " ")
        .replace("\\p{Z}".toRegex(), "_")

fun String.toTitleCase(): String {
    val titleCase = StringBuilder()
    var nextTitleCase = true
    for (c in toLowerCase().toCharArray()) {
        when {
            Character.isSpaceChar(c) -> {
                nextTitleCase = true
                titleCase.append(c)
            }
            nextTitleCase -> {
                nextTitleCase = false
                titleCase.append(Character.toTitleCase(c))
            }
            else -> titleCase.append(c)
        }
    }
    return titleCase.toString()
}
