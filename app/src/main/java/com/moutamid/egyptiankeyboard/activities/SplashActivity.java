package com.moutamid.egyptiankeyboard.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.fxn.stash.Stash;
import com.moutamid.egyptiankeyboard.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)
            return;

        if (Stash.getBoolean(Constants.IS_LOGGED_IN, true)) {
            Stash.put(Constants.IS_LOGGED_IN, false);

            startActivity(new Intent(this, OnBoardingActivity.class));
        } else {

            startActivity(new Intent(this, HomeActivity.class));
        }

    }
}