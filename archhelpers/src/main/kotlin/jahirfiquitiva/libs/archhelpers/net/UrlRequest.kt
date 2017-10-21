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
package jahirfiquitiva.libs.archhelpers.net

import jahirfiquitiva.libs.archhelpers.AHL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

open class UrlRequest<T>(private val url: String, private val debug: Boolean = false) {
    private fun internalRequest(): ResponseBody? {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                return response?.body()
            } catch (ignored: Exception) {
                if (debug) AHL.e("Error! ${ignored.message}")
            } finally {
                response?.close()
            }
        } catch (ignored: Exception) {
            if (debug) AHL.e("Error! ${ignored.message}")
        }
        return null
    }
    
    fun request(def: T, thenDo: (ResponseBody) -> T): T {
        val body = internalRequest()
        body?.let { return thenDo(it) } ?: return def
    }
}