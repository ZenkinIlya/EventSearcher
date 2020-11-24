package com.startup.eventsearcher.authentication.connectionToServer.test;

import android.util.Log;

import com.startup.eventsearcher.BuildConfig;
import com.startup.eventsearcher.utils.ErrorServerHandler;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestRequester {

    private static final String TAG = "TagTestRequester";
    private static ErrorServerHandler errorServerHandler;

    public static ErrorServerHandler getErrorServerHandler() {
        return errorServerHandler;
    }

    public static void testRequest() {
        try {
            //Прослушка по тегу OkHttp
            //https://startandroid.ru/ru/blog/507-retrofit-query-path-rxjava.html
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://rawgit.com/startandroid/data/master/messages/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            TestMessageApi testMessageApi = retrofit.create(TestMessageApi.class);
            Call<List<Message>> messages = testMessageApi.messages();

            Response<List<Message>> execute = messages.execute();
            if (execute.isSuccessful()) {
                if (execute.body() != null) {
                    for (int i = 0; i < execute.body().size(); i++) {
                        Log.d(TAG, "response " + execute.body().get(i).toString());
                    }
                }else{
                    Log.d(TAG, "execute body = null");
                }
            }else {
                Log.d(TAG, "execute not successful");
            }
            errorServerHandler = new ErrorServerHandler(TAG, execute.code(), execute.message());

        } catch (Exception e) {
            e.printStackTrace();
            errorServerHandler = new ErrorServerHandler(TAG, 1000, e.getMessage());
        }
    }
}
