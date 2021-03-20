package com.startup.eventsearcher.authentication.mvpAuth.utils.user;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;

public interface ICurrentUser {
    User getCurrentUserFromJSON();
    void saveCurrentUserToJSON(User user);
}
