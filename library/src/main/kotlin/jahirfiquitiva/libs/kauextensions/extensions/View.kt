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

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.BitmapImageViewTarget
import java.io.File

fun ViewGroup.inflate(layoutId:Int, attachToRoot:Boolean = false):View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ImageView.loadFromUrl(url:String, @DrawableRes error:Int) {
    loadFromUrl(url, ContextCompat.getDrawable(context, error))
}

fun ImageView.loadFromUrl(url:String, error:Drawable? = null) {
    if (url.isEmpty() && error != null) {
        Glide.with(context).load(error).into(this)
    } else {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(this)
    }
}

fun ImageView.loadFromUrls(url:String, thumbUrl:String, sizeMultiplier:Float = 0.5F) {
    if (thumbUrl.isNotEmpty())
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).thumbnail(
                Glide.with(context).load(thumbUrl).priority(Priority.IMMEDIATE)
                        .thumbnail(sizeMultiplier))
                .priority(Priority.HIGH).into(this)
    else if (url.isNotEmpty())
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(sizeMultiplier).priority(Priority.HIGH).into(this)
}

fun ImageView.loadFromUrlsIntoTarget(url:String, thumbUrl:String, target:BitmapImageViewTarget,
                                     sizeMultiplier:Float = 0.5F) {
    if (thumbUrl.isNotEmpty()) {
        Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(Glide.with(context).load(thumbUrl).asBitmap()
                                   .priority(Priority.IMMEDIATE).thumbnail(sizeMultiplier))
                .priority(Priority.HIGH).into(target)
    } else if (url.isNotEmpty()) {
        Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(sizeMultiplier).priority(Priority.HIGH).into(target)
    }
}

fun Context.inflateView(@LayoutRes layout:Int, root:ViewGroup, attachToRoot:Boolean = false):View =
        LayoutInflater.from(this).inflate(layout, root, attachToRoot)

fun Context.downloadOnly(url:String, width:Int = 500, height:Int = 500):FutureTarget<File> =
        Glide.with(this).load(url).downloadOnly(width, height)

/**
 * Based on Allan Wang's code
 */
fun Toolbar.tint(@ColorInt titleColor:Int, @ColorInt subtitleColor:Int = titleColor,
                 @ColorInt iconsColor:Int = titleColor) {
    setTitleTextColor(titleColor)
    setSubtitleTextColor(subtitleColor)
    (0 until childCount).asSequence().forEach {
        (getChildAt(it) as? ImageButton)?.setColorFilter(iconsColor)
    }
}