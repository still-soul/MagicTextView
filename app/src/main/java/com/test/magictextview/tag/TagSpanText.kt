package com.test.magictextview.tag

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.text.style.ReplacementSpan

/**
 * 自定义textView tag
 * @author xiaoman
 */
class TagSpanText(
        /**
         * 文字颜色
         */
        private val mTextColor: Int,
        /**
         * 背景颜色/描边颜色
         */
        private val mRectColor: Int,
        /**
         * 文字大小
         */
        private val mTextSize: Int,
        /**
         * 背景框圆角
         */
        private val mRadius: Int,
        /**
         * 右边距
         */
        private val mRightMargin: Int,
        /**
         * 描边宽度
         */
        private val mStrokeWidth: Int,
        /**
         * 文字左右内边距
         */
        private val mPaddingHorizontal: Int,
        /**
         * 背景画笔style
         */
        private val mPaintStyle: Paint.Style) : ReplacementSpan() {
    private var mSize = 0
    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        paint.textSize = mTextSize.toFloat()
        mSize = paint.measureText(text, start, end).toInt() + mRightMargin + 2 * mPaddingHorizontal
        return mSize
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        drawTagRect(canvas, x, y, paint)
        drawTagText(canvas, text, start, end, y, paint)
    }

    private fun drawTagRect(canvas: Canvas, x: Float, y: Int, paint: Paint) {
        paint.color = mRectColor
        paint.isAntiAlias = true
        val fontMetrics = paint.fontMetricsInt
        paint.strokeWidth = mStrokeWidth.toFloat()
        val oval = RectF(x + mStrokeWidth + 0.5f, (y + fontMetrics.ascent - fontMetrics.descent).toFloat(), x + mSize + mStrokeWidth + 0.5f - mRightMargin, (y + fontMetrics.descent).toFloat())
        paint.style = mPaintStyle
        canvas.drawRoundRect(oval, mRadius.toFloat(), mRadius.toFloat(), paint)
    }

    private fun drawTagText(canvas: Canvas, text: CharSequence, start: Int, end: Int, y: Int, paint: Paint) {
        paint.textSize = mTextSize.toFloat()
        paint.color = mTextColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        val fontMetrics = paint.fontMetricsInt
        val textCenterX = (mSize - mRightMargin) / 2
        val textBaselineY = (y - fontMetrics.descent - fontMetrics.ascent) / 2 + fontMetrics.descent
        val tag = text.subSequence(start, end).toString()
        canvas.drawText(tag, textCenterX.toFloat(), textBaselineY.toFloat(), paint)
    }
}