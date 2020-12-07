package com.test.magictextview.moretext

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.appcompat.widget.AppCompatTextView
import com.test.magictextview.R
import kotlin.math.ceil

/**
 * 限制最大行数且在最后显示...全文
 * @author zhaotk
 */
class MoreTextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.MoreTextViewStyle
) :
    AppCompatTextView(context!!, attrs, defStyleAttr) {
    /**
     * 最大行数
     */
    private val maxLine: Int

    /**
     * 尾部更多文字
     */
    private val moreText: String?

    /**
     * 尾部更多文字颜色
     */
    private val moreTextColor: Int
    private var mPaint: Paint? = null


    init {
        val array = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.MoreTextView, defStyleAttr, 0
        )
        maxLine = array.getInt(R.styleable.MoreTextView_more_action_text_maxLines, Int.MAX_VALUE)
        moreText = array.getString(R.styleable.MoreTextView_more_action_text)
        moreTextColor = array.getColor(R.styleable.MoreTextView_more_action_text_color, Color.BLACK)
        array.recycle()
        init()
    }

    private fun init() {
        mPaint = paint
        viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (TextUtils.isEmpty(text)) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    return false
                }
                val linCount = lineCount
                if (linCount > maxLine) {
                    var offset = 1
                    val layout = layout
                    val staticLayout = StaticLayout(
                        text,
                        layout.paint,
                        layout.width,
                        Layout.Alignment.ALIGN_NORMAL,
                        layout.spacingMultiplier,
                        layout.spacingAdd,
                        false
                    )
                    val indexEnd = staticLayout.getLineEnd(maxLine - 1)
                    val tempText = text.subSequence(0, indexEnd)
                    var offsetWidth =
                        layout.paint.measureText(tempText[indexEnd - 1].toString()).toInt()
                    val moreWidth =
                        ceil(layout.paint.measureText(moreText).toDouble()).toInt()
                    while (offsetWidth <= moreWidth) {
                        offset++
                        offsetWidth += layout.paint.measureText(tempText[indexEnd - offset].toString())
                            .toInt()
                    }
                    val ssbShrink = text.subSequence(0, indexEnd - offset)
                    val stringBuilder = SpannableStringBuilder(ssbShrink)
                    val sb = SpannableString(moreText)
                    sb.setSpan(
                        ForegroundColorSpan(moreTextColor), 3, sb.length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    stringBuilder.append(sb)
                    text = stringBuilder
                }
                viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })
    }
}