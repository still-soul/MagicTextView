package com.test.magictextview.link

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.test.magictextview.link.helper.MyLinkMovementMethod

import com.test.magictextview.link.interfaces.IPressedSpan

/**
 * 显示链接的textView
 * @author zhaotk
 */
class LinkTextView(context: Context, attrs: AttributeSet?) :
    AppCompatTextView(context, attrs) {
    private var mPressedSpan: IPressedSpan? = null
    init {
        isFocusable = false
        isLongClickable = false
//        有链接点击需求不设置则点击无效
        movementMethod = MyLinkMovementMethod.instance
        highlightColor = Color.TRANSPARENT
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val text = text
        val spannable = Spannable.Factory.getInstance().newSpannable(text)
        if (event.action == MotionEvent.ACTION_DOWN) {
            //按下时记下clickSpan
            mPressedSpan = getPressedSpan(this, spannable, event)
        }
        return if (mPressedSpan != null) {
            //如果有clickSpan就走MyLinkMovementMethod的onTouchEvent
            MyLinkMovementMethod.instance.onTouchEvent(this, getText() as Spannable, event)
        } else {
            super.onTouchEvent(event)
        }
    }

    private fun getPressedSpan(
        textView: TextView, spannable: Spannable,
        event: MotionEvent
    ): IPressedSpan? {
        var mTouchSpan: IPressedSpan? = null
        var x = event.x.toInt()
        var y = event.y.toInt()
        x -= textView.totalPaddingLeft
        x += textView.scrollX
        y -= textView.totalPaddingTop
        y += textView.scrollY
        val layout = textView.layout
        val line = layout.getLineForVertical(y)
        try {
            var off = layout.getOffsetForHorizontal(line, x.toFloat())
            if (x < layout.getLineLeft(line) || x > layout.getLineRight(line)) {
                // 实际上没点到任何内容
                off = -1
            }
            val linkSpans =
                spannable.getSpans(off, off, IPressedSpan::class.java)
            if (!linkSpans.isNullOrEmpty()) {
                mTouchSpan = linkSpans[0]
            }
            return mTouchSpan
        } catch (e: IndexOutOfBoundsException) {
            Log.d(this.toString(), "getPressedSpan", e)
        }
        return null
    }


}