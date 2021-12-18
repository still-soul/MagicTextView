package com.test.magictextview.utils

import android.graphics.drawable.Drawable
import android.view.View

/**
 * @author xiaoman
 */
object ViewHelperUtils {
    @JvmStatic
    fun setBackgroundKeepingPadding(view: View, drawable: Drawable?) {
        val padding = intArrayOf(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
        view.background = drawable
        view.setPadding(padding[0], padding[1], padding[2], padding[3])
    }
}