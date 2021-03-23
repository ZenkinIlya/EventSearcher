package com.startup.eventsearcher.authentication.models.user;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String uid;
    private String login;
    private String uriPhoto;

    public User(){};

    public User(String uid, String login, String uriPhoto) {
        this.uid = uid;
        this.login = login;
        this.uriPhoto = uriPhoto;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(String uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
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
                Objects.equals(login, user.login) &&
                Objects.equals(uriPhoto, user.uriPhoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, login, uriPhoto);
    }
}
