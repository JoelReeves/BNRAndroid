package com.bromancelabs.photogallery.services

import com.bromancelabs.photogallery.models.PhotosObjectKt
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrServiceKt {

    companion object {
         private val URL = "https://api.flickr.com/services/rest/"
         val FLICKR_API_KEY = "b71c3d2d57d035bf593c78dcb4b659d1"
         val FLICKR_API_GET_RECENT_PHOTOS = "flickr.photos.getRecent"
         val FLICKR_API_SEARCH_PHOTOS = "flickr.photos.search"
         val FLICKR_API_FORMAT = "json"
         val FLICKR_API_JSON_CALLBACK = "1"
         val FLICKR_API_EXTRAS = "url_s"

        fun getInstance(): FlickrServiceKt = RetrofitSingleton.getInstance(URL).create(FlickrServiceKt::class.java)
    }

    @GET("/services/rest")
    fun getRecentPhotos(
            @Query("method") method: String,
            @Query("api_key") apiKey: String,
            @Query("format") format: String,
            @Query("nojsoncallback") nojson: String,
            @Query("extras") extras: String): Call<PhotosObjectKt>

    @GET("/services/rest/")
    fun searchPhotos(
            @Query("method") method: String,
            @Query("api_key") apiKey: String,
            @Query("format") format: String,
            @Query("nojsoncallback") nojson: String,
            @Query("text") text: String,
            @Query("extras") extras: String): Call<PhotosObjectKt>
}

fun FlickrServiceKt.getRecentPhotos() : Call<PhotosObjectKt> = getRecentPhotos(
        method = FlickrServiceKt.FLICKR_API_GET_RECENT_PHOTOS,
        apiKey = FlickrServiceKt.FLICKR_API_KEY,
        format = FlickrServiceKt.FLICKR_API_FORMAT,
        nojson = FlickrServiceKt.FLICKR_API_JSON_CALLBACK,
        extras = FlickrServiceKt.FLICKR_API_EXTRAS)

fun FlickrServiceKt.searchPhotos(searchString: String) : Call<PhotosObjectKt> = searchPhotos(
        method = FlickrServiceKt.FLICKR_API_SEARCH_PHOTOS,
        apiKey = FlickrServiceKt.FLICKR_API_KEY,
        format = FlickrServiceKt.FLICKR_API_FORMAT,
        nojson = FlickrServiceKt.FLICKR_API_JSON_CALLBACK,
        text = searchString,
        extras = FlickrServiceKt.FLICKR_API_EXTRAS)