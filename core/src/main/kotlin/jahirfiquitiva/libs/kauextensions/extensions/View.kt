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

package jahirfiquitiva.libs.kauextensions.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import jahirfiquitiva.libs.kauextensions.ui.graphics.ObservableColorMatrix

inline val View.isNotVisible: Boolean
    get() = visibility != View.VISIBLE

inline val View.isNotInvisible: Boolean
    get() = visibility != View.INVISIBLE

inline val View.isNotGone: Boolean
    get() = visibility != View.GONE

fun View.buildSnackbar(
        @StringRes text: Int, duration: Int = Snackbar.LENGTH_LONG,
        builder: Snackbar.() -> Unit = {}
                      ): Snackbar {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.builder()
    return snackbar
}

fun View.buildSnackbar(
        text: String, duration: Int = Snackbar.LENGTH_LONG,
        builder: Snackbar.() -> Unit = {}
                      ): Snackbar {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.builder()
    return snackbar
}

/**
 * Credits to Mysplash
 * https://goo.gl/M2sqE2
 */
fun ImageView.animateColorTransition(onFaded: () -> Unit = {}) {
    setHasTransientState(true)
    val matrix = ObservableColorMatrix()
    val saturation = ObjectAnimator.ofFloat(matrix, ObservableColorMatrix.SATURATION, 0F, 1F)
    saturation.addUpdateListener {
        colorFilter = ColorMatrixColorFilter(matrix)
    }
    saturation.duration = 1500L
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        saturation.interpolator = AnimationUtils.loadInterpolator(
                context,
                android.R.interpolator.fast_out_slow_in)
    }
    saturation.addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    clearColorFilter()
                    setHasTransientState(false)
                }
            })
    saturation.start()
    onFaded()
}

fun View.clearChildrenAnimations() {
    clearAnimation()
    getAllChildren(this).forEach { it.clearAnimation() }
}

private fun getAllChildren(v: View): ArrayList<View> {
    if (v !is ViewGroup) {
        val viewArrayList = ArrayList<View>()
        viewArrayList.add(v)
        return viewArrayList
    }
    val result = ArrayList<View>()
    for (i in 0 until v.childCount) {
        val child = v.getChildAt(i)
        val viewArrayList = ArrayList<View>()
        viewArrayList.add(v)
        viewArrayList.addAll(getAllChildren(child))
        result.addAll(viewArrayList)
    }
    return result
}

/**
 * Credits to:
 * https://medium.com/@pablisco/smooth-loading-617995a7b8d3
 */
@Suppress("UNCHECKED_CAST")
fun <T> createAnimator(
        evaluator: TypeEvaluator<*>, vararg values: T,
        onConfig: ValueAnimator.() -> Unit = {},
        onUpdate: (T) -> Unit
                      ): ValueAnimator =
        ValueAnimator.ofObject(evaluator, *values).apply {
            addUpdateListener { onUpdate(it.animatedValue as T) }
            onConfig(this)
        }

fun animateSmoothly(
        @ColorInt startColor: Int, @ColorInt endColor: Int,
        doUpdate: (Int) -> Unit
                   ): ValueAnimator =
        createAnimator(
                ArgbEvaluator(),
                startColor, endColor,
                onConfig = {
                    duration = 1000
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = ValueAnimator.INFINITE
                    start()
                },
                onUpdate = doUpdate)

fun ImageView.setDecodedBitmap(resId: Int) {
    setImageBitmap(decodeBitmapWithSize(resources, resId, width, height))
}

fun RecyclerView.Adapter<*>.isEmpty(): Boolean = itemCount <= 0