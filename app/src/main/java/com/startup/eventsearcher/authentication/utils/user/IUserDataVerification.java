package com.startup.eventsearcher.authentication.utils.user;

public interface IUserDataVerification {
    void setDataCorrect(boolean dataCorrect);
    boolean isDataCorrect();
    String verificationPassword(String password);
    String verificationEmail(String email);
    String verificationLogin(String login);
    String comparePasswords(String firstPassword,String secondPassword);
}
