package com.test.magictextview.round

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.test.magictextview.R
import com.test.magictextview.round.RoundButtonDrawable.Companion.fromAttrSet
import com.test.magictextview.utils.ViewHelperUtils.setBackgroundKeepingPadding

/**
 * 可以设置背景色、指定圆角、描边的宽度和颜色
 * @author xiaoman
 */
class RoundButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var roundButtonDrawable: RoundButtonDrawable? = null

    init {
        roundButtonDrawable = fromAttrSet(context, attrs, defStyleAttr)
        setBackgroundKeepingPadding(this, roundButtonDrawable)
    }

    /**
     * 设置背景颜色
     * @param color
     */
    override fun setBackgroundColor(color: Int) {
        roundButtonDrawable?.setColor(color)
    }

    /**
     * 设置描边的宽度和颜色
     * @param width
     * @param color
     */
    fun setStrokeData(width: Int, color: Int) {
        roundButtonDrawable?.setStrokeData(width, color)
    }

    /**
     * 设置描边颜色
     * @param color
     */
    fun setStrokeColors(color: Int) {
        roundButtonDrawable?.setStrokeColor(color)
    }

    /**
     * 设置四个角的半径
     * @param radius
     */
    fun setRadius(radius: Int) {
        roundButtonDrawable?.cornerRadius = radius.toFloat()
    }

    /**
     * 设置 每一个角的半径
     * @param topLeftRadius     左上角半径
     * @param topRightRadius    右上角半径
     * @param bottomLeftRadius  左下角半径
     * @param bottomRightRadius 右下角半径
     * The corners are ordered top-left, top-right, bottom-right, bottom-left
     */
    fun setEachCornerRadius(topLeftRadius: Int, topRightRadius: Int, bottomLeftRadius: Int, bottomRightRadius: Int) {
        val radius = floatArrayOf(
                topLeftRadius.toFloat(), topLeftRadius.toFloat(),
                topRightRadius.toFloat(), topRightRadius.toFloat(),
                bottomRightRadius.toFloat(), bottomRightRadius.toFloat(),
                bottomLeftRadius.toFloat(), bottomLeftRadius
                .toFloat())
        roundButtonDrawable?.cornerRadii = radius
    }

    /**
     * 设置渐变
     * @param gradientType 渐变类型
     * @param orientation  渐变方向
     * @param colors       渐变颜色
     */
    fun setGradient(gradientType: Int, orientation: GradientDrawable.Orientation?, colors: IntArray?) {
        roundButtonDrawable?.gradientType = gradientType
        roundButtonDrawable?.orientation = orientation
        roundButtonDrawable?.colors = colors
    }
}