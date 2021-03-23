package com.startup.eventsearcher.authentication.views.introduction;

public interface IIntroductionView {
    void onCheckLoginInFirebase(boolean loginInFirebase);
    void onCheckUserHaveLoginAndPhoto(boolean userHaveLoginAndPhoto);
}
