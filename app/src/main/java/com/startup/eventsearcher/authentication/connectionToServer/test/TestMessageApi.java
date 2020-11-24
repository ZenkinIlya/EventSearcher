package com.startup.eventsearcher.authentication.connectionToServer.test;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TestMessageApi {

    @GET("messages1.json")
    Call<List<Message>> messages();
}
