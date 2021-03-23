package com.startup.eventsearcher.authentication.utils.user;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.startup.eventsearcher.authentication.models.user.ConfidentialUserData;
import com.startup.eventsearcher.utils.JsonHandler;

import java.lang.reflect.Type;
import java.util.Objects;

public class ProviderConfidentialUserData implements IProviderConfidentialUserData {

    private static final String TAG = "tgProviderConfUserData";

    private final Context context;

    public ProviderConfidentialUserData(Context context) {
        this.context = context;
    }

    @Override
    public ConfidentialUserData getConfidentialUserDataFromJSON() {
        Type type = new TypeToken<ConfidentialUserData>(){}.getType();
        ConfidentialUserData confidentialUserData = JsonHandler.getSavedObjectFromPreference(
                context,
                "ConfidentialUserData",
                "confidentialUserDataKey",
                type);
        if (confidentialUserData == null){
            Log.w(TAG, "getConfidentialUserDataFromJSON: Не удалось получить конфиденциальные данные пользователя из SharedPreference");
            confidentialUserData = new ConfidentialUserData(null, null);
        }
        Log.d(TAG, "getConfidentialUserDataFromJSON: confidentialUserData = " + Objects.requireNonNull(confidentialUserData).toString());
        return confidentialUserData;
    }

    @Override
    public void saveConfidentialUserDataToJSON(ConfidentialUserData confidentialUserData) {
        JsonHandler.saveObjectToSharedPreference(
                context,
                "ConfidentialUserData",
                "confidentialUserDataKey",
                confidentialUserData);
        Log.d(TAG, "saveConfidentialUserDataToJSON: confidentialUserData = " + confidentialUserData.toString());
    }
}
