package com.test.magictextview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.test.magictextview.R
import com.test.magictextview.moretext.ListMoreTextView
import com.test.magictextview.span.MyClickSpan

class ListAdapter(private val mContext: Context) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    private val listBeans: MutableList<String> = mutableListOf()
    fun setRecyclerList(recyclerList: List<String>) {
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

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val content = listBeans[position]
        holder.tv.text = content
        holder.tv.setOnAllSpanClickListener(object : MyClickSpan.OnAllSpanClickListener{
            override fun onClick(widget: View?) {
                Toast.makeText(mContext,"展开全文", Toast.LENGTH_SHORT).show()
            }

        })
        holder.tv.setOnClickListener {
            Toast.makeText(mContext,"点击文本", Toast.LENGTH_SHORT).show()
        }
    }
}