package com.test.magictextview.round

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.test.magictextview.R

/**
 * res/drawable 中的shape文件动态设置
 * @author xiaoman
 */
class RoundButtonDrawable : GradientDrawable() {
    private var mStrokeWidth = 0

    /**
     * 设置描边宽度和颜色
     */
    fun setStrokeData(width: Int, color: Int) {
        mStrokeWidth = width
        setStroke(width, color)
    }

    fun setStrokeColor(color: Int) {
        setStrokeData(mStrokeWidth, color)
    }

    companion object {
        @JvmStatic
        fun fromAttrSet(context: Context, attrs: AttributeSet?, defStyleAttr: Int): RoundButtonDrawable {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundButton, defStyleAttr, 0)
            val bgColor = typedArray.getColor(R.styleable.RoundButton_roundBgColor, ContextCompat.getColor(context, R.color.white))
            val mRadius = typedArray.getDimensionPixelSize(R.styleable.RoundButton_roundRadius, 0)
            val mTopLeftRadius = typedArray.getDimensionPixelSize(R.styleable.RoundButton_topLeftRadius, 0)
            val mTopRightRadius = typedArray.getDimensionPixelSize(R.styleable.RoundButton_topRightRadius, 0)
            val mBottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.RoundButton_bottomLeftRadius, 0)
            val mBottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.RoundButton_bottomRightRadius, 0)
            val strokeColor = typedArray.getColor(R.styleable.RoundButton_roundStrokeColor, ContextCompat.getColor(context, R.color.white))
            val strokeWidth = typedArray.getDimensionPixelSize(R.styleable.RoundButton_roundStrokeWidth, 0)
            typedArray.recycle()
            val roundButtonDrawable = RoundButtonDrawable()
            //设置背景颜色
            roundButtonDrawable.setColor(bgColor)
            //优先设置指定的圆角
            if (mTopLeftRadius > 0 || mTopRightRadius > 0 || mBottomLeftRadius > 0 || mBottomRightRadius > 0) {
                val radii = floatArrayOf(
                        mTopLeftRadius.toFloat(), mTopLeftRadius.toFloat(),
                        mTopRightRadius.toFloat(), mTopRightRadius.toFloat(),
                        mBottomRightRadius.toFloat(), mBottomRightRadius.toFloat(),
                        mBottomLeftRadius.toFloat(), mBottomLeftRadius
                        .toFloat())
                roundButtonDrawable.cornerRadii = radii
            } else {
                roundButtonDrawable.cornerRadius = mRadius.toFloat()
            }
            //设置描边的宽度和颜色
            roundButtonDrawable.setStrokeData(strokeWidth, strokeColor)
            return roundButtonDrawable
        }
    }
}