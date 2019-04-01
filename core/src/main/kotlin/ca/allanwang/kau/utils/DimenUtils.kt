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
package ca.allanwang.kau.utils

import android.content.res.Resources

/**
 * Utils originally created by Allan Wang
 * Available at https://github.com/AllanWang/KAU
 * I have added them here (copy/pasted) because this lib doesn't really uses/needs all its features
 * at a 100%.
 * Anyway, full credits go to Allan, for these awesome extensions
 */

inline val Float.dpToPx: Float
    get() = this * Resources.getSystem().displayMetrics.density

inline val Int.dpToPx: Int
    get() = toFloat().dpToPx.toInt()

inline val Float.pxToDp: Float
    get() = this / Resources.getSystem().displayMetrics.density

inline val Int.pxToDp: Int
    get() = toFloat().pxToDp.toInt()

inline val Float.dpToSp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity

inline val Int.dpToSp: Int
    get() = toFloat().dpToSp.toInt()

inline val Float.spToDp: Float
    get() = this / Resources.getSystem().displayMetrics.scaledDensity

inline val Int.spToDp: Int
    get() = toFloat().spToDp.toInt()
