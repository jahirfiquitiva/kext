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
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

fun ViewGroup.inflate(layoutId:Int, attachToRoot:Boolean = false):View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun Context.inflateView(@LayoutRes layout:Int, root:ViewGroup, attachToRoot:Boolean = false):View =
        LayoutInflater.from(this).inflate(layout, root, attachToRoot)