package com.test.magictextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.test.magictextview.link.bean.LinkBean
import com.test.magictextview.link.helper.LinkCheckHelper
import com.test.magictextview.span.MyClickSpan
import com.test.magictextview.span.MyImageSpan
import kotlinx.android.synthetic.main.activity_main.*

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
            Toast.makeText(this@MainActivity,"点击内容",Toast.LENGTH_SHORT).show()
        }

        val linkList: MutableList<LinkBean> = mutableListOf()
        val a = "你说的<a href=\"https://www.qq.com\">我是链接AAAA</a>11111<a href=\"https://www.baidu.com\">我也是链接</a>维生素睡啦啊十分另外玩完完完"
        val finalContent = LinkCheckHelper.computeLenFilterLink(a,linkList)
        var ssbContent1 = SpannableStringBuilder(finalContent)
        if (linkList.isNotEmpty()) {
            ssbContent1 = LinkCheckHelper.getLink(this, ssbContent1, linkList)
        }
        tvLink.text = ssbContent1
    }
}