package com.test.magictextview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.test.magictextview.R
import com.test.magictextview.bean.ListBean
import com.test.magictextview.like.interfaces.OnFingerDowningListener
import com.test.magictextview.like.interfaces.OnLikeAnimationListener
import com.test.magictextview.like.view.LikeView
import com.test.magictextview.link.helper.LinkCheckHelper
import com.test.magictextview.moretext.ListMoreTextView
import com.test.magictextview.span.MyClickSpan
import kotlinx.coroutines.launch

class ListAdapter(private val mContext: Context, private val lifecycleScope: LifecycleCoroutineScope,
                  val onLikeAnimationListener: OnLikeAnimationListener?) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    private val listBeans: MutableList<ListBean> = mutableListOf()
    /**
     * 是否第一次长按
     */
    private var fistLongPress = false
    private var throttleTime = 1000//点赞防快速点击
    private var lastClickTime = 0L

    fun setRecyclerList(recyclerList: List<ListBean>) {
        listBeans.clear()
        listBeans.addAll(recyclerList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.list_item_layout, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listBeans.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: ListMoreTextView = itemView.findViewById(R.id.tv)
        val likeView : LikeView = itemView.findViewById(R.id.likeView)

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val bean = listBeans[position]
        val content = bean.desc
        lifecycleScope.launch {
            val contentString = LinkCheckHelper.computeLenFilterLink(content,mContext)
            holder.tv.setMovementMethodDefault()
            holder.tv.text = contentString
            holder.tv.setOnAllSpanClickListener(object : MyClickSpan.OnAllSpanClickListener{
                override fun onClick(widget: View?) {
                    Toast.makeText(mContext,"展开全文", Toast.LENGTH_SHORT).show()
                }

            })
            holder.tv.setOnClickListener {
                Toast.makeText(mContext,"点击文本", Toast.LENGTH_SHORT).show()
            }
        }
        setLikeStatus(holder, bean)

        holder.likeView.setOnFingerDowningListener(object : OnFingerDowningListener {
            /**
             * 长按回调
             */
            override fun onLongPress(v: View) {
                if (!bean.hasLike) {
                    //未点赞
                    if (!fistLongPress) {
                        //这里同步点赞接口等数据交互
                        bean.likeNumber++
                        bean.hasLike = true
                        setLikeStatus(holder, bean)
                    }

                    //显示动画
                    onLikeAnimationListener?.doLikeAnimation(v)
                } else {
                    if (System.currentTimeMillis() - lastClickTime <= throttleTime && lastClickTime != 0L) {
                        //处理点击过后为点赞状态的情况
                        onLikeAnimationListener?.doLikeAnimation(v)
                        lastClickTime = System.currentTimeMillis()
                    } else {
                        //处理长按为点赞状态后的情况
                        onLikeAnimationListener?.doLikeAnimation(v)
                    }
                }

                fistLongPress = true

            }

            /**
             * 长按抬起手指回调处理
             */
            override fun onUp() {
                fistLongPress = false
            }

            /**
             * 单击事件回调
             */
            override fun onDown(v: View) {
                if (System.currentTimeMillis() - lastClickTime > throttleTime || lastClickTime == 0L) {
                    if (!bean.hasLike) {
                        //未点赞情况下，点赞接口和数据交互处理
                        bean.hasLike = true
                        bean.likeNumber++
                        setLikeStatus(holder, bean)
                        throttleTime = 1000
                        onLikeAnimationListener?.doLikeAnimation(v)
                    } else {
                        //点赞状态下，取消点赞接口和数据交互处理
                        bean.hasLike = false
                        bean.likeNumber--
                        setLikeStatus(holder, bean)
                        throttleTime = 30
                    }
                } else if (lastClickTime != 0L && bean.hasLike) {
                    //在时间范围内，连续点击点赞，显示动画
                    onLikeAnimationListener?.doLikeAnimation(v)
                }
                lastClickTime = System.currentTimeMillis()

            }


        })

    }

    private fun setLikeStatus(
        holder: ListViewHolder,
        bean: ListBean
    ) {
        holder.likeView.setLikeStatus(bean.hasLike)
        holder.likeView.getTvLike().text = "${bean.likeNumber}"
    }
}