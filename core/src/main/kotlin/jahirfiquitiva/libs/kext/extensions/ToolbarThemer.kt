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
package jahirfiquitiva.libs.kext.extensions

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.withAlpha
import jahirfiquitiva.libs.kext.R
import java.lang.reflect.Field

fun Toolbar.tint(
    @ColorInt titleColor: Int, @ColorInt subtitleColor: Int = titleColor,
    @ColorInt iconsColor: Int = titleColor, forceShowIcons: Boolean = false
                ) {
    
    (0..childCount).forEach { i ->
        val v = getChildAt(i)
        
        //Step 1 : Changing the color of back button (or open drawer button).
        (v as? ImageButton)?.drawable?.applyColorFilter(iconsColor)
        
        if (v is ActionMenuView) {
            //Step 2: Changing the color of any ActionMenuViews - icons that are not back
            // button, nor text, nor overflow menu icon.
            (0..v.childCount)
                .map {
                    v.getChildAt(it)
                }
                .filterIsInstance<ActionMenuItemView>()
                .forEach { innerView ->
                    innerView.compoundDrawables.forEach {
                        if (it != null) {
                            innerView.post {
                                it.applyColorFilter(iconsColor)
                            }
                        }
                    }
                }
        }
    }
    
    // Step 3: Tint collapse icon
    try {
        val field = Toolbar::class.java.getDeclaredField("mCollapseIcon")
        field.isAccessible = true
        val collapseIcon = field.get(this) as Drawable
        field.set(this, collapseIcon.applyColorFilter(iconsColor))
    } catch (e: Exception) {
    }
    
    // Step 4: Changing the color of title and subtitle.
    setTitleTextColor(titleColor)
    setSubtitleTextColor(subtitleColor)
    
    // Step 5: Change the color of overflow menu icon.
    overflowIcon?.applyColorFilter(iconsColor)
    setOverflowButtonColor(iconsColor)
    
    // Step 6: Tint toolbar menu.
    menu?.tint(iconsColor, forceShowIcons)
}

fun Menu.tint(@ColorInt iconsColor: Int, forceShowIcons: Boolean = false) {
    // Theme menu action views
    (0 until size()).forEach { i ->
        val item = getItem(i)
        item.icon?.applyColorFilter(iconsColor)
        (item.actionView as? SearchView)?.tint(iconsColor)
    }
    
    // Display icons for easy UI understanding
    if (forceShowIcons) {
        try {
            val setOptionalIconsVisible = javaClass.getDeclaredMethod(
                "setOptionalIconsVisible",
                kotlin.Boolean::class.javaPrimitiveType)
            if (!setOptionalIconsVisible.isAccessible) setOptionalIconsVisible.isAccessible = true
            setOptionalIconsVisible.invoke(this, true)
        } catch (ignored: Exception) {
        }
    }
}

private fun Toolbar.setOverflowButtonColor(@ColorInt color: Int) {
    @SuppressLint("PrivateResource")
    val overflowDescription = resources.getString(R.string.abc_action_menu_overflow_description)
    val outViews = ArrayList<View>()
    findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
    if (outViews.isEmpty()) return
    val overflow = outViews[0] as AppCompatImageView
    overflow.setImageDrawable(overflow.drawable.applyColorFilter(color))
}

fun SearchView.tint(@ColorInt tintColor: Int, @ColorInt hintColor: Int = tintColor) {
    val cls = javaClass
    try {
        val mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView")
        mSearchSrcTextViewField.isAccessible = true
        val mSearchSrcTextView = mSearchSrcTextViewField.get(this) as? EditText
        mSearchSrcTextView?.setTextColor(tintColor)
        mSearchSrcTextView?.setHintTextColor(
            if (hintColor == tintColor) hintColor.withAlpha(0.5F) else hintColor)
        mSearchSrcTextView?.tint(tintColor)
        
        var field = cls.getDeclaredField("mSearchButton")
        tintImageView(this, field, tintColor)
        field = cls.getDeclaredField("mGoButton")
        tintImageView(this, field, tintColor)
        field = cls.getDeclaredField("mCloseButton")
        tintImageView(this, field, tintColor)
        field = cls.getDeclaredField("mVoiceButton")
        tintImageView(this, field, tintColor)
        
        field = cls.getDeclaredField("mSearchPlate")
        field.isAccessible = true
        (field.get(this) as View).background.applyColorFilter(tintColor)
        
        field = cls.getDeclaredField("mSearchHintIcon")
        field.isAccessible = true
        field.set(this, (field.get(this) as Drawable).applyColorFilter(tintColor))
    } catch (e: Exception) {
    }
}

private fun tintImageView(target: Any, field: Field, tintColor: Int) {
    field.isAccessible = true
    val imageView = field.get(target) as ImageView
    imageView.tint(tintColor)
}

fun ImageView.tint(@ColorInt color: Int) {
    if (drawable != null) setImageDrawable(drawable.applyColorFilter(color))
}

fun EditText.tint(@ColorInt color: Int) {
    try {
        val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        fCursorDrawableRes.isAccessible = true
        val mCursorDrawableRes = fCursorDrawableRes.getInt(this)
        val fEditor = TextView::class.java.getDeclaredField("mEditor")
        fEditor.isAccessible = true
        val editor = fEditor.get(this)
        val clazz = editor.javaClass
        val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
        fCursorDrawable.isAccessible = true
        val drawables = arrayOfNulls<Drawable>(2)
        drawables[0] =
            ContextCompat.getDrawable(context, mCursorDrawableRes)?.applyColorFilter(color)
        drawables[1] =
            ContextCompat.getDrawable(context, mCursorDrawableRes)?.applyColorFilter(color)
        fCursorDrawable.set(editor, drawables)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}