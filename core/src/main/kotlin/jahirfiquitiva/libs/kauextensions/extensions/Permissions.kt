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

package jahirfiquitiva.libs.kauextensions.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import ca.allanwang.kau.utils.buildIsMarshmallowAndUp
import jahirfiquitiva.libs.kauextensions.ui.callbacks.PermissionRequestListener

fun Context.isPermissionGranted(which: String): Boolean =
        ContextCompat.checkSelfPermission(this, which) == PackageManager.PERMISSION_GRANTED

fun Context.isPermissionDenied(which: String): Boolean = !isPermissionGranted(which)

fun Activity.shouldShowPermissionRationale(which: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(this, which)

fun Activity.canRequestPermission(which: String): Boolean {
    if (!konfigs.hasRequestedPermission(which)) return true
    return !(isPermissionDenied(which) && !shouldShowPermissionRationale(which))
}

@SuppressLint("NewApi")
fun Activity.requestSinglePermission(
        permission: String, requestCode: Int,
        listener: PermissionRequestListener
                                    ) {
    if (buildIsMarshmallowAndUp) {
        if (canRequestPermission(permission)) {
            if (shouldShowPermissionRationale(permission)) {
                // Permission Request needs some explanation
                listener.onShowPermissionInformation(permission)
            } else {
                // Request Permission
                listener.requestPermission(this, permission, requestCode)
            }
        } else {
            listener.onPermissionDenied(permission)
        }
    } else {
        listener.onPermissionGranted(permission)
    }
}