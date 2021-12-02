package com.test.magictextview.moretext

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.test.magictextview.R
import com.test.magictextview.link.helper.MyLinkMovementMethod
import com.test.magictextview.link.span.MyLinkClickSpan
import com.test.magictextview.span.MyClickSpan
import com.test.magictextview.utils.PublicMethod
import kotlin.math.ceil

/**
 * 限制最大行数且在最后显示...全文
 * @author zhaotk
 */
class ListMoreTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.MoreTextViewStyle
) :
        AppCompatTextView(context, attrs, defStyleAttr) {

    /**
     * 最大行数
     */
    private var maxLine: Int

    private val moreTextSize: Int

    /**
     * 尾部更多文字
     */
    private val moreText: String?

    /**
     * 尾部更多文字颜色
     */
    private val moreTextColor: Int

    /**
     * 是否可以点击尾部更多文字
     */
    private val moreCanClick : Boolean

    private var mPaint: Paint? = null

    /**
     * 尾部更多文字点击事件接口回调
     */
    private var onAllSpanClickListener: MyClickSpan.OnAllSpanClickListener? = null

    /**
     * 实现span的点击
     */
    private var mPressedSpan: ClickableSpan? = null
    private var result = false


    init {
        val array = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.MoreTextView, defStyleAttr, 0
        )
        maxLine = array.getInt(R.styleable.MoreTextView_more_action_text_maxLines, Int.MAX_VALUE)
        moreText = array.getString(R.styleable.MoreTextView_more_action_text)
        moreTextSize = array.getInteger(R.styleable.MoreTextView_more_action_text_size, 13)
        moreTextColor = array.getColor(R.styleable.MoreTextView_more_action_text_color, Color.BLACK)
        moreCanClick = array.getBoolean(R.styleable.MoreTextView_more_can_click,false)
        array.recycle()
        init()
    }

    private fun init() {
        mPaint = paint
    }

    /**
     * 设置最大行数
     */
    fun setMaxLine (maxLine : Int){
        this.maxLine = maxLine
    }

    /**
     * 使用者主动调用
     * 如果有显示链接需求一定要调用此方法
     */
    fun setMovementMethodDefault() {
        movementMethod = MyLinkMovementMethod.instance
        highlightColor = Color.TRANSPARENT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (lineCount > maxLine) {
            //如果大于设置的最大行数
            val (layout, stringBuilder, sb) = clipContent()
            stringBuilder.append(sb)
            setMeasuredDimension(measuredWidth, getDesiredHeight(layout))
            text = stringBuilder
        }
    }

    /**
     * 裁剪内容
     */
    private fun clipContent(): Triple<Layout, SpannableStringBuilder, SpannableString> {
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
        //表情字节个数
        var countEmoji = 0
        while (indexEnd > offset && offsetWidth <= moreWidth ) {
            //当前字节是否位表情
            val isEmoji = PublicMethod.isEmojiCharacter(tempText[indexEnd - offset])
            if (isEmoji){
                countEmoji += 1
            }
            offset++
            val pair = getOffsetWidth(
                    indexEnd,
                    offset,
                    tempText,
                    countEmoji,
                    offsetWidth,
                    layout,
                    moreWidth
            )
            offset = pair.first
            offsetWidth = pair.second
        }
        val ssbShrink = tempText.subSequence(0, indexEnd - offset)
        val stringBuilder = SpannableStringBuilder(ssbShrink)
        val sb = SpannableString(moreText)
        sb.setSpan(
                ForegroundColorSpan(moreTextColor), 3, sb.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        //设置字体大小
        sb.setSpan(
                AbsoluteSizeSpan(moreTextSize, true), 3, sb.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (moreCanClick){
            //设置点击事件
            sb.setSpan(
                    MyClickSpan(context, onAllSpanClickListener), 3, sb.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        return Triple(layout, stringBuilder, sb)
    }

    private fun getOffsetWidth(
            indexEnd: Int,
            offset: Int,
            tempText: CharSequence,
            countEmoji: Int,
            offsetWidth: Int,
            layout: Layout,
            moreWidth: Int
    ): Pair<Int, Int> {
        var offset1 = offset
        var offsetWidth1 = offsetWidth
        if (indexEnd > offset1) {
            val text = tempText[indexEnd - offset1 - 1].toString().trim()
            if (text.isNotEmpty() && countEmoji % 2 == 0) {
                val charText = tempText[indexEnd - offset1]
                offsetWidth1 += layout.paint.measureText(charText.toString()).toInt()
                //一个表情两个字符，避免截取一半字符出现乱码或者显示不全...全文
                if (offsetWidth1 > moreWidth && PublicMethod.isEmojiCharacter(charText)) {
                    offset1++
                }
            }
        } else {
            val charText = tempText[indexEnd - offset1]
            offsetWidth1 += layout.paint.measureText(charText.toString()).toInt()
        }
        return Pair(offset1, offsetWidth1)
    }

    /**
     * 获取内容高度
     */
    private fun getDesiredHeight(layout: Layout?): Int {
        if (layout == null) {
            return 0
        }
        val lineTop: Int
        val lineCount = layout.lineCount
        val compoundPaddingTop = compoundPaddingTop + compoundPaddingBottom - lineSpacingExtra.toInt()
        lineTop = when {
            lineCount > maxLine -> {
                //文字行数超过最大行
                layout.getLineTop(maxLine)
            }
            else -> {
                layout.getLineTop(lineCount)
            }
        }
        return (lineTop + compoundPaddingTop).coerceAtLeast(suggestedMinimumHeight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val text = text
        val spannable = Spannable.Factory.getInstance().newSpannable(text)

        if (event.action == MotionEvent.ACTION_DOWN) {
            //手指按下
            onDown(spannable, event)
        }

        if (mPressedSpan != null && mPressedSpan is MyLinkClickSpan) {
            //如果有MyLinkClickSpan就走MyLinkMovementMethod的onTouchEvent
            return MyLinkMovementMethod.instance
                    .onTouchEvent(this, text as Spannable, event)
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            //手指移动
            val mClickSpan = getPressedSpan(this, spannable, event)
            if (mPressedSpan != null && mPressedSpan !== mClickSpan) {
                mPressedSpan = null
                Selection.removeSelection(spannable)
            }
        }
        if (event.action == MotionEvent.ACTION_UP) {
            //手指抬起
            onUp(event, spannable)
        }
        return result
    }

    /**
     * 手指按下逻辑
     */
    private fun onDown(spannable: Spannable, event: MotionEvent) {
        //按下时记下clickSpan
        mPressedSpan = getPressedSpan(this, spannable, event)
        if (mPressedSpan != null && mPressedSpan is MyClickSpan) {
            result = true
            Selection.setSelection(
                    spannable, spannable.getSpanStart(mPressedSpan),
                    spannable.getSpanEnd(mPressedSpan)
            )
        } else {
            result = if (moreCanClick){
                super.onTouchEvent(event)
            }else{
                false
            }
        }
    }

    /**
     * 手指抬起逻辑
     */
    private fun onUp(event: MotionEvent, spannable: Spannable?) {
        result = if (mPressedSpan != null && mPressedSpan is MyClickSpan) {
            (mPressedSpan as MyClickSpan).onClick(this)
            true
        } else {
            if (moreCanClick) {
                super.onTouchEvent(event)
            }
            false
        }
        mPressedSpan = null
        Selection.removeSelection(spannable)
    }

    /**
     * 设置尾部...全文点击事件
     */
    fun setOnAllSpanClickListener(
            onAllSpanClickListener: MyClickSpan.OnAllSpanClickListener
    ) {
        this.onAllSpanClickListener = onAllSpanClickListener
    }

    private fun getPressedSpan(
            textView: TextView, spannable: Spannable,
            event: MotionEvent
    ): ClickableSpan? {
        var mTouchSpan: ClickableSpan? = null

        var x = event.x.toInt()
        var y = event.y.toInt()
        x -= textView.totalPaddingLeft
        x += textView.scrollX
        y -= textView.totalPaddingTop
        y += textView.scrollY
        val layout = layout
        val line = layout.getLineForVertical(y)
        val off = layout.getOffsetForHorizontal(line, x.toFloat())

        val spans: Array<MyClickSpan> =
                spannable.getSpans(
                        off, off,
                        MyClickSpan::class.java
                )
        if (spans.isNotEmpty()) {
            mTouchSpan = spans[0]
        } else {
            val linkSpans = spannable.getSpans(off, off, MyLinkClickSpan::class.java)
            if (linkSpans != null && linkSpans.isNotEmpty()) {
                mTouchSpan = linkSpans[0]
            }
        }
        return mTouchSpan
    }
}