package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.ICurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISetUserDataView;

public class GetUserDataPresenter implements IGetUserDataPresenter {

    private static final String TAG = "tgUserData";
    private final ISetUserDataView iSetUserDataView;
    private final ICurrentUser iCurrentUser;

    public GetUserDataPresenter(ISetUserDataView iSetUserDataView, ICurrentUser iCurrentUser) {
        this.iSetUserDataView = iSetUserDataView;
        this.iCurrentUser = iCurrentUser;
    }

    @Override
    public void onGetData() {
        User user = iCurrentUser.getCurrentUserFromJSON();
        iSetUserDataView.onSetEmail(user.getEmail());
        iSetUserDataView.onSetPassword(user.getPassword());
    }
}
