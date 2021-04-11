package com.startup.eventsearcher.presenters.userData;

import android.util.Log;

import com.startup.eventsearcher.utils.user.IProviderConfidentialUserData;
import com.startup.eventsearcher.views.login.ISetUserDataView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoadConfidentialUserDataPresenter {

    private static final String TAG = "tgLoadConfUserDataPres";

    private final ISetUserDataView iSetUserDataView;
    private final IProviderConfidentialUserData iProviderConfidentialUserData;

    public LoadConfidentialUserDataPresenter(ISetUserDataView iSetUserDataView,
                                             IProviderConfidentialUserData iProviderConfidentialUserData) {
        this.iSetUserDataView = iSetUserDataView;
        this.iProviderConfidentialUserData = iProviderConfidentialUserData;
    }

    public void getData() {
        Log.d(TAG, "getData: Чтение данных из SharedPreference");
        Disposable disposable = Observable.just(iProviderConfidentialUserData.getConfidentialUserDataFromJSON())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(confidentialUserData -> {
                    Log.d(TAG, "getData: Данные получены: " + confidentialUserData.toString());
                    iSetUserDataView.onSetEmail(confidentialUserData.getEmail());
                    iSetUserDataView.onSetPassword(confidentialUserData.getPassword());;
                });
    }
}
