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
package jahirfiquitiva.libs.kauextensions.ui.callbacks

import android.app.Activity
import android.support.v4.app.ActivityCompat
import jahirfiquitiva.libs.kauextensions.extensions.konfigs

interface PermissionRequestListener {
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        activity.konfigs.setPermissionRequested(permission)
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
    
    fun onShowPermissionInformation(permission: String)
    @Deprecated("", ReplaceWith("onShowPermissionInformation"))
    fun onShowInformation(permission: String = "") = onShowPermissionInformation(permission)
    
    fun onPermissionDenied(permission: String)
    @Deprecated("", ReplaceWith("onPermissionDenied"))
    fun onPermissionCompletelyDenied(permission: String = "") = onPermissionDenied(permission)
    
    fun onPermissionGranted(permission: String)
}