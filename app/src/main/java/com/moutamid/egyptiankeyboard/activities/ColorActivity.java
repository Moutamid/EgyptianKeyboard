package com.moutamid.egyptiankeyboard.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.egyptiankeyboard.R;

public class ColorActivity extends AppCompatActivity {
    MaterialCardView red, green, blue;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        red = findViewById(R.id.red);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> onBackPressed());

        red.setOnClickListener(v -> {
            Stash.put("color", "red");
            Toast.makeText(this, "Red Theme Applied to Keyboard", Toast.LENGTH_SHORT).show();
        });
        green.setOnClickListener(v -> {
            Stash.put("color", "green");
            Toast.makeText(this, "Green Theme Applied to Keyboard", Toast.LENGTH_SHORT).show();
        });
        blue.setOnClickListener(v -> {
            Stash.put("color", "blue");
            Toast.makeText(this, "Blue Theme Applied to Keyboard", Toast.LENGTH_SHORT).show();
        });


    }
}