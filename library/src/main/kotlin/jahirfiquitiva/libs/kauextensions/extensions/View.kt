/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

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