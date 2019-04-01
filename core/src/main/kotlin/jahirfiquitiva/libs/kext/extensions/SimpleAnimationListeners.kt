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
package jahirfiquitiva.libs.kext.extensions

import android.animation.Animator
import android.view.animation.Animation

abstract class SimpleAnimationListener : Animation.AnimationListener {
    open fun onStart(animation: Animation) = Unit
    open fun onEnd(animation: Animation) = Unit
    open fun onRepeat(animation: Animation) = Unit
    
    override fun onAnimationRepeat(animation: Animation?) {
        animation?.let { onRepeat(it) }
    }
    
    override fun onAnimationEnd(animation: Animation?) {
        animation?.let { onEnd(it) }
    }
    
    override fun onAnimationStart(animation: Animation?) {
        animation?.let { onStart(it) }
    }
}

abstract class SimpleAnimatorListener : Animator.AnimatorListener {
    open fun onStart(animator: Animator) = Unit
    open fun onEnd(animator: Animator) = Unit
    open fun onRepeat(animator: Animator) = Unit
    open fun onCancel(animator: Animator) = onEnd(animator)
    
    override fun onAnimationRepeat(animator: Animator?) {
        animator?.let { onRepeat(it) }
    }
    
    override fun onAnimationEnd(animator: Animator?) {
        animator?.let { onEnd(it) }
    }
    
    override fun onAnimationStart(animator: Animator?) {
        animator?.let { onStart(it) }
    }
    
    override fun onAnimationCancel(animator: Animator?) {
        animator?.let { onCancel(it) }
    }
}
