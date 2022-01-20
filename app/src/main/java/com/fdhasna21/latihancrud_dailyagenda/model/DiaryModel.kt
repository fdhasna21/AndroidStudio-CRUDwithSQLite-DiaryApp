package com.fdhasna21.latihancrud_dailyagenda.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiaryModel(
    val id : Int? = null,
    val date : String? = null,
    val time : String? = null,
    val title : String? = null,
    val content : String? = null
) : Parcelable

