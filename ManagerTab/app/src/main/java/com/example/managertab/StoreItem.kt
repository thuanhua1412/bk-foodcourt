package com.example.managertab


data class StoreItem(var imageUrl : String?="",
                     var name : String?="",
                     var openTime  : Int? =0, var closeTime:Int? = 0,
                     var supportEmail: String? = ""){
}