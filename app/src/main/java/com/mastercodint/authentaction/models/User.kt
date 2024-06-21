package com.mastercodint.authentaction.models

public data class User(
    val uid:String="",
    val displayName: String?="",
    var imageUrl:String="",
    var arrayList: ArrayList<String> = ArrayList()
)
