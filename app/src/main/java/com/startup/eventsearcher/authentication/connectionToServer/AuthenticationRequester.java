package com.startup.eventsearcher.authentication.connectionToServer;

import android.util.Log;

import com.startup.eventsearcher.BuildConfig;
import com.startup.eventsearcher.utils.ErrorServerHandler;
import com.startup.eventsearcher.authentication.model.MessagesApi;
import com.startup.eventsearcher.authentication.model.SignInMessage;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationRequester {

    private static final String TAG = "TagAuthentication";
    private static ErrorServerHandler errorServerHandler;

    public static ErrorServerHandler getErrorServerHandler() {
        return errorServerHandler;
    }

    //Аутенитификация пользователя
    public static void signInRequest(SignInMessage signInMessage) {
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

            MessagesApi messagesApi = retrofit.create(MessagesApi.class);
            Call<Integer> authenticationMessage = messagesApi.postSignIn(signInMessage);

            //Отправка данных пользователя (логин, пароль)
            //В ответ получаем ошибку
            Response<Integer> execute = authenticationMessage.execute();
            if (!execute.isSuccessful()) {
                Log.d(TAG, "execute not successful");
            }
            errorServerHandler = new ErrorServerHandler(TAG, execute.code(), execute.message());

        } catch (Exception e) {
            e.printStackTrace();
            errorServerHandler = new ErrorServerHandler(TAG, 1000, e.getMessage());
        }
    }
}
