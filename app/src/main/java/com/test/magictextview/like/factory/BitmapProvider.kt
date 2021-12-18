package com.test.magictextview.like.factory

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextPaint
import android.util.LruCache
import androidx.annotation.DrawableRes
import com.test.magictextview.R

/**
 * 点赞动画图标管理
 * @author xiaoman
 */
object BitmapProvider {
    class Default(
        private val context: Context,
        cacheSize: Int,
        @DrawableRes private val drawableArray: IntArray,
        @DrawableRes private val numberDrawableArray: IntArray?,
        @DrawableRes private val levelDrawableArray: IntArray?,
        private val levelStringArray: Array<String>?,
        private val textSize: Float
    ) : Provider {
        private val bitmapLruCache: LruCache<Int, Bitmap> = LruCache(cacheSize)
        private val NUMBER_PREFIX = 0x70000000
        private val LEVEL_PREFIX = -0x80000000

        /**
         * 获取数字图片
         * @param number
         * @return
         */
        override fun getNumberBitmap(number: Int): Bitmap? {
            var bitmap: Bitmap?
            if (numberDrawableArray != null && numberDrawableArray.isNotEmpty()) {
                val index = number % numberDrawableArray.size
                bitmap = bitmapLruCache[NUMBER_PREFIX or numberDrawableArray[index]]
                if (bitmap == null) {
                    bitmap =
                        BitmapFactory.decodeResource(context.resources, numberDrawableArray[index])
                    bitmapLruCache.put(NUMBER_PREFIX or numberDrawableArray[index], bitmap)
                }
            } else {
                bitmap = bitmapLruCache[NUMBER_PREFIX or number]
                if (bitmap == null) {
                    bitmap = createBitmapByText(textSize, number.toString())
                    bitmapLruCache.put(NUMBER_PREFIX or number, bitmap)
                }
            }
            return bitmap
        }

        /**
         * 获取等级文案图片
         * @param level
         * @return
         */
        override fun getLevelBitmap(level: Int): Bitmap? {
            var bitmap: Bitmap?
            if (levelDrawableArray != null && levelDrawableArray.isNotEmpty()) {
                val index = level.coerceAtMost(levelDrawableArray.size)
                bitmap = bitmapLruCache[LEVEL_PREFIX or levelDrawableArray[index]]
                if (bitmap == null) {
                    bitmap =
                        BitmapFactory.decodeResource(context.resources, levelDrawableArray[index])
                    bitmapLruCache.put(LEVEL_PREFIX or levelDrawableArray[index], bitmap)
                }
            } else {
                bitmap = bitmapLruCache[LEVEL_PREFIX or level]
                if (bitmap == null && !levelStringArray.isNullOrEmpty()) {
                    val index = level.coerceAtMost(levelStringArray.size)
                    bitmap = createBitmapByText(textSize, levelStringArray[index])
                    bitmapLruCache.put(LEVEL_PREFIX or level, bitmap)
                }
            }
            return bitmap
        }

        /**
         * 获取随机表情图片
         * @return
         */
        override val randomBitmap: Bitmap
            get() {
                val index = (Math.random() * drawableArray.size).toInt()
                var bitmap = bitmapLruCache[drawableArray[index]]
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(context.resources, drawableArray[index])
                    bitmapLruCache.put(drawableArray[index], bitmap)
                }
                return bitmap
            }

        private fun createBitmapByText(textSize: Float, text: String): Bitmap {
            val textPaint = TextPaint()
            textPaint.color = Color.BLACK
            textPaint.textSize = textSize
            val bitmap = Bitmap.createBitmap(
                textPaint.measureText(text).toInt(),
                textSize.toInt(), Bitmap.Config.ARGB_4444
            )
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.TRANSPARENT)
            canvas.drawText(text, 0f, textSize, textPaint)
            return bitmap
        }

    }

    class Builder(var context: Context) {
        private var cacheSize = 0

        @DrawableRes
        private var drawableArray: IntArray? = null

        @DrawableRes
        private var numberDrawableArray: IntArray? = null

        @DrawableRes
        private var levelDrawableArray: IntArray? = null
        private var levelStringArray: Array<String>? = null
        private var textSize = 0f

        fun setCacheSize(cacheSize: Int): Builder {
            this.cacheSize = cacheSize
            return this
        }

        /**
         * 设置表情图片
         * @param drawableArray
         * @return
         */
        fun setDrawableArray(@DrawableRes drawableArray: IntArray?): Builder {
            this.drawableArray = drawableArray
            return this
        }

        /**
         * 设置数字图片
         * @param numberDrawableArray
         * @return
         */
        fun setNumberDrawableArray(@DrawableRes numberDrawableArray: IntArray): Builder {
            this.numberDrawableArray = numberDrawableArray
            return this
        }

        /**
         * 设置等级文案图片
         * @param levelDrawableArray
         * @return
         */
        fun setLevelDrawableArray(@DrawableRes levelDrawableArray: IntArray?): Builder {
            this.levelDrawableArray = levelDrawableArray
            return this
        }

        fun setLevelStringArray(levelStringArray: Array<String>?): Builder {
            this.levelStringArray = levelStringArray
            return this
        }

        fun setTextSize(textSize: Float): Builder {
            this.textSize = textSize
            return this
        }

        fun build(): Provider {
            if (cacheSize == 0) {
                cacheSize = 32
            }
            if (drawableArray == null || drawableArray?.isEmpty() == true) {
                drawableArray = intArrayOf(R.mipmap.emoji_1)
            }
            if (levelDrawableArray == null && levelStringArray.isNullOrEmpty()) {
                levelStringArray = arrayOf("次赞!", "太棒了!!", "超赞同!!!")
            }
            return Default(
                context, cacheSize, drawableArray!!, numberDrawableArray,
                levelDrawableArray, levelStringArray, textSize
            )
        }
    }

    interface Provider {

        /**
         * 获取随机表情图片
         */
        val randomBitmap: Bitmap

        /**
         * 获取数字图片
         * [number] 点击次数
         */
        fun getNumberBitmap(number: Int): Bitmap?

        /**
         * 获取等级文案图片
         * [level] 等级
         */
        fun getLevelBitmap(level: Int): Bitmap?
    }
}