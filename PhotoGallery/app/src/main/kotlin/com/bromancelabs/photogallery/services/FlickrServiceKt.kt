package com.bromancelabs.photogallery.services

import com.bromancelabs.photogallery.models.PhotosObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrServiceKt {

    @GET("/services/rest")
    fun getRecentPhotos(@Query("method") method: String, @Query("api_key") apiKey: String, @Query("format") format: String, @Query("nojsoncallback") nojson: String, @Query("extras") extras: String): Call<PhotosObject>

    @GET("/services/rest/")
    fun searchPhotos(@Query("method") method: String, @Query("api_key") apiKey: String, @Query("format") format: String, @Query("nojsoncallback") nojson: String, @Query("text") text: String, @Query("extras") extras: String): Call<PhotosObject>
}