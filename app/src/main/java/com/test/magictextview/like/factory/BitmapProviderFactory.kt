package com.test.magictextview.like.factory

import android.content.Context
import com.test.magictextview.R

/**
 * 用于点赞
 * @author xiaoman
 */
object BitmapProviderFactory {
    fun getProvider(context: Context): BitmapProvider.Provider {
        return BitmapProvider.Builder(context)
            .setDrawableArray(
                intArrayOf(
                        R.mipmap.emoji_1, R.mipmap.emoji_2, R.mipmap.emoji_3,
                        R.mipmap.emoji_4, R.mipmap.emoji_5, R.mipmap.emoji_6,
                        R.mipmap.emoji_7, R.mipmap.emoji_8, R.mipmap.emoji_9, R.mipmap.emoji_10,
                        R.mipmap.emoji_11, R.mipmap.emoji_12, R.mipmap.emoji_13,
                        R.mipmap.emoji_14
                )
            )
            .setNumberDrawableArray(
                intArrayOf(
                        R.mipmap.multi_digg_num_0, R.mipmap.multi_digg_num_1,
                        R.mipmap.multi_digg_num_2, R.mipmap.multi_digg_num_3,
                        R.mipmap.multi_digg_num_4, R.mipmap.multi_digg_num_5,
                        R.mipmap.multi_digg_num_6, R.mipmap.multi_digg_num_7,
                        R.mipmap.multi_digg_num_8, R.mipmap.multi_digg_num_9
                )
            )
            .setLevelDrawableArray(
                intArrayOf(
                        R.mipmap.multi_digg_word_level_1, R.mipmap.multi_digg_word_level_2,
                        R.mipmap.multi_digg_word_level_3
                )
            )
            .build()
    }
}