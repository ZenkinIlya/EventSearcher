package com.startup.eventsearcher.utils.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startup.eventsearcher.models.user.User;

public class FirebaseAuthUserGetter {

    public static User getUserFromFirebaseAuth(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return new User(currentUser.getUid(), currentUser.getDisplayName());
    }

    public static String getUserEmailFromFirebaseAuth(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getEmail();
    }

    public static String getUserPhoneNumberFromFirebaseAuth(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getPhoneNumber();
    }
}
