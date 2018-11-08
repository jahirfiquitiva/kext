package jahirfiquitiva.libs.kext.extensions

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

@DrawableRes
fun Context.getAppIconResId(pkg: String): Int? {
    return getAppInfo(pkg)?.icon
}

fun Context.getAppIcon(pkg: String): Drawable? {
    return try {
        loadIcon(pkg) ?: packageManager.getApplicationIcon(pkg)
    } catch (e: Exception) {
        null
    }
}

private fun Context.loadIcon(pkg: String): Drawable? {
    try {
        val ai = getAppInfo(pkg)
        if (ai != null) {
            var icon = ai.loadIcon(packageManager)
            if (icon == null) icon = getResources(ai)?.getAppIcon(ai.icon)
            return icon
        }
    } catch (e: Exception) {
    }
    return null
}

private fun Resources.getAppIcon(iconId: Int): Drawable? {
    return try {
        val iconDpi: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            DisplayMetrics.DENSITY_XXXHIGH
        } else {
            DisplayMetrics.DENSITY_XXHIGH
        }
        ResourcesCompat.getDrawableForDensity(this, iconId, iconDpi, null)
    } catch (e: Exception) {
        null
    }
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