package com.startup.eventsearcher.authentication.mvpAuth.models.user;

import android.net.Uri;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String uid;
    private ConfidentialUserData confidentialUserData;
    private String login;
    private Uri uriPhoto;

    public User(){};

    public User(String uid, ConfidentialUserData confidentialUserData, String login, Uri uriPhoto) {
        this.uid = uid;
        this.confidentialUserData = confidentialUserData;
        this.login = login;
        this.uriPhoto = uriPhoto;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ConfidentialUserData getConfidentialUserData() {
        return confidentialUserData;
    }

    public void setConfidentialUserData(ConfidentialUserData confidentialUserData) {
        this.confidentialUserData = confidentialUserData;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Uri getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(Uri uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", confidentialUserData=" + confidentialUserData +
                ", login='" + login + '\'' +
                ", uriPhoto=" + uriPhoto +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid) &&
                Objects.equals(confidentialUserData, user.confidentialUserData) &&
                Objects.equals(login, user.login) &&
                Objects.equals(uriPhoto, user.uriPhoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, confidentialUserData, login, uriPhoto);
    }
}
