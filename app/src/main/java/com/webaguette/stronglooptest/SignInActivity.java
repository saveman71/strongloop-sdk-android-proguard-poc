package com.webaguette.stronglooptest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.strongloop.android.loopback.AccessToken;
import com.webaguette.stronglooptest.loopback.User;
import com.webaguette.stronglooptest.loopback.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button signInButton = (Button) findViewById(R.id.button);
        assert signInButton != null;
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = "email@domain.com";
                final String password = "password";

                MainApplication.getInstance().userRepository.loginUser(email, password, new UserRepository.LoginCallback() {
                    @Override
                    public void onSuccess(AccessToken token, User user) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("error", "error: ", t);
                    }
                });
            }
        });
    }
}


