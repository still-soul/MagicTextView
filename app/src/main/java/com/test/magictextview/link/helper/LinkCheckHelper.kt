package com.test.magictextview.link.helper

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.test.magictextview.R
import com.test.magictextview.bean.LinkBean
import com.test.magictextview.link.span.MyLinkClickSpan
import com.test.magictextview.span.MyImageSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

/**
 * 链接工具类
 * @author zhaotk
 */
object LinkCheckHelper {
    /**
     * 匹配a标签直接插入链接
     */
    suspend fun computeLenFilterLink(text: String, mContext: Context): SpannableStringBuilder =
        withContext(Dispatchers.Default) {

            var strings = SpannableStringBuilder(text)
            val pattern = "<a \\s*href\\s*=\\s*(?:.*?)>(.*?)</a\\s*>"
            val p = Pattern.compile(pattern)
            val matcher = p.matcher(strings)
            while (matcher.find()) {
                val str = matcher.group()
                val linkTitle = matcher.group(1) ?: ""

                //a标签链接正则匹配
                val patternUrlString =
                    "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))"
                val patternUrl = Pattern.compile(
                    patternUrlString,
                    Pattern.CASE_INSENSITIVE
                )
                //链接url
                val matcherUrL = patternUrl.matcher(strings)
                var linkUrl = ""
                while (matcherUrL.find()) {
                    linkUrl = matcherUrL.group()
                    linkUrl = linkUrl.replace("href\\s*=\\s*(['|\"]*)".toRegex(), "")
                    linkUrl = linkUrl.replace("['|\"]".toRegex(), "")
                    linkUrl = linkUrl.trim { it <= ' ' }



                    break
                }
                //设置颜色
                val sb = SpannableString("#$linkTitle")
                sb.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(mContext, R.color.link_color)
                    ), 0,
                    sb.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )

                sb.setSpan(
                    object : MyLinkClickSpan(mContext) {
                        override fun onSpanClick(widget: View?) {
                            //链接跳转
                            Toast.makeText(mContext, "点击链=$linkUrl", Toast.LENGTH_SHORT).show()

                        }
                    },
                    0, sb.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                val start = strings.indexOf(str)
                strings.delete(start, start + str.length)
                //插入链接
                strings.insert(start, sb)

            }

            return@withContext strings
        }


    const val REFC = "refc"

    /**
     * 获取链接标题和链接URL，占位符占位
     */
    fun computeLenFilterLink(text: String, linkList: MutableList<LinkBean>): String {
        linkList.clear()
        var strings = text
        val pattern = "<a \\s*href\\s*=\\s*(?:.*?)>(.*?)</a\\s*>"
        val p = Pattern.compile(pattern)
        val matcher = p.matcher(strings)
        var position = 0
        while (matcher.find()) {
            position += 1
            val str = matcher.group()
            val linkTitle = matcher.group(1) ?: ""

            //a标签链接正则匹配
            val patternUrlString =
                "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))"
            val patternUrl = Pattern.compile(
                patternUrlString,
                Pattern.CASE_INSENSITIVE
            )
            //链接url
            val matcherUrL = patternUrl.matcher(strings)
            while (matcherUrL.find()) {
                var linkUrl = matcherUrL.group()
                linkUrl = linkUrl.replace("href\\s*=\\s*(['|\"]*)".toRegex(), "")
                linkUrl = linkUrl.replace("['|\"]".toRegex(), "")
                linkUrl = linkUrl.trim { it <= ' ' }

                val linkBean = LinkBean()
                linkBean.linkTitle = linkTitle
                linkBean.linkUrl = linkUrl
                linkList.add(linkBean)
                break
            }
            strings = strings.replaceFirst(str, "$REFC$position$linkTitle")

        }

        return strings
    }


    /**
     * 链接拼接
     */
    fun getLink(
        mContext: Context,
        ssbShrink: SpannableStringBuilder,
        linkList: MutableList<LinkBean>
    ): SpannableStringBuilder {

        for ((index, linkBean) in linkList.withIndex()) {
            val placeholder = REFC + (index + 1) + linkBean.linkTitle
            val position = ssbShrink.indexOf(placeholder)
            if (position == -1) {
                Log.e("ssbShrink", ssbShrink.toString())
                continue
            }
            val sb = SpannableString("    " + linkBean.linkTitle + " ")

            val drawable =
                ContextCompat.getDrawable(mContext, R.mipmap.icon_link)
            drawable?.setBounds(
                0, 0, drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )
            sb.setSpan(
                MyImageSpan(drawable), 0, 3,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            //设置颜色
            sb.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(mContext, R.color.link_color)
                ), 4,
                sb.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )

            sb.setSpan(
                object : MyLinkClickSpan(mContext) {
                    override fun onSpanClick(widget: View?) {
                        //链接跳转

                    }
                },
                0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )

            sb.setSpan(
                object : MyLinkClickSpan(mContext) {
                    override fun onSpanClick(widget: View?) {
                        //链接跳转

                    }
                },
                4, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            //删除已使用的占位符
            ssbShrink.delete(position, position + placeholder.length)
            //插入链接
            ssbShrink.insert(position, sb)

        }

        return ssbShrink
    }


}