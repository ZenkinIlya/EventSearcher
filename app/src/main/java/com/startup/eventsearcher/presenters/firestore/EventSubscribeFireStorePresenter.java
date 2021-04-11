package com.startup.eventsearcher.presenters.firestore;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.models.event.Subscriber;
import com.startup.eventsearcher.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.views.subscribe.ISubscribeFireStoreView;

public class EventSubscribeFireStorePresenter {
    private static final String TAG = "tgEventSubscribePres";

    private final ISubscribeFireStoreView iSubscribeFireStoreView;

    public EventSubscribeFireStorePresenter(ISubscribeFireStoreView iSubscribeFireStoreView) {
        this.iSubscribeFireStoreView = iSubscribeFireStoreView;
    }

    public void subscribeToEventInFirebase(String idEvent, Subscriber subscriber) {
        Log.d(TAG, "subscribeToEventInFirebase()");

        iSubscribeFireStoreView.showLoading(true);
        App.db.collection("events")
                .document(String.valueOf(idEvent))
                .update("subscribers", FieldValue.arrayUnion(subscriber))
                .addOnSuccessListener(voidTask -> {
                    Log.i(TAG, "subscribeToEventInFirebase: пользователь подписался");
                    iSubscribeFireStoreView.onSuccess();
                })
                .addOnFailureListener(error -> {
                    String stringError = FirebaseErrorHandler.errorHandler(error);
                    Log.e(TAG, "subscribeToEventInFirebase: " + stringError);
                    iSubscribeFireStoreView.onGetError(stringError);
                })
                .addOnCompleteListener(voidTask -> {
                    iSubscribeFireStoreView.showLoading(false);
                });
    }

    public void unSubscribeFormEventInFirebase(String idEvent, Subscriber subscriber) {
        Log.d(TAG, "unSubscribeFormEventInFirebase()");

        iSubscribeFireStoreView.showLoading(true);
        App.db.collection("events")
                .document(String.valueOf(idEvent))
                .update("subscribers", FieldValue.arrayRemove(subscriber))
                .addOnSuccessListener(voidTask -> {
                    Log.i(TAG, "unSubscribeFormEventInFirebase: пользователь отписался");
                    iSubscribeFireStoreView.onSuccess();
                })
                .addOnFailureListener(error -> {
                    String stringError = FirebaseErrorHandler.errorHandler(error);
                    Log.e(TAG, "unSubscribeFormEventInFirebase: " + stringError);
                    iSubscribeFireStoreView.onGetError(stringError);
                })
                .addOnCompleteListener(voidTask -> {
                    iSubscribeFireStoreView.showLoading(false);
                });


    }
}
