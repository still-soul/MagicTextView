package com.test.magictextview.span

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat

class MyClickSpan(
    private val context: Context,
    private val clickListener: OnAllSpanClickListener?
) :
    ClickableSpan() {
    override fun onClick(widget: View) {
        clickListener?.onClick(widget)
    }

    interface OnAllSpanClickListener {
        /**
         * 点击回调
         */
        fun onClick(widget: View?)
    }

    override fun updateDrawState(ds: TextPaint) {

        ds.bgColor = ContextCompat.getColor(context, android.R.color.transparent)
        ds.isUnderlineText = false
    }
}