package com.example.ethan.myproject.network;

import com.example.ethan.myproject.model.People;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("5b44aab62f000049004209dc")
    Call<List<People>> getAllPeople();
}