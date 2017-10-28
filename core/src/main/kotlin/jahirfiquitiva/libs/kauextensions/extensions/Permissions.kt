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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import ca.allanwang.kau.utils.buildIsMarshmallowAndUp

fun Context.shouldRequestPermission(which: String): Boolean {
    if (buildIsMarshmallowAndUp) {
        val permissionResult = ActivityCompat.checkSelfPermission(this, which)
        return permissionResult != PackageManager.PERMISSION_GRANTED
    }
    return false
}

@SuppressLint("NewApi")
fun Activity.requestSinglePermission(
        permission: String, requestCode: Int,
        listener: PermissionRequestListener
                                    ) {
    if (shouldRequestPermission(permission)) {
        // Permission has not been granted
        if (shouldShowRequestPermissionRationale(permission)) {
            // Permission needs some explanation
            listener.onShowInformation(permission)
        } else {
            if (!konfigs.getPermissionRequested(permission)) {
                // Request permission
                listener.onPermissionRequest(this@requestSinglePermission, permission, requestCode)
            } else {
                // Handle the feature without permission or ask user to manually allow it
                listener.onPermissionCompletelyDenied()
            }
        }
        konfigs.setPermissionRequested(permission, true)
    } else {
        listener.onPermissionGranted()
    }
}

abstract class PermissionRequestListener {
    open fun onPermissionRequest(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
    
    abstract fun onShowInformation(permission: String)
    abstract fun onPermissionCompletelyDenied()
    abstract fun onPermissionGranted()
}