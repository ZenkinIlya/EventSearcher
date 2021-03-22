package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.util.Log;

import com.startup.eventsearcher.authentication.mvpAuth.utils.user.IProviderConfidentialUserData;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISetUserDataView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoadConfidentialUserDataPresenter implements ILoadConfidentialUserDataPresenter {

    private static final String TAG = "tgLoadConfUserDataPres";

    private final ISetUserDataView iSetUserDataView;
    private final IProviderConfidentialUserData iProviderConfidentialUserData;

    public LoadConfidentialUserDataPresenter(ISetUserDataView iSetUserDataView,
                                             IProviderConfidentialUserData iProviderConfidentialUserData) {
        this.iSetUserDataView = iSetUserDataView;
        this.iProviderConfidentialUserData = iProviderConfidentialUserData;
    }

    @Override
    public void onGetData() {
        Log.d(TAG, "onGetData: Чтение данных из SharedPreference");
        Disposable disposable = Observable.just(iProviderConfidentialUserData.getConfidentialUserDataFromJSON())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(confidentialUserData -> {
                    Log.d(TAG, "onGetData: Данные получены: " + confidentialUserData.toString());
                    iSetUserDataView.onSetEmail(confidentialUserData.getEmail());
                    iSetUserDataView.onSetPassword(confidentialUserData.getPassword());;
                });
    }
}
