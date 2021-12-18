package com.test.magictextview.like.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.test.magictextview.like.factory.BitmapProvider
import com.test.magictextview.utils.PublicMethod

/**
 * 数字和等级文案view
 * @author xiaoman
 */
class NumberLevelView @JvmOverloads constructor(
    context: Context,
    private val provider: BitmapProvider.Provider?,
    private val x: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var textPaint: Paint = Paint()

    /**
     * 点击次数
     */
    private var mNumber = 0

    /**
     * 等级文案图片
     */
    private var bitmapTalk: Bitmap? = null

    /**
     * 等级
     */
    private var level = 0

    /**
     * 数字图片宽度
     */
    private var numberImageWidth = 0

    /**
     * 数字图片的总宽度
     */
    private var offsetX = 0

    /**
     * x 初始位置
     */
    private var initialValue = 0

    /**
     * 默认数字和等级文案图片间距
     */
    private var spacing = 0

    init {
        textPaint.isAntiAlias = true
        initialValue = x - PublicMethod.dp2px(context, 120f)
        numberImageWidth = provider?.getNumberBitmap(1)?.width ?: 0
        spacing = PublicMethod.dp2px(context, 10f)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val levelBitmap = provider?.getLevelBitmap(level) ?: return
        //等级图片的宽度
        val levelBitmapWidth = levelBitmap.width

        val dst = Rect()
        when (mNumber) {
            in 0..9 -> {
                initialValue = x - levelBitmapWidth
                dst.left =  initialValue
                dst.right = initialValue + levelBitmapWidth
            }
            in 10..99 -> {
                initialValue  = x - PublicMethod.dp2px(context, 100f)
                dst.left =  initialValue + numberImageWidth + spacing
                dst.right = initialValue+ numberImageWidth  + spacing+ levelBitmapWidth
            }
            else -> {
                initialValue = x - PublicMethod.dp2px(context, 120f)
                dst.left =  initialValue + 2*numberImageWidth + spacing
                dst.right = initialValue+ 2*numberImageWidth + spacing + levelBitmapWidth
            }
        }
        dst.top = 0
        dst.bottom = levelBitmap.height
        //绘制等级文案图标
        canvas.drawBitmap(levelBitmap, null, dst, textPaint)

        while (mNumber > 0) {
            val number = mNumber % 10
            val bitmap = provider.getNumberBitmap(number)?:continue
            offsetX += bitmap.width
            //这里是数字
            val rect = Rect()
            rect.top = 0
            when {
                mNumber/ 10 < 1 -> {
                    rect.left = initialValue - bitmap.width
                    rect.right = initialValue
                }
                mNumber/ 10 in 1..9 -> {
                    rect.left = initialValue
                    rect.right = initialValue + bitmap.width
                }
                else -> {
                    rect.left = initialValue +  bitmap.width
                    rect.right = initialValue +2* bitmap.width
                }
            }

            rect.bottom = bitmap.height
            //绘制数字
            canvas.drawBitmap(bitmap, null, rect, textPaint)
            mNumber /= 10
        }

    }

    fun setNumber(number: Int) {
        this.mNumber = number
        if (mNumber >999){
            mNumber = 999
        }
        level = when (mNumber) {
            in 1..20 -> {
                0
            }
            in 21..80 -> {
                1
            }
            else -> {
                2
            }
        }
        //根据等级取出等级文案图标
        bitmapTalk = provider?.getLevelBitmap(level)
        invalidate()
    }


}