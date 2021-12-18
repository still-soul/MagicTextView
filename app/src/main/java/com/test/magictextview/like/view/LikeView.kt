package com.test.magictextview.like.view

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.ContentFrameLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.test.magictextview.like.interfaces.OnFingerDowningListener
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.test.magictextview.R
import java.lang.reflect.Field

/**
 * 点赞view
 * @author xiaoman
 */
 class LikeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(
    context, attrs, defStyleAttr
) {

    private lateinit var tvLike : TextView
    private lateinit var ivLike : ImageView

    private var autoPollTask: AutoPollTask? = null
    private var onFingerDowningListener : OnFingerDowningListener? = null

    private var likeIcon = 0
    private var textColor = ContextCompat.getColor(context, R.color.black)
    private var textSize = 12f
    private var text : String? = null

    private var lastDownTime = 0L

    /**
     * 是否可以长按
     */
    private var canLongPress = true

    /**
     * 是否点击
     */
    private var isDowning = false

    /**
     * 下拉控件是否正在刷新
     */
    private var isRefreshing = false

    /**
     * 下拉控件是否可以刷新
     */
    private var enableRefresh = true

    /**
     * 第一次点击
     */
    private var firstClick = true

    init {
        autoPollTask = AutoPollTask()
        val typedArray = context.obtainStyledAttributes(
            attrs,
                R.styleable.LikeView,
            defStyleAttr,
            0
        )
        canLongPress = typedArray.getBoolean(R.styleable.LikeView_long_press, true)
        likeIcon = typedArray.getResourceId(R.styleable.LikeView_like_icon_id, 0)
        textColor = typedArray.getColor(
                R.styleable.LikeView_text_color, ContextCompat.getColor(
                context,
                R.color.black
            )
        )
        textSize = typedArray.getDimension(R.styleable.LikeView_text_size, 12f)
        text =  typedArray.getString(R.styleable.LikeView_text_string)
        typedArray.recycle()
        initLayout()
    }

    private fun initLayout() {
        View.inflate(context, R.layout.layout_like_view, this)
        tvLike = findViewById(R.id.tv_like)
        ivLike = findViewById(R.id.iv_like)
        tvLike.setTextColor(textColor)
        tvLike.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        if (likeIcon != 0){
            ivLike.setImageResource(likeIcon)
        }
        if (!text.isNullOrEmpty()){
            tvLike.text = text
        }
    }


    /**
     * 设置点赞状态
     */
    fun setLikeStatus(isLike : Boolean){
        ivLike.setImageResource( if (isLike) R.mipmap.icon_liked else R.mipmap.icon_like)
        if (isLike){
            tvLike.setTextColor(Color.parseColor("#FE89D3"))
        }else{
            tvLike.setTextColor(
                    ContextCompat.getColor(
                            context,
                            R.color.black
                    )
            )
        }
    }

    fun getTvLike() : TextView{
        return tvLike
    }


    fun setOnFingerDowningListener(onFingerDowningListener: OnFingerDowningListener){
        this.onFingerDowningListener = onFingerDowningListener
    }


    private inner class AutoPollTask : Runnable {
        override fun run() {
            onFingerDowningListener?.onLongPress(this@LikeView)
            if(!canLongPress){
                removeCallbacks(autoPollTask)
            }else{
                postDelayed(autoPollTask, CLICK_INTERVAL_TIME)
            }
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        parent?.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var onTouch = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isRefreshing = false
                isDowning = true
                //点击
                lastDownTime = System.currentTimeMillis()
//                findSmartRefreshLayout(false)
                if (isRefreshing) {
                    //如果有下拉控件并且正在刷新直接不响应
                    return false
                }
                postDelayed(autoPollTask, CLICK_INTERVAL_TIME)
                onTouch = true
            }
            MotionEvent.ACTION_UP -> {
                isDowning = false
                //抬起
                if (System.currentTimeMillis() - lastDownTime < CLICK_INTERVAL_TIME) {
                    //小于间隔时间按照单击处理
                    onFingerDowningListener?.onDown(this)
                } else {
                    //大于等于间隔时间按照长按抬起手指处理
                    onFingerDowningListener?.onUp()
                }
                findSmartRefreshLayout(true)
                removeCallbacks(autoPollTask)
                onTouch = true
            }
            MotionEvent.ACTION_CANCEL ->{
                isDowning = false
                findSmartRefreshLayout(true)
                removeCallbacks(autoPollTask)
                onTouch = false
            }
            else -> onTouch = false
        }
        return onTouch
    }

    /**
     * 如果父布局有SmartRefreshLayout 控件，设置控件是否可用
     */
    private fun findSmartRefreshLayout(enable: Boolean) {
        var parent = parent
        while (parent != null && parent !is ContentFrameLayout) {
            if (parent is SmartRefreshLayout) {
                isRefreshing = parent.state == RefreshState.Refreshing
                if (isRefreshing){
                    //如果有下拉控件并且正在刷新直接结束
                    break
                }
                if (!enable && firstClick){
                    try {
                        firstClick = false
                        val field: Field = parent.javaClass.getDeclaredField("mEnableRefresh")
                        field.isAccessible = true
                        //通过反射获取是否可以先下拉刷新的初始值
                        enableRefresh = field.getBoolean(parent)
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (enableRefresh){
                    //如果初始值不可以下拉刷新不要设置下拉刷新状态
                    parent.setEnableRefresh(enable)
                }
                parent.setEnableLoadMore(enable)
                break
            } else {
                parent = parent.parent
            }
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility != VISIBLE && isDowning){
            isDowning = false
            //抬起
//            findSmartRefreshLayout(true)
            if (System.currentTimeMillis() - lastDownTime < CLICK_INTERVAL_TIME) {
                //小于间隔时间按照单击处理
                onFingerDowningListener?.onDown(this)
            } else {
                //大于等于间隔时间按照长按抬起手指处理
                onFingerDowningListener?.onUp()
            }
            autoPollTask?.let {
                removeCallbacks(it)
            }
        }
    }


    companion object{
        /**
         * 点击间隔时间，用来判断是否是长按
         */
        const val CLICK_INTERVAL_TIME : Long = 250
    }

}