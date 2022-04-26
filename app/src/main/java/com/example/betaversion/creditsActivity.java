package com.example.betaversion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	1.0
 * * @since		20/04/2022
 *
 * * This creditsActivity.class displays the credits and an option to content through mail.
 */
public class creditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
    }

    /**
     * Logout method for logout from the user.
     *
     * @param item the item in the layout_top_bar.xml
     */
    public void Logout(MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("להתנתק?");
        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        Variable.setEmailVer(settings.getString("email",""));
        adb.setMessage(Variable.getEmailVer().substring(0,Variable.emailVer.indexOf("@"))+" יצא מהמערכת");

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent si = new Intent(creditsActivity.this, LoginActivity.class);

                // Changing the preferences to default
                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("email", "");
                editor.putBoolean("stayConnect",false);
                editor.apply();

                startActivity(si);
                finish();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    /**
     * openMailService method for reacting to a click on the app email that is displayed on the TextView object
     *
     * @param view the TextView that displays the email
     */
    public void openMailService(View view) {
        String appEmail = getResources().getString(R.string.appEmail);
        Uri uri = Uri.parse("mailto:" + "").buildUpon()
                .appendQueryParameter("to", appEmail)
                .appendQueryParameter("subject", "פנייתך: ")
                .appendQueryParameter("body", "")
                .build();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(emailIntent, "שלח באמצעות..."));
    }
}