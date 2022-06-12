package com.example.betaversion;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class splashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ContextCompat.startForegroundService(splashActivity.this, new Intent(splashActivity.this, BackgroundService.class));

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(splashActivity.this,LoginActivity.class);
                splashActivity.this.startActivity(intent);
                splashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}