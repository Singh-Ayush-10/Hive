package com.mastercodint.authentaction.models

import android.widget.ImageView

data class Posts(
    var userName:String="",
    var uId:String="",
    val text:String="",
    val image:String="",
    var userImageView:String="",
    var arrayList: ArrayList<String> = ArrayList()
)
