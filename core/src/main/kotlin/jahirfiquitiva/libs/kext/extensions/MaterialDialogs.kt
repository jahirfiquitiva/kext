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
package jahirfiquitiva.libs.kext.extensions

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.MultiChoiceListener
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice

inline fun Context.mdDialog(config: MaterialDialog.() -> Unit = {}): MaterialDialog {
    val builder = MaterialDialog(this)
    builder.config()
    return builder
}

val MaterialDialog.customView: View?
    get() = getCustomView()

fun <T> MaterialDialog.items(
    items: Array<T>?,
    disabledIndices: IntArray? = null,
    waitForPositiveButton: Boolean = true,
    listener: ItemListener = { _, _, _ -> }
                            ): MaterialDialog =
    listItems(
        items = items?.map { "$it" },
        disabledIndices = disabledIndices,
        waitForPositiveButton = waitForPositiveButton,
        selection = listener)

fun <T> MaterialDialog.items(
    items: ArrayList<T>?,
    disabledIndices: IntArray? = null,
    waitForPositiveButton: Boolean = true,
    listener: ItemListener = { _, _, _ -> }
                            ): MaterialDialog =
    listItems(
        items = items?.map { "$it" },
        disabledIndices = disabledIndices,
        waitForPositiveButton = waitForPositiveButton,
        selection = listener)

fun <T> MaterialDialog.itemsSingleChoice(
    items: Array<T>?,
    initialSelection: Int = 0,
    disabledIndices: IntArray? = null,
    waitForPositiveButton: Boolean = true,
    listener: SingleChoiceListener = { _, _, _ -> }
                                        ): MaterialDialog =
    listItemsSingleChoice(
        items = items?.map { "$it" },
        initialSelection = initialSelection,
        disabledIndices = disabledIndices,
        waitForPositiveButton = waitForPositiveButton,
        selection = listener)

fun <T> MaterialDialog.itemsSingleChoice(
    items: ArrayList<T>?,
    initialSelection: Int = 0,
    disabledIndices: IntArray? = null,
    waitForPositiveButton: Boolean = true,
    listener: SingleChoiceListener = { _, _, _ -> }
                                        ): MaterialDialog =
    listItemsSingleChoice(
        items = items?.map { "$it" },
        initialSelection = initialSelection,
        disabledIndices = disabledIndices,
        waitForPositiveButton = waitForPositiveButton,
        selection = listener)

fun <T> MaterialDialog.itemsMultiChoice(
    items: Array<T>?,
    initialSelection: IntArray = intArrayOf(),
    disabledIndices: IntArray? = null,
    waitForPositiveButton: Boolean = true,
    allowEmptySelection: Boolean = false,
    listener: MultiChoiceListener = { _, _, _ -> }
                                       ): MaterialDialog =
    listItemsMultiChoice(
        items = items?.map { "$it" },
        initialSelection = initialSelection,
        disabledIndices = disabledIndices,
        waitForPositiveButton = waitForPositiveButton,
        allowEmptySelection = allowEmptySelection,
        selection = listener)

fun <T> MaterialDialog.itemsMultiChoice(
    items: ArrayList<T>?,
    initialSelection: IntArray = intArrayOf(),
    disabledIndices: IntArray? = null,
    waitForPositiveButton: Boolean = true,
    allowEmptySelection: Boolean = false,
    listener: MultiChoiceListener = { _, _, _ -> }
                                       ): MaterialDialog =
    listItemsMultiChoice(
        items = items?.map { "$it" },
        initialSelection = initialSelection,
        disabledIndices = disabledIndices,
        waitForPositiveButton = waitForPositiveButton,
        allowEmptySelection = allowEmptySelection,
        selection = listener)
