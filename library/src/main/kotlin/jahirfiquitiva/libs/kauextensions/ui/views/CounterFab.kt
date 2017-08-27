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

package jahirfiquitiva.libs.kauextensions.ui.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntRange
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * Kotlin port of CounterFab by André Mion
 * https://github.com/andremion/CounterFab/
 */
class CounterFab:FloatingActionButton {
    private val ANIMATION_PROPERTY =
            object:Property<CounterFab, Float>(Float::class.java, "animation") {
                override fun set(`object`:CounterFab, value:Float?) {
                    value?.let {
                        mAnimationFactor = it
                    }
                    postInvalidateOnAnimation()
                }
                
                override fun get(`object`:CounterFab):Float = 0F
            }
    
    private val MAX_COUNT = 99
    private val MAX_COUNT_TEXT = "99+"
    private val TEXT_SIZE_DP = 11
    private val TEXT_PADDING_DP = 2
    private val MASK_COLOR = Color.parseColor("#33000000")
    // Translucent black as mask color
    private val ANIMATION_INTERPOLATOR = OvershootInterpolator()
    
    private var mContentBounds:Rect = Rect()
    private var mTextPaint:Paint = Paint()
    private var mTextSize:Float = 0F
    private var mCirclePaint:Paint = Paint()
    private var mCircleBounds:Rect = Rect()
    private var mMaskPaint:Paint = Paint()
    private var mAnimationDuration:Int = 0
    private var mAnimationFactor:Float = 0.toFloat()
    
    private var mCount:Int = 0
    private var mText:String? = null
    private var mTextHeight:Float = 0F
    private var mAnimator:ObjectAnimator? = null
    
    constructor(context:Context):super(context) {
        initFab()
    }
    
    constructor(context:Context, attrs:AttributeSet):super(context, attrs) {
        initFab()
    }
    
    constructor(context:Context, attrs:AttributeSet?, defStyleAttr:Int):super(context, attrs,
                                                                              defStyleAttr) {
        initFab()
    }
    
    private fun initFab() {
        useCompatPadding = true
        
        val density = resources.displayMetrics.density
        
        mTextSize = TEXT_SIZE_DP * density
        val textPadding = TEXT_PADDING_DP * density
        
        mAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        mAnimationFactor = 1F
        
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.style = Paint.Style.STROKE
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = mTextSize
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.typeface = Typeface.SANS_SERIF
        
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint.style = Paint.Style.FILL
        val colorStateList = backgroundTintList
        if (colorStateList != null) {
            mCirclePaint.color = colorStateList.defaultColor
        } else {
            val background = background
            if (background is ColorDrawable) {
                mCirclePaint.color = background.color
            }
        }
        
        mMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMaskPaint.style = Paint.Style.FILL
        mMaskPaint.color = MASK_COLOR
        
        val textBounds = Rect()
        mTextPaint.getTextBounds(MAX_COUNT_TEXT, 0, MAX_COUNT_TEXT.length, textBounds)
        mTextHeight = textBounds.height().toFloat()
        
        val textWidth = mTextPaint.measureText(MAX_COUNT_TEXT)
        val circleRadius = Math.max(textWidth, mTextHeight) / 2F + textPadding
        mCircleBounds = Rect(0, 0, (circleRadius * 2).toInt(), (circleRadius * 2).toInt())
        mContentBounds = Rect()
        
        onCountChanged()
    }
    
    /**
     * @return The current count value
     */
    fun getCount():Int = mCount
    
    /**
     * Set the count to show on badge
     *
     * @param count
     * The count value starting from 0
     */
    fun setCount(@IntRange(from = 0) count:Int) {
        if (count == mCount) return
        mCount = if (count > 0) count else 0
        onCountChanged()
        if (ViewCompat.isLaidOut(this)) {
            startAnimation()
        }
    }
    
    /**
     * Increase the current count value by 1
     */
    fun increase() {
        setCount(mCount + 1)
    }
    
    /**
     * Decrease the current count value by 1
     */
    fun decrease() {
        setCount(if (mCount > 0) mCount - 1 else 0)
    }
    
    private fun onCountChanged() {
        mText = if (mCount > MAX_COUNT) {
            MAX_COUNT_TEXT
        } else {
            mCount.toString()
        }
    }
    
    private fun startAnimation() {
        var start = 0F
        var end = 1F
        if (mCount == 0) {
            start = 1F
            end = 0F
        }
        if (isAnimating()) {
            mAnimator?.cancel()
        }
        mAnimator = ObjectAnimator.ofObject(this, ANIMATION_PROPERTY, null, start, end)
        mAnimator?.interpolator = ANIMATION_INTERPOLATOR
        mAnimator?.duration = mAnimationDuration.toLong()
        mAnimator?.start()
    }
    
    private fun isAnimating():Boolean = mAnimator?.isRunning ?: true
    
    override fun onDraw(canvas:Canvas) {
        super.onDraw(canvas)
        if (mCount > 0 || isAnimating()) {
            if (getContentRect(mContentBounds)) {
                mCircleBounds.offsetTo(
                        mContentBounds.left + mContentBounds.width() - mCircleBounds.width(),
                        mContentBounds.top)
            }
            val cx = mCircleBounds.centerX().toFloat()
            val cy = mCircleBounds.centerY().toFloat()
            val radius = mCircleBounds.width() / 2F * mAnimationFactor
            // Solid circle
            canvas.drawCircle(cx, cy, radius, mCirclePaint)
            // Mask circle
            canvas.drawCircle(cx, cy, radius, mMaskPaint)
            // Count text
            mTextPaint.textSize = mTextSize * mAnimationFactor
            canvas.drawText(mText, cx, cy + mTextHeight / 2F, mTextPaint)
        }
    }
    
    private class SavedState:View.BaseSavedState {
        var count:Int = 0
        
        /**
         * Constructor called from [CounterFab.onSaveInstanceState]
         */
        constructor(superState:Parcelable):super(superState)
        
        /**
         * Constructor called from [.CREATOR]
         */
        constructor(`in`:Parcel):super(`in`) {
            count = `in`.readInt()
        }
        
        override fun writeToParcel(out:Parcel, flags:Int) {
            super.writeToParcel(out, flags)
            out.writeInt(count)
        }
        
        override fun toString():String {
            return CounterFab::class.java.simpleName + "." + SavedState::class.java.simpleName + "{" +
                    Integer.toHexString(System.identityHashCode(this)) + " count=" + count + "}"
        }
        
        companion object {
            val CREATOR:Parcelable.Creator<SavedState> = object:Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`:Parcel):SavedState = SavedState(`in`)
                
                override fun newArray(size:Int):Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
    
    public override fun onSaveInstanceState():Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.count = mCount
        return ss
    }
    
    public override fun onRestoreInstanceState(state:Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        setCount(ss.count)
        requestLayout()
    }
}