package com.startup.eventsearcher.authentication.utils.user;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startup.eventsearcher.authentication.models.user.User;

public class FirebaseAuthUserGetter {

    public static User getUserFromFirebaseAuth(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String photoUrl;
        Uri url = currentUser.getPhotoUrl();
        if (currentUser.getPhotoUrl() == null){
            photoUrl = "";
        }else {
            photoUrl = url.toString();
        }
        return new User(currentUser.getUid(), currentUser.getDisplayName(), photoUrl);
    }

    public static String getUserEmailFromFirebaseAuth(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getEmail();
    }
}
