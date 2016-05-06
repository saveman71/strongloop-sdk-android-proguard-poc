package com.webaguette.stronglooptest;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.Log;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.webaguette.stronglooptest.loopback.User;
import com.webaguette.stronglooptest.loopback.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainApplication extends Application {

    private static MainApplication instance;
    private final String TAG = "MainApplication";

    public RestAdapter restAdapter = null;
    public UserRepository userRepository = null;
    public User user = null;

    public ActivityInfo lastActivity = null;
    public Date lastActivityChange = null;
    public Date lastPackageChange = null;
    public boolean lockEnabled = false;
    public boolean hasWarned = false;

    BroadcastReceiver mReceiver = null;

    private boolean mUserFetchedOnce = false;
    private boolean mIsCurrentlyFetchingUser = false;
    private List<GetUserCallback> mGetUserCallbacks = new ArrayList<>();

    private long mElapsedTimeDay = -1;

    public Date lastAutomaticRestart = null;

    private Handler mHandler = new Handler();

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        restAdapter = new RestAdapter(this, "http://10.0.2.2:3000/api");
        userRepository = restAdapter.createRepository(UserRepository.class);

        getUser(null);
    }

    public void getUser(GetUserCallback cb) {

        if (user != null) {
            cb.onResult(user);
            return;
        }

        user = userRepository.getCachedCurrentUser();
        if (user != null) {
            cb.onResult(user);
            return;
        }

        if (mUserFetchedOnce) {
            cb.onResult(null);
            return;
        }

        if (cb != null) {
            mGetUserCallbacks.add(cb);
        }

        if (mIsCurrentlyFetchingUser) {
            return;
        }

        final ObjectCallback<User> callback = new ObjectCallback<User>() {
            private final int mMaxRetries = 3;
            private int mRetries = 0;

            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "onSuccess user: " + user);
                mUserFetchedOnce = true;
                mIsCurrentlyFetchingUser = false;
                MainApplication.this.user = user;

                for (GetUserCallback cb : mGetUserCallbacks) {
                    cb.onResult(user);
                }
                mGetUserCallbacks.clear();
            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "onError", t);
                MainApplication.this.user = null;
                if (t instanceof java.io.IOException) {
                    final ObjectCallback<User> self = this;
                    final Runnable r = new Runnable() {
                        public void run() {
                            Log.d(TAG, "RUN");
                            if (mRetries > 1) {
                                restAdapter = new RestAdapter(MainApplication.this, "https://limit.kids-ok.com/api");
                                userRepository = restAdapter.createRepository(UserRepository.class);
                            }
                            userRepository.findCurrentUser(self);
                        }
                    };
                    Log.d(TAG, "Retry " + mRetries);
                    if (mRetries < mMaxRetries) {
                        mRetries++;
                        mHandler.postDelayed(r, mRetries * 500);
                        return;
                    }
                }
                mIsCurrentlyFetchingUser = false;
                mUserFetchedOnce = true;
                for (GetUserCallback cb : mGetUserCallbacks) {
                    cb.onResult(null);
                }
                mGetUserCallbacks.clear();
            }
        };

        mIsCurrentlyFetchingUser = true;
        userRepository.findCurrentUser(callback);
    }

    public void refreshUser(final GetUserCallback cb) {
        user = null;
        restAdapter = new RestAdapter(MainApplication.this, "https://limit.kids-ok.com/api");
        userRepository = restAdapter.createRepository(UserRepository.class);
        mUserFetchedOnce = false;
        getUser(cb);
    }

    public interface GetUserCallback {
        void onResult(User user);
    }
}
