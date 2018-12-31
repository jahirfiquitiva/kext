package ca.allanwang.kau.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.customview.widget.ViewDragHelper
import com.google.android.material.snackbar.Snackbar
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.extensions.accentColor
import jahirfiquitiva.libs.kext.extensions.buildSnackbar
import jahirfiquitiva.libs.kext.extensions.color
import jahirfiquitiva.libs.kext.extensions.string

fun <T : View> T.visible(): T {
    visibility = View.VISIBLE
    return this
}

fun <T : View> T.invisible(): T {
    visibility = View.INVISIBLE
    return this
}

fun <T : View> T.gone(): T {
    visibility = View.GONE
    return this
}

fun <T : View> T.invisibleIf(invisible: Boolean): T =
    if (invisible) invisible() else visible()

fun <T : View> T.visibleIf(visible: Boolean): T = if (visible) visible() else gone()

fun <T : View> T.goneIf(gone: Boolean): T = visibleIf(!gone)

inline val View.isVisible: Boolean get() = visibility == View.VISIBLE

inline val View.isInvisible: Boolean get() = visibility == View.INVISIBLE

inline val View.isGone: Boolean get() = visibility == View.GONE

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

@SuppressLint("PrivateResource")
fun View.snackbar(
    text: String,
    @ColorInt textColor: Int = Color.WHITE,
    @ColorInt actionColor: Int = context.accentColor,
    @ColorInt backgroundColor: Int = context.color(R.color.design_snackbar_background_color),
    margin: Int = 0,
    duration: Int = Snackbar.LENGTH_SHORT,
    builder: Snackbar.() -> Unit = {}
                 ): Snackbar {
    val snackbar =
        buildSnackbar(text, textColor, actionColor, backgroundColor, margin, duration, builder)
    snackbar.show()
    return snackbar
}

@SuppressLint("PrivateResource")
fun View.snackbar(
    @StringRes text: Int,
    @ColorInt textColor: Int = Color.WHITE,
    @ColorInt actionColor: Int = context.accentColor,
    @ColorInt backgroundColor: Int = context.color(R.color.design_snackbar_background_color),
    margin: Int = 0,
    duration: Int = Snackbar.LENGTH_SHORT,
    builder: Snackbar.() -> Unit = {}
                 ) =
    snackbar(
        context.string(text), textColor, actionColor, backgroundColor, margin, duration, builder)

fun View.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) =
    context.toast(id, duration)

fun View.toast(text: String, duration: Int = Toast.LENGTH_SHORT) =
    context.toast(text, duration)

/**
 * Set left margin to a value in px
 */
fun View.setMarginLeft(margin: Int) = setMargins(margin, KAU_LEFT)

/**
 * Set top margin to a value in px
 */
fun View.setMarginTop(margin: Int) = setMargins(margin, KAU_TOP)

/**
 * Set right margin to a value in px
 */
fun View.setMarginRight(margin: Int) = setMargins(margin, KAU_RIGHT)

/**
 * Set bottom margin to a value in px
 */
fun View.setMarginBottom(margin: Int) = setMargins(margin, KAU_BOTTOM)

/**
 * Set left and right margins to a value in px
 */
fun View.setMarginHorizontal(margin: Int) = setMargins(margin, KAU_HORIZONTAL)

/**
 * Set top and bottom margins to a value in px
 */
fun View.setMarginVertical(margin: Int) = setMargins(margin, KAU_VERTICAL)

/**
 * Set all margins to a value in px
 */
fun View.setMargin(margin: Int) = setMargins(margin, KAU_ALL)

/**
 * Base margin setter
 * returns true if setting is successful, false otherwise
 */
private fun View.setMargins(margin: Int, flag: Int): Boolean {
    val p = (layoutParams as? ViewGroup.MarginLayoutParams) ?: return false
    p.setMargins(
        if (flag and KAU_LEFT > 0) margin else p.leftMargin,
        if (flag and KAU_TOP > 0) margin else p.topMargin,
        if (flag and KAU_RIGHT > 0) margin else p.rightMargin,
        if (flag and KAU_BOTTOM > 0) margin else p.bottomMargin)
    return true
}

/**
 * Set left padding to a value in px
 */
fun View.setPaddingLeft(padding: Int) = setPadding(padding, KAU_LEFT)

/**
 * Set top padding to a value in px
 */
fun View.setPaddingTop(padding: Int) = setPadding(padding, KAU_TOP)

/**
 * Set right padding to a value in px
 */
fun View.setPaddingRight(padding: Int) = setPadding(padding, KAU_RIGHT)

/**
 * Set bottom padding to a value in px
 */
fun View.setPaddingBottom(padding: Int) = setPadding(padding, KAU_BOTTOM)

/**
 * Set left and right padding to a value in px
 */
fun View.setPaddingHorizontal(padding: Int) = setPadding(padding, KAU_HORIZONTAL)

/**
 * Set top and bottom padding to a value in px
 */
fun View.setPaddingVertical(padding: Int) = setPadding(padding, KAU_VERTICAL)

/**
 * Set all padding to a value in px
 */
fun View.setPadding(padding: Int) = setPadding(padding, KAU_ALL)

/**
 * Base padding setter
 */
private fun View.setPadding(padding: Int, flag: Int) {
    setPadding(
        if (flag and KAU_LEFT > 0) padding else paddingLeft,
        if (flag and KAU_TOP > 0) padding else paddingTop,
        if (flag and KAU_RIGHT > 0) padding else paddingRight,
        if (flag and KAU_BOTTOM > 0) padding else paddingBottom)
}

const val KAU_LEFT = ViewDragHelper.EDGE_LEFT
const val KAU_RIGHT = ViewDragHelper.EDGE_RIGHT
const val KAU_TOP = ViewDragHelper.EDGE_TOP
const val KAU_BOTTOM = ViewDragHelper.EDGE_BOTTOM
const val KAU_HORIZONTAL = KAU_LEFT or KAU_RIGHT
const val KAU_VERTICAL = KAU_TOP or KAU_BOTTOM
const val KAU_ALL = KAU_HORIZONTAL or KAU_VERTICAL