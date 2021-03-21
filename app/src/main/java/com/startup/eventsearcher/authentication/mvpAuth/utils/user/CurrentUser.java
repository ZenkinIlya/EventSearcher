package com.startup.eventsearcher.authentication.mvpAuth.utils.user;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.startup.eventsearcher.authentication.mvpAuth.models.user.ConfidentialUserData;
import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;
import com.startup.eventsearcher.utils.JsonHandler;

import java.lang.reflect.Type;
import java.util.Objects;

public class CurrentUser implements ICurrentUser {

    private static final String TAG = "tgCurrentUser";

    private final Context context;

    public CurrentUser(Context context) {
        this.context = context;
    }

    @Override
    public User getCurrentUserFromJSON(){
        Type type = new TypeToken<User>(){}.getType();
        User currentUserFromJson = JsonHandler.getSavedObjectFromPreference(
                context,
                "CurrentUser",
                "currentUserKey",
                type);
        if (currentUserFromJson == null){
            Log.w(TAG, "getCurrentUserFromJSON: Не удалось получить данные пользователя из SharedPreference");
            currentUserFromJson = new User(null, new ConfidentialUserData(null, null), null , null);
        }
        Log.d(TAG, "getCurrentUserFromJSON: User = " + Objects.requireNonNull(currentUserFromJson).toString());
        return currentUserFromJson;
    }

    @Override
    public void saveCurrentUserToJSON(User user){
        JsonHandler.saveObjectToSharedPreference(
                context,
                "CurrentUser",
                "currentUserKey",
                user);
        Log.d(TAG, "saveCurrentUserToJSON: User = " + user.toString());
    }
}
