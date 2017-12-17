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
package jahirfiquitiva.libs.kauextensions.extensions

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.string(tag: String, default: String = ""): String =
        optString(tag, default) ?: default

fun JSONObject.boolean(tag: String, default: Boolean = false): Boolean = optBoolean(tag, default)

fun JSONObject.int(tag: String, default: Int = 0): Int = optInt(tag, default)

fun JSONObject.double(tag: String, default: Double = 0.0): Double = optDouble(tag, default)

fun JSONObject.long(tag: String, default: Long = 0L): Long = optLong(tag, default)

fun JSONObject.obj(tag: String): JSONObject? = optJSONObject(tag) ?: null

fun JSONObject.array(tag: String): JSONArray? = optJSONArray(tag) ?: null

fun JSONArray.string(index: Int, default: String = ""): String =
        optString(index, default) ?: default

fun JSONArray.boolean(index: Int, default: Boolean = false): Boolean = optBoolean(index, default)

fun JSONArray.int(index: Int, default: Int = 0): Int = optInt(index, default)

fun JSONArray.double(index: Int, default: Double = 0.0): Double = optDouble(index, default)

fun JSONArray.long(index: Int, default: Long = 0L): Long = optLong(index, default)

fun JSONArray.obj(index: Int): JSONObject? = optJSONObject(index) ?: null

fun JSONArray.array(index: Int): JSONArray? = optJSONArray(index) ?: null

inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    if (length() <= 0) return
    for (i in 0 until length()) {
        action(getJSONObject(i))
    }
}