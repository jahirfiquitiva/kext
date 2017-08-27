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

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.OverScroller

open class ZoomableImageView:AppCompatImageView {
    private val DEBUG = "DEBUG"
    //
    // SuperMin and SuperMax multipliers. Determine how much the image can be
    // zoomed below or above the zoom boundaries, before animating back to the
    // min/max zoom boundary.
    //
    private val SUPER_MIN_MULTIPLIER = .75f
    private val SUPER_MAX_MULTIPLIER = 1.25f
    //
    // Scale of image ranges from minScale to maxScale, where minScale == 1
    // when the image is stretched to fit view.
    //
    private var normalizedScale:Float = 0F
    
    //
    // Matrix applied to image. MSCALE_X and MSCALE_Y should always be equal.
    // MTRANS_X and MTRANS_Y are the other values used. prevMatrix is the matrix
    // saved prior to the screen rotating.
    //
    private var prevMatrix:Matrix? = null
    private var state:State? = null
    private var minScale:Float = 0F
    private var maxScale:Float = 0F
    private var superMinScale:Float = 0F
    private var superMaxScale:Float = 0F
    private var m:FloatArray? = null
    private var fling:Fling? = null
    private var mScaleType:ImageView.ScaleType? = null
    private var imageRenderedAtLeastOnce:Boolean = false
    private var onDrawReady:Boolean = false
    private var delayedZoomVariables:ZoomVariables? = null
    //
    // Size of view and previous view size (ie before rotation)
    //
    private var viewWidth:Int = 0
    private var viewHeight:Int = 0
    private var prevViewWidth:Int = 0
    private var prevViewHeight:Int = 0
    //
    // Size of image when it is stretched to fit view. Before and After rotation.
    //
    private var matchViewWidth:Float = 0F
    private var matchViewHeight:Float = 0F
    private var prevMatchViewWidth:Float = 0F
    private var prevMatchViewHeight:Float = 0F
    private var mScaleDetector:ScaleGestureDetector? = null
    private var mGestureDetector:GestureDetector? = null
    private var doubleTapListener:GestureDetector.OnDoubleTapListener? = null
    private var userTouchListener:View.OnTouchListener? = null
    private var zoomableImageViewListener:OnZoomableImageViewListener? = null
    private var singleTapListener:OnSingleTapListener? = null
    
    constructor(context:Context):super(context) {
        sharedConstructing(context)
    }
    
    constructor(context:Context, attrs:AttributeSet):super(context, attrs) {
        sharedConstructing(context)
    }
    
    constructor(context:Context, attrs:AttributeSet, defStyle:Int):super(context, attrs, defStyle) {
        sharedConstructing(context)
    }
    
    private fun sharedConstructing(context:Context) {
        super.setClickable(true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetector(context, GestureListener())
        prevMatrix = Matrix()
        m = FloatArray(9)
        normalizedScale = 1f
        if (mScaleType == null) {
            mScaleType = ImageView.ScaleType.FIT_CENTER
        }
        minScale = 1f
        maxScale = 3f
        superMinScale = SUPER_MIN_MULTIPLIER * minScale
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
        imageMatrix = matrix
        scaleType = ImageView.ScaleType.MATRIX
        setState(State.NONE)
        onDrawReady = false
        super.setOnTouchListener(PrivateOnTouchListener())
    }
    
    override fun setOnTouchListener(l:View.OnTouchListener) {
        userTouchListener = l
    }
    
    fun setOnzoomableImageViewListener(l:OnZoomableImageViewListener) {
        zoomableImageViewListener = l
    }
    
    fun setOnDoubleTapListener(l:GestureDetector.OnDoubleTapListener) {
        doubleTapListener = l
    }
    
    fun setOnSingleTapListener(l:OnSingleTapListener) {
        singleTapListener = l
    }
    
    override fun setImageResource(resId:Int) {
        super.setImageResource(resId)
        savePreviousImageValues()
        fitImageToView()
    }
    
    override fun setImageBitmap(bm:Bitmap?) {
        super.setImageBitmap(bm)
        savePreviousImageValues()
        fitImageToView()
    }
    
    override fun setImageDrawable(drawable:Drawable?) {
        super.setImageDrawable(drawable)
        savePreviousImageValues()
        fitImageToView()
    }
    
    override fun setImageURI(uri:Uri?) {
        super.setImageURI(uri)
        savePreviousImageValues()
        fitImageToView()
    }
    
    override fun getScaleType():ImageView.ScaleType? = mScaleType
    
    override fun setScaleType(type:ImageView.ScaleType?) {
        if (type == ImageView.ScaleType.FIT_START || type == ImageView.ScaleType.FIT_END) {
            throw UnsupportedOperationException(
                    "ZoomableImageView does not support FIT_START or" + " FIT_END")
        }
        if (type == ImageView.ScaleType.MATRIX) {
            super.setScaleType(ImageView.ScaleType.MATRIX)
            
        } else {
            mScaleType = type
            if (onDrawReady) {
                //
                // If the image is already rendered, scaleType has been called programmatically
                // and the ZoomableImageView should be updated with the new scaleType.
                //
                setZoom(this)
            }
        }
    }
    
    /**
     * Returns false if image is in initial, unzoomed state. False, otherwise.
     *
     * @return true if image is zoomed
     */
    private fun isZoomed():Boolean = normalizedScale != 1f
    
    /**
     * Return a Rect representing the zoomed image.
     *
     * @return rect representing zoomed image
     */
    fun getZoomedRect():RectF {
        if (mScaleType == ImageView.ScaleType.FIT_XY) {
            throw UnsupportedOperationException("getZoomedRect() not supported with FIT_XY")
        }
        val topLeft = transformCoordTouchToBitmap(0f, 0f, true)
        val bottomRight = transformCoordTouchToBitmap(viewWidth.toFloat(), viewHeight.toFloat(),
                                                      true)
        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        return RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h)
    }
    
