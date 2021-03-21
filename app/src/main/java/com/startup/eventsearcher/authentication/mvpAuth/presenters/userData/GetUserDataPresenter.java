package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.util.Log;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.ICurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISetUserDataView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
        Log.d(TAG, "onGetData: Чтение данных из SharedPreference");
        Disposable disposable = Observable.just(iCurrentUser.getCurrentUserFromJSON())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    Log.d(TAG, "onGetData: Данные получены: " + user.toString());
                    iSetUserDataView.onSetEmail(user.getConfidentialUserData().getEmail());
                    iSetUserDataView.onSetPassword(user.getConfidentialUserData().getPassword());
                    iSetUserDataView.onGetUserDataFromSharedPreferenceSuccess();
                });
    }
}
