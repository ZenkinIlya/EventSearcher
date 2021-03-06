package com.startup.eventsearcher.models.user;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String uid;
    private String login;

    public User(){};

    public User(String uid, String login) {
        this.uid = uid;
        this.login = login;
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

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", login='" + login + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, login);
    }
}