    /**
     * Save the current matrix and view dimensions in the prevMatrix and prevView variables.
     */
    private fun savePreviousImageValues() {
        if (matrix != null && viewHeight != 0 && viewWidth != 0) {
            matrix?.getValues(m)
            prevMatrix?.setValues(m)
            prevMatchViewHeight = matchViewHeight
            prevMatchViewWidth = matchViewWidth
            prevViewHeight = viewHeight
            prevViewWidth = viewWidth
        }
    }
    
    public override fun onSaveInstanceState():Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putFloat("saveScale", normalizedScale)
        bundle.putFloat("matchViewHeight", matchViewHeight)
        bundle.putFloat("matchViewWidth", matchViewWidth)
        bundle.putInt("viewWidth", viewWidth)
        bundle.putInt("viewHeight", viewHeight)
        matrix?.getValues(m)
        bundle.putFloatArray("matrix", m)
        bundle.putBoolean("imageRendered", imageRenderedAtLeastOnce)
        return bundle
    }
    
    public override fun onRestoreInstanceState(state:Parcelable) {
        if (state is Bundle) {
            normalizedScale = state.getFloat("saveScale")
            m = state.getFloatArray("matrix")
            prevMatrix?.setValues(m)
            prevMatchViewHeight = state.getFloat("matchViewHeight")
            prevMatchViewWidth = state.getFloat("matchViewWidth")
            prevViewHeight = state.getInt("viewHeight")
            prevViewWidth = state.getInt("viewWidth")
            imageRenderedAtLeastOnce = state.getBoolean("imageRendered")
            super.onRestoreInstanceState(state.getParcelable("instanceState"))
            return
        }
        super.onRestoreInstanceState(state)
    }
    
    override fun onDraw(canvas:Canvas) {
        onDrawReady = true
        imageRenderedAtLeastOnce = true
        if (delayedZoomVariables != null) {
            setZoom(delayedZoomVariables?.scale ?: 1F, delayedZoomVariables?.focusX ?: 1F,
                    delayedZoomVariables?.focusY ?: 1F, delayedZoomVariables?.scaleType)
            delayedZoomVariables = null
        }
        super.onDraw(canvas)
    }
    
    public override fun onConfigurationChanged(newConfig:Configuration) {
        super.onConfigurationChanged(newConfig)
        savePreviousImageValues()
    }
    
    /**
     * Get the max zoom multiplier.
     *
     * @return max zoom multiplier.
     */
    fun getMaxZoom():Float = maxScale
    
    /**
     * Set the max zoom multiplier. Default value: 3.
     *
     * @param max
     * max zoom multiplier.
     */
    fun setMaxZoom(max:Float) {
        maxScale = max
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
    }
    
    /**
     * Get the min zoom multiplier.
     *
     * @return min zoom multiplier.
     */
    fun getMinZoom():Float = minScale
    
    /**
     * Set the min zoom multiplier. Default value: 1.
     *
     * @param min
     * min zoom multiplier.
     */
    fun setMinZoom(min:Float) {
        minScale = min
        superMinScale = SUPER_MIN_MULTIPLIER * minScale
    }
    
    /**
     * Get the current zoom. This is the zoom relative to the initial scale, not the original
     * resource.
     *
     * @return current zoom multiplier.
     */
    private fun getCurrentZoom():Float = normalizedScale
    
    /**
     * Reset zoom and translation to initial state.
     */
    private fun resetZoom() {
        normalizedScale = 1f
        fitImageToView()
    }
    
    /**
     * Set zoom to the specified scale. Image will be centered by default.
     */
    fun setZoom(scale:Float) {
        setZoom(scale, 0.5f, 0.5f)
    }
    
    /**
     * Set zoom to the specified scale. Image will be centered around the point (focusX, focusY).
     * These floats range from 0 to 1 and denote the focus point as a fraction from the left and top
     * of the view. For example, the top left corner of the image would be (0, 0). And the bottom
     * right corner would be (1, 1).
     */
    private fun setZoom(scale:Float, focusX:Float, focusY:Float) {
        setZoom(scale, focusX, focusY, mScaleType)
    }
    
    /**
     * Set zoom to the specified scale. Image will be centered around the point (focusX, focusY).
     * These floats range from 0 to 1 and denote the focus point as a fraction from the left and top
     * of the view. For example, the top left corner of the image would be (0, 0). And the bottom
     * right corner would be (1, 1).
     */
    private fun setZoom(scale:Float, focusX:Float, focusY:Float, scaleType:ImageView.ScaleType?) {
        //
        // setZoom can be called before the image is on the screen, but at this point,
        // image and view sizes have not yet been calculated in onMeasure. Thus, we should
        // delay calling setZoom until the view has been measured.
        //
        if (!onDrawReady) {
            delayedZoomVariables = ZoomVariables(scale, focusX, focusY, scaleType)
            return
        }
        if (scaleType != mScaleType) {
            setScaleType(scaleType)
        }
        resetZoom()
        scaleImage(scale.toDouble(), (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true)
        matrix?.getValues(m)
        m?.let {
            it[Matrix.MTRANS_X] = -(focusX * getImageWidth() - viewWidth * 0.5f)
            it[Matrix.MTRANS_Y] = -(focusY * getImageHeight() - viewHeight * 0.5f)
        }
        matrix?.setValues(m)
        fixTrans()
        imageMatrix = matrix
    }
    
    /**
     * Set zoom parameters equal to another ZoomableImageView. Including scale, position, and
     * ScaleType.
     */
    private fun setZoom(img:ZoomableImageView) {
        val center = img.getScrollPosition() ?: return
        setZoom(img.getCurrentZoom(), center.x, center.y, img.scaleType)
    }
    
    /**
     * Return the point at the center of the zoomed image. The PointF coordinates range in value
     * between 0 and 1 and the focus point is denoted as a fraction from the left and top of the
     * view. For example, the top left corner of the image would be (0, 0). And the bottom right
     * corner would be (1, 1).
     *
     * @return PointF representing the scroll position of the zoomed image.
     */
    private fun getScrollPosition():PointF? {
        val drawable = drawable ?: return null
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val point = transformCoordTouchToBitmap((viewWidth / 2).toFloat(),
                                                (viewHeight / 2).toFloat(), true)
        point.x /= drawableWidth.toFloat()
        point.y /= drawableHeight.toFloat()
        return point
    }
    
    /**
     * Set the focus point of the zoomed image. The focus points are denoted as a fraction from the
     * left and top of the view. The focus points can range in value between 0 and 1.
     */
    fun setScrollPosition(focusX:Float, focusY:Float) {
        setZoom(normalizedScale, focusX, focusY)
    }
    
    /**
     * Performs boundary checking and fixes the image matrix if it is out of bounds.
     */
    private fun fixTrans() {
        matrix?.getValues(m)
        val transX = m?.let { it[Matrix.MTRANS_X] } ?: 0F
        val transY = m?.let { it[Matrix.MTRANS_Y] } ?: 0F
        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), getImageWidth())
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), getImageHeight())
        if (fixTransX != 0f || fixTransY != 0f) {
            matrix?.postTranslate(fixTransX, fixTransY)
        }
    }
    
    /**
     * When transitioning from zooming from focus to zoom from center (or vice versa) the image can
     * become unaligned within the view. This is apparent when zooming quickly. When the content
     * size is less than the view size, the content will often be centered incorrectly within the
     * view. fixScaleTrans first calls fixTrans() and then makes sure the image is centered
     * correctly within the view.
     */
    private fun fixScaleTrans() {
        fixTrans()
        matrix?.getValues(m)
        m?.let {
            if (getImageWidth() < viewWidth) {
                it[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2
            }
            if (getImageHeight() < viewHeight) {
                it[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2
            }
        }
        matrix?.setValues(m)
    }
    
    private fun getFixTrans(trans:Float, viewSize:Float, contentSize:Float):Float {
        val minTrans:Float
        val maxTrans:Float
        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
            
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        if (trans < minTrans)
            return -trans + minTrans
        return if (trans > maxTrans) -trans + maxTrans else 0f
    }
    
    private fun getFixDragTrans(delta:Float, viewSize:Float, contentSize:Float):Float {
        return if (contentSize <= viewSize) {
            0f
        } else delta
    }
    
    private fun getImageWidth():Float = matchViewWidth * normalizedScale
    
    private fun getImageHeight():Float = matchViewHeight * normalizedScale
    
    override fun onMeasure(widthMeasureSpec:Int, heightMeasureSpec:Int) {
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            setMeasuredDimension(0, 0)
            return
        }
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        viewWidth = setViewSize(widthMode, widthSize, drawableWidth)
        
        viewHeight = setViewSize(heightMode, heightSize, drawableHeight)
        //
        // Set view dimensions
        //
        setMeasuredDimension(viewWidth, viewHeight)
        //
        // Fit content within view
        //
        fitImageToView()
    }
    
    /**
     * If the normalizedScale is equal to 1, then the image is made to fit the screen. Otherwise, it
     * is made to fit the screen according to the dimensions of the previous image matrix. This
     * allows the image to maintain its zoom after rotation.
     */
    private fun fitImageToView() {
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            return
        }
        if (matrix == null || prevMatrix == null) {
            return
        }
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        //
        // Scale image for view
        //
        var scaleX = viewWidth.toFloat() / drawableWidth
        var scaleY = viewHeight.toFloat() / drawableHeight
        when (mScaleType) {
            ScaleType.CENTER -> {
                scaleY = 1F
                scaleX = scaleY
            }
            ScaleType.CENTER_CROP -> {
                scaleY = Math.max(scaleX, scaleY)
                scaleX = scaleY
            }
            ScaleType.CENTER_INSIDE -> {
                scaleY = Math.min(1f, Math.min(scaleX, scaleY))
                scaleX = scaleY
                scaleY = Math.min(scaleX, scaleY)
                scaleX = scaleY
            }
            ScaleType.FIT_CENTER -> {
                scaleY = Math.min(scaleX, scaleY)
                scaleX = scaleY
            }
            ImageView.ScaleType.FIT_XY -> {
            }
            else ->
                //
                // FIT_START and FIT_END not supported
                //
                throw UnsupportedOperationException(
                        "ZoomableImageView does not support " + "FIT_START or FIT_END")
        }
        //
        // Center the image
        //
        val redundantXSpace = viewWidth - scaleX * drawableWidth
        val redundantYSpace = viewHeight - scaleY * drawableHeight
        matchViewWidth = viewWidth - redundantXSpace
        matchViewHeight = viewHeight - redundantYSpace
        if (!isZoomed() && !imageRenderedAtLeastOnce) {
            //
            // Stretch and center image to fit view
            //
            matrix?.setScale(scaleX, scaleY)
            matrix?.postTranslate(redundantXSpace / 2, redundantYSpace / 2)
            normalizedScale = 1f
            
        } else {
            //
            // These values should never be 0 or we will set viewWidth and viewHeight
            // to NaN in translateMatrixAfterRotate. To avoid this, call savePreviousImageValues
            // to set them equal to the current values.
            //
            if (prevMatchViewWidth == 0f || prevMatchViewHeight == 0f) {
                savePreviousImageValues()
            }
            prevMatrix?.getValues(m)
            //
            // Rescale Matrix after rotation
            //
            m?.let {
                it[Matrix.MSCALE_X] = matchViewWidth / drawableWidth * normalizedScale
                it[Matrix.MSCALE_Y] = matchViewHeight / drawableHeight * normalizedScale
            }
            //
            // TransX and TransY from previous matrix
            //
            val transX = m?.let { it[Matrix.MTRANS_X] } ?: 0F
            val transY = m?.let { it[Matrix.MTRANS_Y] } ?: 0F
            //
            // Width
            //
            val prevActualWidth = prevMatchViewWidth * normalizedScale
            val actualWidth = getImageWidth()
            translateMatrixAfterRotate(Matrix.MTRANS_X, transX, prevActualWidth, actualWidth,
                                       prevViewWidth, viewWidth, drawableWidth)
            //
            // Height
            //
            val prevActualHeight = prevMatchViewHeight * normalizedScale
            val actualHeight = getImageHeight()
            translateMatrixAfterRotate(Matrix.MTRANS_Y, transY, prevActualHeight, actualHeight,
                                       prevViewHeight, viewHeight, drawableHeight)
            //
            // Set the matrix to the adjusted scale and translate values.
            //
            matrix?.setValues(m)
        }
        fixTrans()
        imageMatrix = matrix
    }
    
    /**
     * Set view dimensions based on layout params
     */
    private fun setViewSize(mode:Int, size:Int, drawableWidth:Int):Int = when (mode) {
        View.MeasureSpec.EXACTLY -> size
        View.MeasureSpec.AT_MOST -> Math.min(drawableWidth, size)
        View.MeasureSpec.UNSPECIFIED -> drawableWidth
        else -> size
    }
    
    /**
     * After rotating, the matrix needs to be translated. This function finds the area of image
     * which was previously centered and adjusts translations so that is again the center,
     * post-rotation.
     *
     * @param axis
     * Matrix.MTRANS_X or Matrix.MTRANS_Y
     * @param trans
     * the value of trans in that axis before the rotation
     * @param prevImageSize
     * the width/height of the image before the rotation
     * @param imageSize
     * width/height of the image after rotation
     * @param prevViewSize
     * width/height of view before rotation
     * @param viewSize
     * width/height of view after rotation
     * @param drawableSize
     * width/height of drawable
     */
    private fun translateMatrixAfterRotate(axis:Int, trans:Float, prevImageSize:Float,
                                           imageSize:Float, prevViewSize:Int, viewSize:Int,
                                           drawableSize:Int) {
        when {
            imageSize < viewSize -> //
                // The width/height of image is less than the view's width/height. Center it.
                //
                m?.let { it[axis] = (viewSize - drawableSize * (m?.let { it[Matrix.MSCALE_X] } ?: 0F)) * 0.5f }
            trans > 0 -> //
                // The image is larger than the view, but was not before rotation. Center it.
                //
                m?.let { it[axis] = -((imageSize - viewSize) * 0.5f) }
            else -> {
                //
                // Find the area of the image which was previously centered in the view. Determine
                // its distance
                // from the left/top side of the view as a fraction of the entire image's
                // width/height. Use that percentage
                // to calculate the trans in the new view width/height.
                //
                val percentage = (Math.abs(trans) + 0.5f * prevViewSize) / prevImageSize
                m?.let { it[axis] = -(percentage * imageSize - viewSize * 0.5f) }
            }
        }
    }
    
    private fun setState(state:State) {
        this.state = state
    }
    
    fun canScrollHorizontallyFroyo(direction:Int):Boolean = canScrollHorizontally(direction)
    
    override fun canScrollHorizontally(direction:Int):Boolean {
        matrix?.getValues(m)
        val x = m?.let { it[Matrix.MTRANS_X] } ?: 0F
        if (getImageWidth() < viewWidth) {
            return false
        } else if (x >= -1 && direction < 0) {
            return false
        } else if (Math.abs(x) + viewWidth.toFloat() + 1f >= getImageWidth() && direction > 0) {
            return false
        }
        return true
    }
    
    private fun scaleImage(deltaScale:Double, focusX:Float, focusY:Float,
                           stretchImageToSuper:Boolean) {
        val lowerScale:Float
        val upperScale:Float
        if (stretchImageToSuper) {
            lowerScale = superMinScale
            upperScale = superMaxScale
            
        } else {
            lowerScale = minScale
            upperScale = maxScale
        }
        val origScale = normalizedScale
        normalizedScale *= deltaScale.toFloat()
        val fDeltaScale = when {
            normalizedScale > upperScale -> {
                normalizedScale = upperScale
                (upperScale / origScale)
            }
            normalizedScale < lowerScale -> {
                normalizedScale = lowerScale
                (lowerScale / origScale)
            }
            else -> 0F
        }
        matrix?.postScale(fDeltaScale, fDeltaScale, focusX, focusY)
        fixScaleTrans()
    }
    
    /**
     * This function will transform the coordinates in the touch event to the coordinate system of
     * the drawable that the imageview contain
     *
     * @param x
     * x-coordinate of touch event
     * @param y
     * y-coordinate of touch event
     * @param clipToBitmap
     * Touch event may occur within view, but outside image content. True, to clip return
     * value to the bounds of the bitmap size.
     * @return Coordinates of the point touched, in the coordinate system of the original drawable.
     */
    private fun transformCoordTouchToBitmap(x:Float, y:Float, clipToBitmap:Boolean):PointF {
        matrix?.getValues(m)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val transX = m?.let { it[Matrix.MTRANS_X] } ?: 0F
        val transY = m?.let { it[Matrix.MTRANS_Y] } ?: 0F
        var finalX = (x - transX) * origW / getImageWidth()
        var finalY = (y - transY) * origH / getImageHeight()
        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0f), origW)
            finalY = Math.min(Math.max(finalY, 0f), origH)
        }
        return PointF(finalX, finalY)
    }
    
    /**
     * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
     * drawable's coordinate system to the view's coordinate system.
     *
     * @param bx
     * x-coordinate in original bitmap coordinate system
     * @param by
     * y-coordinate in original bitmap coordinate system
     * @return Coordinates of the point in the view's coordinate system.
     */
    private fun transformCoordBitmapToTouch(bx:Float, by:Float):PointF {
        matrix?.getValues(m)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val px = bx / origW
        val py = by / origH
        val finalX = (m?.let { it[Matrix.MTRANS_X] } ?: 0F) + getImageWidth() * px
        val finalY = (m?.let { it[Matrix.MTRANS_Y] } ?: 0F) + getImageHeight() * py
        return PointF(finalX, finalY)
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun compatPostOnAnimation(runnable:Runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postOnAnimation(runnable)
        } else {
            postDelayed(runnable, (1000 / 60).toLong())
        }
    }
    
    private fun printMatrixInfo() {
        val n = FloatArray(9)
        matrix?.getValues(n)
        Log.d(DEBUG, "Scale: " + n[Matrix.MSCALE_X] + " TransX: " + n[Matrix.MTRANS_X] + " " +
                "TransY: " + n[Matrix.MTRANS_Y])
    }
    
    private enum class State {
        NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM
    }
    
    interface OnZoomableImageViewListener {
        fun onMove()
    }
    
    interface OnSingleTapListener {
        fun onSingleTap():Boolean
    }
    
    /**
     * Gesture Listener detects a single click or long click and passes that on to the view's
     * listener.
     *
     * @author Ortiz
     */
    private inner class GestureListener:GestureDetector.SimpleOnGestureListener() {
        
        override fun onSingleTapConfirmed(e:MotionEvent):Boolean {
            if (doubleTapListener != null) {
                return doubleTapListener?.onSingleTapConfirmed(e) ?: true
            }
            return if (singleTapListener != null) {
                singleTapListener?.onSingleTap() ?: true
            } else performClick()
        }
        
        override fun onLongPress(e:MotionEvent) {
            performLongClick()
        }
        
        override fun onFling(e1:MotionEvent, e2:MotionEvent, velocityX:Float,
                             velocityY:Float):Boolean {
            if (fling != null) {
                //
                // If a previous fling is still active, it should be cancelled so that two flings
                // are not run simultaenously.
                //
                fling?.cancelFling()
            }
            fling = Fling(velocityX.toInt(), velocityY.toInt())
            fling?.let { compatPostOnAnimation(it) }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
        
        override fun onDoubleTap(e:MotionEvent):Boolean {
            var consumed = false
            if (doubleTapListener != null) {
                consumed = doubleTapListener?.onDoubleTap(e) ?: true
            }
            if (state == State.NONE) {
                val targetZoom = if (normalizedScale == minScale) maxScale else minScale
                val doubleTap = DoubleTapZoom(targetZoom, e.x, e.y, false)
                compatPostOnAnimation(doubleTap)
                consumed = true
            }
            return consumed
        }
        
        override fun onDoubleTapEvent(e:MotionEvent):Boolean =
                doubleTapListener != null && (doubleTapListener?.onDoubleTapEvent(e) ?: true)
    }
    
    /**
     * Responsible for all touch events. Handles the heavy lifting of drag and also sends touch
     * events to Scale Detector and Gesture Detector.
     *
     * @author Ortiz
     */
    private inner class PrivateOnTouchListener:View.OnTouchListener {
        
        //
        // Remember last point position for dragging
        //
        private val last = PointF()
        
        override fun onTouch(v:View, event:MotionEvent):Boolean {
            mScaleDetector?.onTouchEvent(event)
            mGestureDetector?.onTouchEvent(event)
            val curr = PointF(event.x, event.y)
            if (state == State.NONE || state == State.DRAG || state == State.FLING) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        last.set(curr)
                        fling?.cancelFling()
                        setState(State.DRAG)
                    }
                    MotionEvent.ACTION_MOVE -> if (state == State.DRAG) {
                        val deltaX = curr.x - last.x
                        val deltaY = curr.y - last.y
                        val fixTransX = getFixDragTrans(deltaX, viewWidth.toFloat(),
                                                        getImageWidth())
                        val fixTransY = getFixDragTrans(deltaY, viewHeight.toFloat(),
                                                        getImageHeight())
                        matrix?.postTranslate(fixTransX, fixTransY)
                        fixTrans()
                        last.set(curr.x, curr.y)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> setState(State.NONE)
                }
            }
            imageMatrix = matrix
            //
            // User-defined OnTouchListener
            //
            userTouchListener?.onTouch(v, event)
            //
            // OnZoomableImageViewListener is set: ZoomableImageView dragged by user.
            //
            zoomableImageViewListener?.onMove()
            //
            // indicate event was handled
            //
            return true
        }
    }
    
    /**
     * ScaleListener detects user two finger scaling and scales image.
     *
     * @author Ortiz
     */
    private inner class ScaleListener:ScaleGestureDetector.SimpleOnScaleGestureListener() {
        
        override fun onScaleBegin(detector:ScaleGestureDetector):Boolean {
            setState(State.ZOOM)
            return true
        }
        
        override fun onScale(detector:ScaleGestureDetector):Boolean {
            scaleImage(detector.scaleFactor.toDouble(), detector.focusX, detector.focusY, true)
            //
            // OnzoomableImageViewListener is set: ZoomableImageView pinch zoomed by user.
            //
            zoomableImageViewListener?.onMove()
            return true
        }
        
        override fun onScaleEnd(detector:ScaleGestureDetector) {
            super.onScaleEnd(detector)
            setState(State.NONE)
            var animateToZoomBoundary = false
            var targetZoom = normalizedScale
            if (normalizedScale > maxScale) {
                targetZoom = maxScale
                animateToZoomBoundary = true
                
            } else if (normalizedScale < minScale) {
                targetZoom = minScale
                animateToZoomBoundary = true
            }
            if (animateToZoomBoundary) {
                val doubleTap = DoubleTapZoom(targetZoom, (viewWidth / 2).toFloat(),
                                              (viewHeight / 2).toFloat(), true)
                compatPostOnAnimation(doubleTap)
            }
        }
    }
    
    /**
     * DoubleTapZoom calls a series of runnables which apply an animated zoom in/out graphic to the
     * image.
     *
     * @author Ortiz
     */
    private inner class DoubleTapZoom internal constructor(private val targetZoom:Float,
                                                           focusX:Float, focusY:Float,
                                                           private val stretchImageToSuper:Boolean):Runnable {
        private val startTime:Long
        private val startZoom:Float
        private val bitmapX:Float
        private val bitmapY:Float
        private val interpolator = AccelerateDecelerateInterpolator()
        private val startTouch:PointF
        private val endTouch:PointF
        
        init {
            setState(State.ANIMATE_ZOOM)
            startTime = System.currentTimeMillis()
            this.startZoom = normalizedScale
            val bitmapPoint = transformCoordTouchToBitmap(focusX, focusY, false)
            this.bitmapX = bitmapPoint.x
            this.bitmapY = bitmapPoint.y
            //
            // Used for translating image during scaling
            //
            startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY)
            endTouch = PointF((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        }
        
        override fun run() {
            val t = interpolate()
            val deltaScale = calculateDeltaScale(t)
            scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper)
            translateImageToCenterTouchPosition(t)
            fixScaleTrans()
            imageMatrix = matrix
            //
            // OnzoomableImageViewListener is set: double tap runnable updates listener
            // with every frame.
            //
            zoomableImageViewListener?.onMove()
            if (t < 1f) {
                //
                // We haven't finished zooming
                //
                compatPostOnAnimation(this)
                
            } else {
                //
                // Finished zooming
                //
                setState(State.NONE)
            }
        }
        
        /**
         * Interpolate between where the image should start and end in order to translate the image
         * so that the point that is touched is what ends up centered at the end of the zoom.
         */
        private fun translateImageToCenterTouchPosition(t:Float) {
            val targetX = startTouch.x + t * (endTouch.x - startTouch.x)
            val targetY = startTouch.y + t * (endTouch.y - startTouch.y)
            val curr = transformCoordBitmapToTouch(bitmapX, bitmapY)
            matrix?.postTranslate(targetX - curr.x, targetY - curr.y)
        }
        
        /**
         * Use interpolator to get t
         */
        private fun interpolate():Float {
            val currTime = System.currentTimeMillis()
            var elapsed = (currTime - startTime) / ZOOM_TIME
            elapsed = Math.min(1f, elapsed)
            return interpolator.getInterpolation(elapsed)
        }
        
        /**
         * Interpolate the current targeted zoom and get the delta from the current zoom.
         */
        private fun calculateDeltaScale(t:Float):Double {
            val zoom = (startZoom + t * (targetZoom - startZoom)).toDouble()
            return zoom / normalizedScale
        }
        
        private val ZOOM_TIME = 500f
    }
    
    /**
     * Fling launches sequential runnables which apply the fling graphic to the image. The values
     * for the translation are interpolated by the Scroller.
     *
     * @author Ortiz
     */
    private inner class Fling internal constructor(velocityX:Int, velocityY:Int):Runnable {
        
        private var scroller:CompatScroller? = null
        private var currX:Int = 0
        private var currY:Int = 0
        
        init {
            setState(State.FLING)
            scroller = CompatScroller(context)
            matrix?.getValues(m)
            val startX = m?.let { it[Matrix.MTRANS_X].toInt() } ?: 0
            val startY = m?.let { it[Matrix.MTRANS_Y].toInt() } ?: 0
            val minX:Int
            val maxX:Int
            val minY:Int
            val maxY:Int
            if (getImageWidth() > viewWidth) {
                minX = viewWidth - getImageWidth().toInt()
                maxX = 0
            } else {
                maxX = startX
                minX = maxX
            }
            if (getImageHeight() > viewHeight) {
                minY = viewHeight - getImageHeight().toInt()
                maxY = 0
            } else {
                maxY = startY
                minY = maxY
            }
            scroller?.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
            currX = startX
            currY = startY
        }
        
        fun cancelFling() {
            if (scroller != null) {
                setState(State.NONE)
                scroller?.forceFinished(true)
            }
        }
        
        override fun run() {
            //
            // OnzoomableImageViewListener is set: ZoomableImageView listener has been flung by user.
            // Listener runnable updated with each frame of fling animation.
            //
            zoomableImageViewListener?.onMove()
            val scrollerFinished = scroller?.isFinished ?: false
            if (scrollerFinished) {
                scroller = null
                return
            }
            scroller?.let {
                if (it.computeScrollOffset()) {
                    val newX = it.currX
                    val newY = it.currY
                    val transX = newX - currX
                    val transY = newY - currY
                    currX = newX
                    currY = newY
                    matrix?.postTranslate(transX.toFloat(), transY.toFloat())
                    fixTrans()
                    imageMatrix = matrix
                    compatPostOnAnimation(this)
                }
            }
        }
    }
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private inner class CompatScroller(context:Context) {
        private val overScroller:OverScroller = OverScroller(context)
        
        fun fling(startX:Int, startY:Int, velocityX:Int, velocityY:Int, minX:Int, maxX:Int,
                  minY:Int, maxY:Int) {
            overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
        }
        
        fun forceFinished(finished:Boolean) {
            overScroller.forceFinished(finished)
        }
        
        val isFinished:Boolean
            get() = overScroller.isFinished
        
        fun computeScrollOffset():Boolean {
            overScroller.computeScrollOffset()
            return overScroller.computeScrollOffset()
        }
        
        val currX:Int
            get() = overScroller.currX
        
        val currY:Int
            get() = overScroller.currY
    }
    
    private inner class ZoomVariables(val scale:Float, val focusX:Float, val focusY:Float,
                                      val scaleType:ImageView.ScaleType?)
}