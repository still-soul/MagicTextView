package com.test.magictextview.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import androidx.annotation.NonNull

class MyImageSpan(drawable: Drawable?) : ImageSpan(drawable!!) {

    override fun draw(
        @NonNull canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        @NonNull paint: Paint
    ) {
        val b = drawable
        val fm = paint.fontMetricsInt
        //计算y方向的位移
        val transY = (y + fm.descent + y + fm.ascent) / 2 - b.bounds.bottom / 2
        canvas.save()
        //绘制图片位移一段距离
        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }
}