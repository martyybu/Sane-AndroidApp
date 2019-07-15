package com.example.tp.sane.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //Sets up the connection to the API
    private static final String ROOT_URL = "http://192.168.0.48/Sane/public/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if(mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public API getApi() {
        return retrofit.create(API.class);
    }
}
