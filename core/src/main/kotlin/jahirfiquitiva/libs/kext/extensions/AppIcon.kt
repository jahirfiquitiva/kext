package jahirfiquitiva.libs.kext.extensions

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics

@DrawableRes
fun Context.getAppIconResId(pkg: String): Int? {
    return getAppInfo(pkg)?.icon
}

fun Context.getAppIcon(pkg: String): Drawable? {
    return loadIcon(pkg) ?: packageManager.getApplicationIcon(pkg)
}

private fun Context.loadIcon(pkg: String): Drawable? {
    val ai = getAppInfo(pkg)
    if (ai != null) {
        var icon = ai.loadIcon(packageManager)
        if (icon == null) icon = getResources(ai)?.getAppIcon(ai.icon)
        return icon
    }
    return null
}

private fun Resources.getAppIcon(iconId: Int): Drawable? {
    val d: Drawable?
    d = try {
        val iconDpi: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            DisplayMetrics.DENSITY_XXXHIGH
        } else {
            DisplayMetrics.DENSITY_XXHIGH
        }
        ResourcesCompat.getDrawableForDensity(this, iconId, iconDpi, null)
    } catch (e: Exception) {
        null
    }
    return d
}

private fun Context.getAppInfo(pkg: String): ApplicationInfo? {
    return try {
        packageManager.getApplicationInfo(pkg, 0)
    } catch (e: Exception) {
        null
    }
}

private fun Context.getResources(ai: ApplicationInfo): Resources? {
    return try {
        packageManager.getResourcesForApplication(ai)
    } catch (e: Exception) {
        null
    }
}