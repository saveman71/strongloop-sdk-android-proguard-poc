package com.webaguette.stronglooptest.loopback;

public class UserRepository
        extends com.strongloop.android.loopback.UserRepository<User> {

    public UserRepository() {
        super("user", null, User.class);
    }

    public interface LoginCallback
            extends com.strongloop.android.loopback.UserRepository.LoginCallback<User> {
    }
}
