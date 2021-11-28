package com.test.magictextview

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.test.magictextview.bean.LinkBean
import com.test.magictextview.link.helper.LinkCheckHelper
import com.test.magictextview.span.MyClickSpan
import com.test.magictextview.span.MyImageSpan
import com.test.magictextview.tag.TagSpanText
import com.test.magictextview.utils.PublicMethod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ssbContent = SpannableStringBuilder()
        ssbContent.append("   ")
        val digestDrawable =
            ContextCompat.getDrawable(this, R.mipmap.ic_digest_topic)
        digestDrawable?.setBounds(
            0, 0, digestDrawable.intrinsicWidth,
            digestDrawable.intrinsicHeight
        )
        ssbContent.setSpan(
            MyImageSpan(digestDrawable), 0,ssbContent.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ssbContent.append(resources.getString(R.string.more_string))

        tvMore.text = ssbContent
        tvMore.setOnAllSpanClickListener(object : MyClickSpan.OnAllSpanClickListener{
            override fun onClick(widget: View?) {
              Toast.makeText(this@MainActivity,"展开全文",Toast.LENGTH_SHORT).show()
            }

        })
        tvMore.setOnClickListener {
            ListActivity.launch(this)
        }

//        val linkList: MutableList<LinkBean> = mutableListOf()
//        val a = "你说的<a href=\"https://www.qq.com\">我是链接AAAA</a>11111<a href=\"https://www.baidu.com\">我也是链接</a>维生素睡啦啊十分另外玩完完完"
//        val finalContent = LinkCheckHelper.computeLenFilterLink(a,linkList)
//        var ssbContent1 = SpannableStringBuilder(finalContent)
//        if (linkList.isNotEmpty()) {
//            ssbContent1 = LinkCheckHelper.getLink(this, ssbContent1, linkList)
//        }
        val string = "你说的<a href=\"https://www.qq.com\">我是链接</a>11111<a href=\"https://www.baidu.com\">我也是链接</a>好开心啊，哈哈哈哈"
        lifecycleScope.launch {
            val contentString = LinkCheckHelper.computeLenFilterLink(string,this@MainActivity)
            tvLink.text = contentString

        }

        val strokeWidth = 1
        val spanTextSize = PublicMethod.sp2px(this, 10f)
        val spanRadius = PublicMethod.dp2px(this, 3f)
        val marginRight = PublicMethod.dp2px(this, 4f)
        val spanPaddingLeft = PublicMethod.dp2px(this, 4f)
        val tagString = "置顶"
        val textString = "多好多好的都不懂"
        val spannableString = SpannableString("$tagString$textString")
        val color =
                Color.parseColor("#FF5784")
        val span = TagSpanText(
                color,
                color,
                spanTextSize ,
                spanRadius ,
                marginRight,
                strokeWidth ,
                spanPaddingLeft,
                Paint.Style.STROKE
        )
        spannableString.setSpan(span, 0, tagString.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        findViewById<TextView>(R.id.tvTag).text = spannableString


    }
}