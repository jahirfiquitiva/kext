/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.customtabs.CustomTabsSession
import ca.allanwang.kau.utils.startLink

fun Context.openLink(link:String) {
    val mClient = arrayOfNulls<CustomTabsClient>(1)
    val mCustomTabsSession = arrayOfNulls<CustomTabsSession>(1)
    val mCustomTabsServiceConnection:CustomTabsServiceConnection
    val customTabsIntent:CustomTabsIntent

    mCustomTabsServiceConnection = object:CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(componentName:ComponentName,
                                                  customTabsClient:CustomTabsClient) {
            mClient[0] = customTabsClient
            mClient[0]?.warmup(0L)
            mCustomTabsSession[0] = mClient[0]?.newSession(null)
        }

        override fun onServiceDisconnected(name:ComponentName) {
            mClient[0] = null
        }
    }
    CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", mCustomTabsServiceConnection)
    customTabsIntent = CustomTabsIntent.Builder(mCustomTabsSession[0])
            .setToolbarColor(primaryColor)
            .setShowTitle(true)
            .build()

    try {
        customTabsIntent.launchUrl(this, Uri.parse(link))
    } catch (ex:Exception) {
        startLink(link)
    }
}