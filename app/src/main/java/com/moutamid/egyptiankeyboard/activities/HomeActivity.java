package com.moutamid.egyptiankeyboard.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.moutamid.egyptiankeyboard.R;
import com.moutamid.egyptiankeyboard.databinding.ActivityHomeBinding;

public class HomeActivity extends Activity {

//    ProgressDialog progressDialog;

    private ActivityHomeBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Please Wait...");
//        progressDialog.setCancelable(false);

//        setTitle(R.string.settings_name);

        /*b.setColorBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ColorActivity.class));
        });


        b.done.setOnClickListener(v -> {
        });*/
    }


}
