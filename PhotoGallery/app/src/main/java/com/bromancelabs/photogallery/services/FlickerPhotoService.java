package com.bromancelabs.photogallery.services;

import com.bromancelabs.photogallery.models.PhotosObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickerPhotoService {

    @GET("/services/rest/")
    Call<PhotosObject> getRecentPhotos(@Query("method") String method, @Query("api_key") String apiKey, @Query("format") String format, @Query("nojsoncallback") String nojson, @Query("extras") String extras);
}
