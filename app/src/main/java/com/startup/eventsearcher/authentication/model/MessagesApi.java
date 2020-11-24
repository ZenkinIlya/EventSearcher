package com.startup.eventsearcher.authentication.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//https://habr.com/ru/post/429058/

public interface MessagesApi {

    @POST("login")
    Call<Integer> postSignIn(@Body SignInMessage signInMessage);
}
