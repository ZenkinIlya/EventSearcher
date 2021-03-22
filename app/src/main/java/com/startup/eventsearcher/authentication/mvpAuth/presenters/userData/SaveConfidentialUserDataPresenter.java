package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.util.Log;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.ConfidentialUserData;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.IProviderConfidentialUserData;

public class SaveConfidentialUserDataPresenter implements ISaveConfidentialUserDataPresenter {

    private static final String TAG = "tgSaveConfUserDataPres";
    private final IProviderConfidentialUserData iProviderConfidentialUserData;

    public SaveConfidentialUserDataPresenter(IProviderConfidentialUserData iProviderConfidentialUserData) {
        this.iProviderConfidentialUserData = iProviderConfidentialUserData;
    }

    @Override
    public void onSetData(boolean saveData, String email, String password) {
        Log.d(TAG, "onSetData: email = " + email + ", password = " + password);
        ConfidentialUserData confidentialUserData = new ConfidentialUserData(email, password);
        if (saveData){
            Log.d(TAG, "onSetData: Сохранение конфиденциальных данных пользователя в SharedPreference");
            iProviderConfidentialUserData.saveConfidentialUserDataToJSON(confidentialUserData);
        }
    }
}
