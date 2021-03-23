package com.startup.eventsearcher.authentication.models.user;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Objects;

public class ConfidentialUserData implements Serializable {

    @Expose(serialize = false)
    private String email;
    @Expose(serialize = false)
    private String password;

    public ConfidentialUserData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ConfidentialUserData{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfidentialUserData that = (ConfidentialUserData) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
