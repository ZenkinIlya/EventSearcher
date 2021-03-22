package com.startup.eventsearcher.authentication.mvpAuth.views.introduction;

public interface IIntroductionView {
    void onCheckLoginInFirebase(boolean loginInFirebase);
    void onCheckUserHaveLoginAndPhoto(boolean userHaveLoginAndPhoto);
}
