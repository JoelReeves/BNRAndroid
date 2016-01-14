package com.bromancelabs.locatr.services;

import com.bromancelabs.locatr.models.PhotosObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrService {

    @GET("/services/rest/")
    Call<PhotosObject> searchPhotos(@Query("method") String method, @Query("api_key") String apiKey, @Query("format") String format, @Query("nojsoncallback") String nojson, @Query("text") String text, @Query("lat") double latitude, @Query("lon") double longitude, @Query("extras") String extras);
}
