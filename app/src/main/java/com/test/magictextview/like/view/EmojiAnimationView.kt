package com.test.magictextview.like.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.test.magictextview.like.factory.BitmapProvider

/**
 * 表情动画view
 * @author xiaoman
 */
class EmojiAnimationView @JvmOverloads constructor(
    context: Context,
    private val provider: BitmapProvider.Provider?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mThumbImage: Bitmap? = null
    private var mBitmapPaint: Paint? = null
    private var mAnimatorListener: AnimatorListener? = null

    /**
     * 表情图标的宽度
     */
    private var emojiWith = 0

    /**
     * 表情图标的高度
     */
    private var emojiHeight = 0


    private fun init() {
        //初始化图片，取出随机图标
        mThumbImage = provider?.randomBitmap
    }

    init {
        //初始化paint
        mBitmapPaint = Paint()
        mBitmapPaint?.isAntiAlias = true
    }

    /**
     * 设置动画
     */
    private fun showAnimation() {
        val imageWidth = mThumbImage?.width ?:0
        val imageHeight = mThumbImage?.height ?:0
        val topX = -1080 + (1400 * Math.random()).toFloat()
        val topY = -300 + (-700 * Math.random()).toFloat()
        //上升动画
        val translateAnimationX = ObjectAnimator.ofFloat(this, "translationX", 0f, topX)
        translateAnimationX.duration = DURATION.toLong()
        translateAnimationX.interpolator = LinearInterpolator()
        val translateAnimationY = ObjectAnimator.ofFloat(this, "translationY", 0f, topY)
        translateAnimationY.duration = DURATION.toLong()
        translateAnimationY.interpolator = DecelerateInterpolator()
        //表情图片的大小变化
        val translateAnimationRightLength = ObjectAnimator.ofInt(
            this, "emojiWith",
            0,imageWidth,imageWidth,imageWidth,imageWidth, imageWidth, imageWidth, imageWidth, imageWidth, imageWidth
        )
        translateAnimationRightLength.duration = DURATION.toLong()
        val translateAnimationBottomLength = ObjectAnimator.ofInt(
            this, "emojiHeight",
            0,imageHeight,imageHeight,imageHeight,imageHeight,imageHeight, imageHeight, imageHeight, imageHeight, imageHeight
        )
        translateAnimationBottomLength.duration = DURATION.toLong()
        translateAnimationRightLength.addUpdateListener {
            invalidate()
        }
        //透明度变化
        val alphaAnimation = ObjectAnimator.ofFloat(
            this,
            "alpha",
            0.8f,
            1.0f,
            1.0f,
            1.0f,
            0.9f,
            0.8f,
            0.8f,
            0.7f,
            0.6f,
            0f
        )
        alphaAnimation.duration = DURATION.toLong()
        //动画集合
        val animatorSet = AnimatorSet()
        animatorSet.play(translateAnimationX).with(translateAnimationY)
            .with(translateAnimationRightLength).with(translateAnimationBottomLength)
            .with(alphaAnimation)

        //下降动画
        val translateAnimationXDown =
            ObjectAnimator.ofFloat(this, "translationX", topX, topX * 1.2f)
        translateAnimationXDown.duration = (DURATION / 5).toLong()
        translateAnimationXDown.interpolator = LinearInterpolator()
        val translateAnimationYDown =
            ObjectAnimator.ofFloat(this, "translationY", topY, topY * 0.8f)
        translateAnimationYDown.duration = (DURATION / 5).toLong()
        translateAnimationYDown.interpolator = AccelerateInterpolator()
        //设置动画播放顺序
        val animatorSetDown = AnimatorSet()
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animatorSetDown.play(translateAnimationXDown).with(translateAnimationYDown)
                animatorSetDown.start()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSetDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                //动画完成后通知移除动画view
                mAnimatorListener?.onAnimationEmojiEnd()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawEmojiImage(canvas)
    }

    /**
     * 绘制表情图片
     */
    private fun drawEmojiImage(canvas: Canvas) {
        mThumbImage?.let{
            val dst = Rect()
            dst.left = 0
            dst.top = 0
            dst.right = emojiWith
            dst.bottom = emojiHeight
            canvas.drawBitmap(it, null, dst, mBitmapPaint)
        }

    }

    /**
     * 这些get\set方法用于表情图标的大小动画
     * 不能删除
     */
    fun getEmojiWith(): Int {
        return emojiWith
    }

    fun setEmojiWith(emojiWith: Int) {
        this.emojiWith = emojiWith
    }

    fun getEmojiHeight(): Int {
        return emojiHeight
    }

    fun setEmojiHeight(emojiHeight: Int) {
        this.emojiHeight = emojiHeight
    }

    fun setEmojiAnimation() {
        showAnimation()
    }

    fun setAnimatorListener(animatorListener: AnimatorListener?) {
        mAnimatorListener = animatorListener
    }

    interface AnimatorListener {
        /**
         *  动画结束
         */
        fun onAnimationEmojiEnd()
    }


    fun setEmoji() {
        init()
    }

    companion object {
        //动画时长
        const val DURATION = 500
    }
}
