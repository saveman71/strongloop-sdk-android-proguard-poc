package com.webaguette.stronglooptest.loopback;

public class User extends com.strongloop.android.loopback.User {
    private boolean isPremium;
    private String phoneNumber;

    public boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(boolean premium) {
        isPremium = premium;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
