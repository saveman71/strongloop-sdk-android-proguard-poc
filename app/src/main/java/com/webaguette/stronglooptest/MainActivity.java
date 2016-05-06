package com.webaguette.stronglooptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.webaguette.stronglooptest.loopback.User;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.text);

        Button refreshButton = (Button) findViewById(R.id.refresh_user);
        assert refreshButton != null;
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getInstance().refreshUser(null);
                textView.setText("" + MainApplication.getInstance().user);

                MainApplication.getInstance().getUser(new MainApplication.GetUserCallback() {
                    @Override
                    public void onResult(User user) {
                        Log.d(TAG, "User: " + user);
                        assert textView != null;
                        textView.setText("" + user);
                        if (user != null) {
                            Log.d(TAG, "User: " + user.getIsPremium());

                            SharedPreferences settings = getSharedPreferences("KidsOkPrefs", Context.MODE_PRIVATE);
                            boolean isPremium = settings.getBoolean("isPremium", false);

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("isPremium", user.getIsPremium());
                            editor.apply();
                        }
                        else {
                            Intent login = new Intent(MainActivity.this, SignInActivity.class);
                            startActivityForResult(login, 1);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView textView = (TextView) findViewById(R.id.text);

        MainApplication.getInstance().getUser(new MainApplication.GetUserCallback() {
            @Override
            public void onResult(User user) {
                Log.d(TAG, "User: " + user);
                assert textView != null;
                textView.setText("" + user);
                if (user != null) {
                    Log.d(TAG, "User: " + user.getIsPremium());

                    SharedPreferences settings = getSharedPreferences("KidsOkPrefs", Context.MODE_PRIVATE);
                    boolean isPremium = settings.getBoolean("isPremium", false);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("isPremium", user.getIsPremium());
                    editor.apply();
                }
                else {
                    Intent login = new Intent(MainActivity.this, SignInActivity.class);
                    startActivityForResult(login, 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "canceled");
            }
            else {
                Log.d(TAG, "ok");
            }
        }
    }

}
