package com.startup.eventsearcher.main.ui.profile.model;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {
    private String login;
    private String password;
    private String name;
    private String surname;
    private String email;

    public Person(){};

    public Person(String login, String password, String name, String surname, String email) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return login.equals(person.login) &&
                password.equals(person.password) &&
                name.equals(person.name) &&
                surname.equals(person.surname) &&
                email.equals(person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password, name, surname, email);
    }
}
