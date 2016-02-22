package com.bromancelabs.photogallery.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhotosObjectKt constructor(
        @SerializedName("photos")
        @Expose
        var photos: PhotosKt
)