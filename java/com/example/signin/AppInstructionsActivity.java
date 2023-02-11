package com.example.signin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class AppInstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intructions);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);



    }


}