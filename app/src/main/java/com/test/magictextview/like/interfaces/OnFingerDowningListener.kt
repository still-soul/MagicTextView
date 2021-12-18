package com.test.magictextview.like.interfaces

import android.view.View

/**
 * 手指按下监听回调
 * @author xiaoman
 */
interface OnFingerDowningListener {
    /**
     * 长按事件回调
     * [v] 按下的view
     */
    fun onLongPress(v : View)

    /**
     * 手指抬起
     */
    fun onUp()

    /**
     * 点击事件回调
     * [v] 按下的view
     */
    fun onDown(v : View)

}