package com.bromancelabs.photogallery.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class PhotosKt constructor(
        @SerializedName("page") @Expose
        var page: Int,
        @SerializedName("pages") @Expose
        var pages: Int,
        @SerializedName("perpage") @Expose
        var perpage: Int,
        @SerializedName("total") @Expose
        var total: String,
        @SerializedName("photo") @Expose
        var photo: List<PhotoKt> = emptyList()
)