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

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.support.annotation.ArrayRes
import android.support.annotation.AttrRes
import android.support.annotation.BoolRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import ca.allanwang.kau.utils.boolean
import ca.allanwang.kau.utils.color
import ca.allanwang.kau.utils.dimen
import ca.allanwang.kau.utils.dimenPixelSize
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.integer
import ca.allanwang.kau.utils.resolveBoolean
import ca.allanwang.kau.utils.string
import jahirfiquitiva.libs.kauextensions.R
import jahirfiquitiva.libs.kauextensions.helpers.Konfigurations

@Deprecated("", ReplaceWith("isFirstRun"))
val Context.isFirstRunEver: Boolean
    get() = isFirstRun

val Context.isFirstRun: Boolean
    get() {
        val isIt = konfigs.isFirstRun
        konfigs.isFirstRun = false
        return isIt
    }

@Deprecated("", ReplaceWith("isUpdate"))
val Context.justUpdated: Boolean
    get() = isUpdate

val Context.isUpdate: Boolean
    get() {
        val thisVersion = getAppVersionCode()
        val prevVersion = konfigs.lastVersion
        konfigs.lastVersion = thisVersion
        return thisVersion > prevVersion
    }

fun Context.compliesWithMinTime(time: Long): Boolean =
        System.currentTimeMillis() - firstInstallTime > time

val Context.firstInstallTime: Long
    get() {
        return try {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        } catch (e: Exception) {
            -1
        }
    }

val Context.lastUpdateTime: Long
    get() {
        return try {
            packageManager.getPackageInfo(packageName, 0).lastUpdateTime
        } catch (e: Exception) {
            -1
        }
    }

val Context.usesLightTheme
    get() = !usesDarkTheme

val Context.usesDarkTheme
    get() = resolveBoolean(R.attr.isDark)

fun Context.stringArray(@ArrayRes arrayRes: Int): Array<String> =
        resources.getStringArray(arrayRes)

@Deprecated("Use KAU's string instead", ReplaceWith("string()"))
fun Context.getStringFromRes(@StringRes stringRes: Int, fallback: String): String =
        string(stringRes, fallback) ?: fallback

@Deprecated("Use stringArray instead", ReplaceWith("stringArray()"))
fun Context.getStringArray(@ArrayRes arrayRes: Int): Array<String> = stringArray(arrayRes)

@Deprecated("Use KAU's color instead", ReplaceWith("color()"))
fun Context.getColorFromRes(@ColorRes colorRes: Int) = color(colorRes)

@Deprecated("Use KAU's bool instead", ReplaceWith("bool()"))
fun Context.getBoolean(@BoolRes bool: Int) = boolean(bool)

@Deprecated("Use KAU's integer instead", ReplaceWith("integer()"))
fun Context.getInteger(@IntegerRes id: Int): Int = integer(id)

@Deprecated("Use KAU's dimen instead", ReplaceWith("dimen()"))
fun Context.getDimension(@DimenRes id: Int): Float = dimen(id)

@Deprecated("Use KAU's dimenPixelSize instead", ReplaceWith("dimenPixelSize()"))
fun Context.getDimensionPixelSize(@DimenRes id: Int): Int = dimenPixelSize(id)

@Deprecated("Use KAU's drawable instead", ReplaceWith("drawable()"))
fun Context.getDrawable(@DrawableRes id: Int, fallback: Drawable? = null): Drawable? =
        drawable(id, fallback)

@ColorInt
fun Context.extractColor(attribute: IntArray): Int {
    val typedValue = TypedValue()
    val a = obtainStyledAttributes(typedValue.data, attribute)
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}

fun Context.extractDrawable(@AttrRes drawableAttributeId: Int): Drawable {
    val typedValue = TypedValue()
    val a = obtainStyledAttributes(typedValue.data, intArrayOf(drawableAttributeId))
    val drawable = a.getDrawable(0)
    a.recycle()
    return drawable
}

fun Context.showToast(@StringRes textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    if (isOnMainThread()) {
        Toast.makeText(this, textRes, duration).show()
    } else {
        (this as? Activity)?.runOnUiThread { Toast.makeText(this, textRes, duration).show() }
    }
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    if (isOnMainThread()) {
        Toast.makeText(this, text, duration).show()
    } else {
        (this as? Activity)?.runOnUiThread { Toast.makeText(this, text, duration).show() }
    }
}

inline fun <reified T : View> Context.inflate(
        @LayoutRes layout: Int,
        root: ViewGroup,
        attachToRoot: Boolean = false
                                             ): T =
        LayoutInflater.from(this).inflate(layout, root, attachToRoot) as T

fun Context.getAppName(): String = getStringFromRes(R.string.app_name, "KAU Extensions")

fun Context.getLogTag(): String = getAppName()

fun Context.getAppVersionCode(): Int {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionCode
    } catch (e: Exception) {
        -1
    }
}

fun Context.getAppVersion(): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: Exception) {
        "Unknown"
    }
}

fun Context.isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun Context.getSharedPrefs(name: String) = getSharedPreferences(name, Context.MODE_PRIVATE)

fun Context.hasReadStoragePermission() =
        ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

fun Context.hasWriteStoragePermission() =
        ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

val Context.konfigs: Konfigurations
    get() = Konfigurations.newInstance("kau_extensions", this)

fun Context.runInAThread(item: () -> Unit) {
    Thread(Runnable(item)).start()
}

val Context.isInHorizontalMode: Boolean
    get() = currentRotation == 90 || currentRotation == 270

val Context.isInPortraitMode: Boolean
    get() = currentRotation == 0 || currentRotation == 180

val Context.currentRotation: Int
    get() {
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        return display.rotation * 90
    }

val Context.isLowRamDevice: Boolean
    get() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val lowRAMDevice: Boolean
        lowRAMDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activityManager.isLowRamDevice
        } else {
            val memInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memInfo)
            memInfo.lowMemory
        }
        return lowRAMDevice
    }

fun Context.getStatusBarHeight(force: Boolean = false): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) result = resources.getDimensionPixelSize(resourceId)
    
    val dimenResult = dimenPixelSize(R.dimen.status_bar_height)
    //if our dimension is 0 return 0 because on those devices we don't need the height
    return if (dimenResult == 0 && !force) {
        0
    } else {
        //if our dimens is > 0 && the result == 0 use the dimenResult else the result
        if (result == 0) dimenResult else result
    }
}

fun Context.getUriFromResource(id: Int): Uri? {
    return Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://" +
                    "${resources.getResourcePackageName(id)}/" +
                    "${resources.getResourceTypeName(id)}/" + resources.getResourceEntryName(id))
}