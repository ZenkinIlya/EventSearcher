package com.startup.eventsearcher.utils.user;

import com.startup.eventsearcher.models.user.ConfidentialUserData;

public interface IProviderConfidentialUserData {
    ConfidentialUserData getConfidentialUserDataFromJSON();
    void saveConfidentialUserDataToJSON(ConfidentialUserData confidentialUserData);
}
