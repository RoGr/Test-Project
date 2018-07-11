package com.example.ethan.myproject.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static final String BASE_URL = "http://www.mocky.io/v2/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {


        if (retrofit == null) {
            OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

            retrofit = new Retrofit.Builder().
                    baseUrl(BASE_URL).
                    client(okHttpClient).
                    addConverterFactory(GsonConverterFactory.create()).
                    build();
        }

        return retrofit;
    }


}