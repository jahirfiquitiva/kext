package ca.allanwang.kau.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import jahirfiquitiva.libs.kext.extensions.isOnMainThread
import jahirfiquitiva.libs.kext.extensions.string
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Utils originally created by Allan Wang
 * Available at https://github.com/AllanWang/KAU
 * I have added them here (copy/pasted) because this lib doesn't really uses/needs all its features
 * at a 100%.
 * Anyway, full credits go to Allan, for these awesome extensions
 */

fun Context.isAppInstalled(packageName: String): Boolean = try {
    packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    true
} catch (e: Exception) {
    false
}

fun Context.isAppEnabled(packageName: String): Boolean = try {
    packageManager.getApplicationInfo(packageName, 0).enabled
} catch (e: Exception) {
    false
}

/**
 * Restarts an activity from itself with a fade animation
 * Keeps its existing extra bundles and has a intentBuilder to accept other parameters
 */
inline fun Activity.restart(intentBuilder: Intent.() -> Unit = {}) {
    val i = Intent(this, this::class.java)
    intent?.extras?.let { i.putExtras(it) }
    i.intentBuilder()
    startActivity(i)
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    finish()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

inline var Activity.statusBarColor: Int
    @SuppressLint("NewApi")
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor else Color.BLACK
    @SuppressLint("NewApi")
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.statusBarColor = value
    }

inline var Activity.statusBarLight: Boolean
    @SuppressLint("InlinedApi")
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR > 0
        else false
    }
    @SuppressLint("InlinedApi")
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility =
                if (value) flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

inline var Activity.navigationBarColor: Int
    @SuppressLint("NewApi")
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.navigationBarColor
        else Color.BLACK
    }
    @SuppressLint("NewApi")
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.navigationBarColor = value
    }

/**
 * Returns the content view of this Activity if set, null otherwise.
 */
inline val Activity.contentView: View?
    get() = findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0)

fun Activity.snackbar(
    text: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    from: View? = null,
    builder: Snackbar.() -> Unit = {}
                     ) = (from ?: contentView)?.snackbar(text, duration, builder)

fun Activity.snackbar(
    @StringRes textId: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    from: View? = null,
    builder: Snackbar.() -> Unit = {}
                     ) = (from ?: contentView)?.snackbar(textId, duration, builder)

//Toast helpers
fun Context.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) =
    toast(string(id), duration)

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    if (isOnMainThread()) {
        Toast.makeText(this, text, duration).show()
    } else {
        (this as? Activity)?.runOnUiThread { Toast.makeText(this, text, duration).show() }
    }

fun Context.resolveColor(@AttrRes attr: Int, fallback: Int = 0): Int {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getColor(0, fallback)
    } finally {
        a.recycle()
    }
}

fun Context.resolveBoolean(@AttrRes attr: Int, fallback: Boolean = false): Boolean {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getBoolean(0, fallback)
    } finally {
        a.recycle()
    }
}

inline val Context.isNetworkAvailable: Boolean
    @SuppressLint("MissingPermission")
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

inline val Context.isWifiConnected: Boolean
    @SuppressLint("MissingPermission")
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo?.type ?: -1) == ConnectivityManager.TYPE_WIFI
    }

inline val Context.isMobileDataConnected: Boolean
    @SuppressLint("MissingPermission")
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo?.type ?: -1) == ConnectivityManager.TYPE_MOBILE
    }

/**
 * Opens a url
 * If given a series of links, will open the first one that isn't null
 */
fun Context.openLink(vararg url: String?) {
    val link = url.firstOrNull { !it.isNullOrBlank() } ?: return
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    if (browserIntent.resolveActivity(packageManager) != null)
        startActivity(browserIntent)
    else toast("Cannot find a browser")
}

fun Number.round(@IntRange(from = 1L) decimalCount: Int): String {
    val expression = StringBuilder().append("#.")
    (1..decimalCount).forEach { expression.append("#") }
    val formatter = DecimalFormat(expression.toString())
    formatter.roundingMode = RoundingMode.HALF_UP
    return formatter.format(this)
}

fun postDelayed(delay: Long, action: () -> Unit) {
    Handler().postDelayed(action, delay)
}