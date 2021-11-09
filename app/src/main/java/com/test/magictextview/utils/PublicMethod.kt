package com.test.magictextview.utils

object PublicMethod {

    fun isEmojiCharacter(codePoint: Char): Boolean {
        return !(codePoint.toInt() == 0x0 ||
                codePoint.toInt() == 0x9 ||
                codePoint.toInt() == 0xA ||
                codePoint.toInt() == 0xD ||
                codePoint.toInt() in 0x20..0xD7FF ||
                codePoint.toInt() in 0xE000..0xFFFD ||
                codePoint.toInt() in 0x10000..0x10FFFF)
    }
}