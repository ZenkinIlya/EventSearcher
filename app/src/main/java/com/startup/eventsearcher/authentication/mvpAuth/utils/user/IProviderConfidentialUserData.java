package com.startup.eventsearcher.authentication.mvpAuth.utils.user;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.ConfidentialUserData;

public interface IProviderConfidentialUserData {
    ConfidentialUserData getConfidentialUserDataFromJSON();
    void saveConfidentialUserDataToJSON(ConfidentialUserData confidentialUserData);
}
