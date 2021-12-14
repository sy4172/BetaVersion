package com.example.betaversion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(splashActivity.this,SignUpActivity.class);
                splashActivity.this.startActivity(intent);
                splashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}