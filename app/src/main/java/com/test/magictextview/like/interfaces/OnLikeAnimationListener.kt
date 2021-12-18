package com.test.magictextview.like.interfaces

import android.view.View

/**
 * 点赞动画监听回调
 * @author xiaoman
 */
interface OnLikeAnimationListener {
    /**
     *  开始点赞动画
     *  [v] 按下的view
     */
    fun doLikeAnimation(v: View)

}