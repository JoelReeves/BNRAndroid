package com.bromancelabs.photogallery.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitSingleton {

    private static Retrofit mRetrofit;

    private RetrofitSingleton() {

    }

    public static Retrofit getInstance(String url) {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}
