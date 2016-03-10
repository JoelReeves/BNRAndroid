package com.bromancelabs.photogallery.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotoKt constructor(
        @SerializedName("id") @Expose
        var id: String,
        @SerializedName("owner") @Expose
        var owner: String,
        @SerializedName("secret") @Expose
        var secret: String,
        @SerializedName("server") @Expose
        var server: String,
        @SerializedName("farm") @Expose
        var farm: Int,
        @SerializedName("title") @Expose
        var title: String,
        @SerializedName("ispublic") @Expose
        var ispublic: Int,
        @SerializedName("isfriend") @Expose
        var isfriend: Int,
        @SerializedName("isfamily") @Expose
        var isfamily: Int,
        @SerializedName("url_s") @Expose
        var url: String?
) {

    override fun toString() = "{Title: $title}"
}