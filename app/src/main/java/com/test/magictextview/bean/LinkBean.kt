package com.test.magictextview.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 链接bean
 * @author zhaotk
 */
@Parcelize
data class LinkBean(
    var linkTitle: String? = null,
    var linkUrl: String? = null
) : Parcelable