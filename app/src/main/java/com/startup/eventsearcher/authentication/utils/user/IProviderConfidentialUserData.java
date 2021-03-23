package com.startup.eventsearcher.authentication.utils.user;

import com.startup.eventsearcher.authentication.models.user.ConfidentialUserData;

public interface IProviderConfidentialUserData {
    ConfidentialUserData getConfidentialUserDataFromJSON();
    void saveConfidentialUserDataToJSON(ConfidentialUserData confidentialUserData);
}
