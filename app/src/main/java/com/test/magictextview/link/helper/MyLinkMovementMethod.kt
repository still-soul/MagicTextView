package com.test.magictextview.link.helper

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.view.MotionEvent
import android.widget.TextView

/**
 * 禁止textView滑动，这里只保留了点击响应，
 * @author zhaotk
 */
class MyLinkMovementMethod : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        return (sHelper.onTouchEvent(widget, buffer, event))
    }

    companion object {
        val instance: MyLinkMovementMethod?
            get() {
                if (sInstance == null) {
                    sInstance = MyLinkMovementMethod()
                }
                return sInstance
            }
        private var sInstance: MyLinkMovementMethod? = null
        private val sHelper = SpanClickHelper()
    }
}