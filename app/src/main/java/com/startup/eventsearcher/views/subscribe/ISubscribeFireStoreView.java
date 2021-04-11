package com.startup.eventsearcher.views.subscribe;

public interface ISubscribeFireStoreView {
    void onSuccess();
    void onGetError(String message);
    void showLoading(boolean show);
}
