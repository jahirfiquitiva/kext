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
package jahirfiquitiva.libs.kauextensions.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.buildIsMarshmallowAndUp
import jahirfiquitiva.libs.kauextensions.extensions.canRequestPermission
import jahirfiquitiva.libs.kauextensions.extensions.konfigs
import jahirfiquitiva.libs.kauextensions.extensions.shouldShowPermissionRationale
import jahirfiquitiva.libs.kauextensions.ui.callbacks.PermissionRequestListener

abstract class SimplePermissionActivity : AppCompatActivity(), PermissionRequestListener {
    
    companion object {
        const val PERMISSION_REQUEST_CODE = 99
        const val PERMISSION_REQUEST_FROM_SETTINGS = 98
    }
    
    private var permission: String? = null
    
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
                                           ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission?.let { onPermissionGranted(it) }
            } else {
                permission?.let { onPermissionDenied(it) }
            }
        }
    }
    
    override fun requestPermission(permission: String) {
        this.permission = permission
        if (buildIsMarshmallowAndUp) {
            if (canRequestPermission(permission)) {
                if (shouldShowPermissionRationale(permission)) {
                    // Permission Request needs some explanation
                    onShowPermissionInformation(permission)
                } else {
                    // Request Permission
                    ActivityCompat.requestPermissions(
                            this, arrayOf(permission), PERMISSION_REQUEST_CODE)
                }
            } else {
                onPermissionDenied(permission)
            }
            konfigs.setPermissionRequested(permission)
        } else {
            onPermissionGranted(permission)
        }
    }
    
    override fun onShowPermissionInformation(permission: String) {}
    
    override fun onPermissionDenied(permission: String) {
        this.permission = null
    }
    
    override fun onPermissionGranted(permission: String) {
        this.permission = null
    }
    
    val settingsIntent: Intent by lazy {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
    }
    
    fun startPermissionSettings() {
        startActivityForResult(settingsIntent, PERMISSION_REQUEST_FROM_SETTINGS)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_FROM_SETTINGS) {
            permission?.let { requestPermission(it) }
        }
    }
}