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

import android.app.ActivityManager
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ca.allanwang.kau.utils.resolveBoolean
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.helpers.Prefs
import jahirfiquitiva.libs.kext.helpers.Rec
import jahirfiquitiva.libs.kext.ui.activities.ThemedActivity

val Context.isFirstRun: Boolean
    get() {
        val prefs = (this as? ThemedActivity<*>)?.prefs ?: Prefs("kau_ext", this)
        val isIt = prefs.isFirstRun
        prefs.isFirstRun = false
        return isIt
    }

val Context.isUpdate: Boolean
    get() {
        val prefs = (this as? ThemedActivity<*>)?.prefs ?: Prefs("kau_ext", this)
        val thisVersion = getAppVersionCode()
        val prevVersion = prefs.lastVersion
        prefs.lastVersion = thisVersion
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
    get() = (this as? ThemedActivity<*>)?.usesDarkTheme() ?: resolveBoolean(R.attr.isDarkTheme)

@ColorInt
fun Context.extractColor(attribute: IntArray): Int {
    val typedValue = TypedValue()
    val a = obtainStyledAttributes(typedValue.data, attribute)
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}

fun Context.extractDrawable(@AttrRes drawableAttributeId: Int): Drawable? {
    val typedValue = TypedValue()
    val a = obtainStyledAttributes(typedValue.data, intArrayOf(drawableAttributeId))
    val drawable = a.getDrawable(0)
    a.recycle()
    return drawable
}

inline fun <reified T : View> Context.inflate(
    @LayoutRes layout: Int,
    root: ViewGroup? = null,
    attachToRoot: Boolean = false
                                             ): T? =
    LayoutInflater.from(this).inflate(layout, root, attachToRoot) as? T

fun Context.getAppName(defName: String = ""): String {
    var name: String = try {
        (packageManager?.getApplicationLabel(applicationInfo) ?: "").toString()
    } catch (e: Exception) {
        ""
    }
    if (name.hasContent()) return name
    
    val stringRes = applicationInfo?.labelRes ?: 0
    name = if (stringRes == 0) {
        applicationInfo?.nonLocalizedLabel?.toString() ?: ""
    } else {
        string(stringRes)
    }
    
    if (name.hasContent()) return name
    if (defName.hasContent()) return defName
    
    val def = string(R.string.app_name)
    return if (def.hasContent()) def else "Unknown"
}

@Suppress("DEPRECATION")
fun Context.getAppVersionCode(): Long {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, 0).longVersionCode
        } else {
            packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
        }
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

fun Context.getSharedPrefs(name: String): SharedPreferences =
    getSharedPreferences(name, Context.MODE_PRIVATE)

val Context.isInHorizontalMode: Boolean
    get() = currentRotation == 90 || currentRotation == 270

val Context.isInPortraitMode: Boolean
    get() = currentRotation == 0 || currentRotation == 180

val Context.currentRotation: Int
    get() {
        val display = (getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay
        return (display?.rotation ?: 0) * 90
    }

val Context.isLowRamDevice: Boolean
    get() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val lowRAMDevice: Boolean
        lowRAMDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activityManager?.isLowRamDevice ?: true
        } else {
            val memInfo = ActivityManager.MemoryInfo()
            activityManager?.getMemoryInfo(memInfo)
            memInfo.lowMemory
        }
        return lowRAMDevice
    }

fun Context.getStatusBarHeight(force: Boolean = false): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) result = resources.getDimensionPixelSize(resourceId)
    
    val dimenResult = resources.getDimensionPixelSize(R.dimen.status_bar_height)
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

fun Context.bitmap(name: String): Bitmap? = bitmapDrawable(name)?.bitmap

fun Context.bitmapDrawable(name: String, logError: Boolean = false): BitmapDrawable? {
    if (!name.hasContent()) return null
    return try {
        ResourcesCompat.getDrawable(resources, resource(name), null) as? BitmapDrawable
    } catch (e: Exception) {
        if (logError) Rec().e("BitmapDrawable with name '$name' could not be found")
        null
    }
}

fun Context.drawable(name: String, logError: Boolean = false): Drawable? {
    if (!name.hasContent()) return null
    return try {
        ContextCompat.getDrawable(this, resource(name))
    } catch (e: Exception) {
        if (logError) Rec().e("Drawable with name '$name' could not be found")
        null
    }
}

fun Context.drawable(@DrawableRes res: Int): Drawable? =
    try {
        ContextCompat.getDrawable(this, res)
    } catch (ignored: Exception) {
        null
    }

@DrawableRes
fun Context.resource(name: String): Int {
    val res = resources.getIdentifier(name, "drawable", packageName)
    return if (res != 0) res else 0
}

@ColorInt
fun Context.color(@ColorRes res: Int): Int =
    try {
        ContextCompat.getColor(this, res)
    } catch (ignored: Exception) {
        0
    }

fun Context.string(@StringRes res: Int, fallback: String = ""): String =
    try {
        getString(res) ?: fallback
    } catch (ignored: Exception) {
        fallback
    }

fun Context.stringArray(@ArrayRes arrayRes: Int): Array<String>? =
    try {
        resources.getStringArray(arrayRes)
    } catch (ignored: Exception) {
        null
    }

fun Context.boolean(@BoolRes res: Int, fallback: Boolean = false): Boolean =
    try {
        resources.getBoolean(res)
    } catch (ignored: Exception) {
        fallback
    }

fun Context.int(@IntegerRes res: Int, fallback: Int = 0): Int =
    try {
        resources.getInteger(res)
    } catch (ignored: Exception) {
        fallback
    }

fun Context.dimen(@DimenRes res: Int, fallback: Float = 0.0F): Float =
    try {
        resources.getDimension(res)
    } catch (ignored: Exception) {
        fallback
    }

fun Context.dimenPixelSize(@DimenRes res: Int, fallback: Int = 0): Int =
    try {
        resources.getDimensionPixelSize(res)
    } catch (ignored: Exception) {
        fallback
    }
