package com.moutamid.egyptiankeyboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.egyptiankeyboard.databinding.ActivityOnBoardingBinding;
import com.moutamid.egyptiankeyboard.utils.Utils;

public class OnBoardingActivity extends AppCompatActivity {

    private ActivityOnBoardingBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        });


        b.selectKeyboardBtn.setOnClickListener(view -> {
            InputMethodManager imeManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
        });

    }

    //to know when the picker has been closed
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Utils.isKeyboardDefault(OnBoardingActivity.this)) {
            // KEYBOARD IS SET AS DEFAULT
            b.step1Layout.setVisibility(View.GONE);
            b.step2Layout.setVisibility(View.GONE);

            b.step3Layout.setVisibility(View.VISIBLE);
            b.edittext.requestFocus();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isKeyboardEnabled(OnBoardingActivity.this)) {

            if (Utils.isKeyboardDefault(OnBoardingActivity.this)) {
                b.step1Layout.setVisibility(View.GONE);
                b.step2Layout.setVisibility(View.GONE);
                b.step3Layout.setVisibility(View.VISIBLE);
                b.edittext.requestFocus();
            } else {

                // KEYBOARD IS ENABLED
                b.step1Layout.setVisibility(View.GONE);
                b.step2Layout.setVisibility(View.VISIBLE);
                b.step3Layout.setVisibility(View.VISIBLE);
            }
        }

    }

}