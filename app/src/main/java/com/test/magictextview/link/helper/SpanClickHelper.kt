package com.test.magictextview.link.helper

import android.text.Selection
import android.text.Spannable
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import com.test.magictextview.link.interfaces.IPressedSpan

/**
 * 链接触摸事件帮助类
 * @author zhaotk
 */
class SpanClickHelper {
    private var mPressedSpan: IPressedSpan? = null

    fun onTouchEvent(
        textView: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPressedSpan = getPressedSpan(textView, spannable, event)
                if (mPressedSpan != null) {
                    //手指按下 设置按下为true，修改对应的链接文字背景颜色
                    mPressedSpan!!.setPressed(true)
                    //设置选中区域
                    Selection.setSelection(
                        spannable, spannable.getSpanStart(mPressedSpan),
                        spannable.getSpanEnd(mPressedSpan)
                    )
                }

                mPressedSpan != null
            }
            MotionEvent.ACTION_MOVE -> {
                val touchedSpan = getPressedSpan(textView, spannable, event)
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    //手指移动时 设置按下为false，对应的链接文字背景颜色置回透明
                    mPressedSpan!!.setPressed(false)
                    mPressedSpan = null
                    //移除选中区域
                    Selection.removeSelection(spannable)
                }
                mPressedSpan != null
            }
            MotionEvent.ACTION_UP -> {
                var touchSpanHint = false
                if (mPressedSpan != null) {
                    touchSpanHint = true
                    //手指抬起时 设置按下为false，对应的链接文字背景颜色置回透明
                    mPressedSpan!!.setPressed(false)
                    //传递点击事件回调
                    mPressedSpan!!.onClick(textView)
                }
                mPressedSpan = null
                Selection.removeSelection(spannable)
                touchSpanHint
            }
            else -> {
                if (mPressedSpan != null) {
                    //其它收拾 都设置按下为false,对应的链接文字背景颜色置回透明
                    mPressedSpan!!.setPressed(false)
                }
                //移除选中区域
                Selection.removeSelection(spannable)
                false
            }
        }
    }

    /**
     * 判断手指是否点击在链接上
     */
    private fun getPressedSpan(
        textView: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): IPressedSpan? {
        var x = event.x.toInt()
        var y = event.y.toInt()
        x -= textView.totalPaddingLeft
        y -= textView.totalPaddingTop
        x += textView.scrollX
        y += textView.scrollY
        val layout = textView.layout
        val line = layout.getLineForVertical(y)

        try {
            var off = layout.getOffsetForHorizontal(line, x.toFloat())
            if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) {
                // 实际上没点到任何内容
                off = -1
            }
            val link = spannable.getSpans(
                off, off,
                IPressedSpan::class.java
            )
            var touchedSpan: IPressedSpan? = null
            if (link.isNotEmpty()) {
                touchedSpan = link[0]
            }
            return touchedSpan
        } catch (e: IndexOutOfBoundsException) {
            Log.d(this.toString(), "getPressedSpan", e)

        }
        return null
    }

}