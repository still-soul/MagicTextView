package com.test.magictextview.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListBean(
    val desc: String,
    var hasLike: Boolean,
    var likeNumber: Int
) : Parcelable