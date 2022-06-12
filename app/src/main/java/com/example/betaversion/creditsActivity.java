package com.example.betaversion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


/**
 * * @author    Shahar Yani
 * * @version  	1.0
 * * @since		20/04/2022
 * <p>
 * * This creditsActivity.class displays the credits and an option to content through mail.
 */
public class creditsActivity extends AppCompatActivity {

    CheckBox locationCB, recordCB, writeStorageCB, readStorageCB;
    TextView currentUserEmailTV;

    final int LOCATION_PERMISSION_CODE = 11;
    final int READ_DATA_PERMISSION_CODE = 12;
    final int WRITE_DATA_PERMISSION_CODE = 13;
    final int RECORD_PERMISSION_CODE = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        locationCB = findViewById(R.id.locationCB);
        recordCB = findViewById(R.id.recordCB);
        writeStorageCB = findViewById(R.id.writeStorageCB);
        readStorageCB = findViewById(R.id.readStorageCB);
        currentUserEmailTV = findViewById(R.id.currentUserEmailTV);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();

        currentUserEmailTV.setText("מחובר כעת: "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
        displayPermissionsStatus();

    }

    private void displayPermissionsStatus() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            readStorageCB.setChecked(true);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            writeStorageCB.setChecked(true);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationCB.setChecked(true);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            recordCB.setChecked(true);
        }
    }

    /**
     * Logout method for logout from the user.
     *
     * @param item the item in the layout_top_bar.xml
     */
    public void Logout(MenuItem item) {
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("יציאה ממערכת");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);

        final TextView messageTV = new TextView(this);
        messageTV.setText("החשבון "+ FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))+" ינותק.");
        messageTV.setTextSize(18);
        messageTV.setGravity(Gravity.RIGHT);
        messageTV.setPadding(0,15,30,0);
        messageTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        adb.setView(messageTV);

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("צא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent si = new Intent(creditsActivity.this, LoginActivity.class);

                // Changing the preferences to default
                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("stayConnect",false);
                editor.apply();

                startActivity(si);
                finish();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * Move to previous act.
     *
     * @param view the view
     */
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
                .appendQueryParameter("body", "תיאור הפנייה:"+"\n")
                .build();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(emailIntent, "שלח באמצעות..."));
    }

    /**
     * React to click according the Switch objects.
     *
     * @param view the view
     */
    public void reactToClick(View view) {
        switch (view.getId()){
            case (R.id.locationCB): {
                if (locationCB.isChecked()) ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
            break;

            case (R.id.recordCB): {
                if (recordCB.isChecked()){
                    ActivityCompat.requestPermissions(creditsActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_CODE);
                }
                else recordCB.setChecked(false);
            }
            break;

            case (R.id.readStorageCB): {
                if (readStorageCB.isChecked()){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_DATA_PERMISSION_CODE);
                }
            }
            break;

            case (R.id.writeStorageCB): {
                if (writeStorageCB.isChecked()){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_DATA_PERMISSION_CODE);
                }
            }
            break;
        }
    }

    // React to the prmissions requqest
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationCB.setChecked(true);
                } else {
                    locationCB.setChecked(false);
                    Snackbar.make(currentUserEmailTV, "אין גישה למיקום המכשיר", 2000).show();
                }
                break;
            }

            case RECORD_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordCB.setChecked(true);
                } else {
                    recordCB.setChecked(false);
                    Snackbar.make(currentUserEmailTV, "אין אפשרות להקלטה", 2000).show();
                }
                break;
            }

            case READ_DATA_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readStorageCB.setChecked(true);
                } else {
                    readStorageCB.setChecked(false);
                    Snackbar.make(currentUserEmailTV, "אין גישה ליכרון לצורך קריאה", 2000).show();
                }
                break;
            }

            case WRITE_DATA_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeStorageCB.setChecked(true);
                } else {
                    writeStorageCB.setChecked(false);
                    Snackbar.make(currentUserEmailTV, "אין גישה ליכרון לצורך כתיבה", 2000).show();
                }
                break;
            }

        }
        // Other 'case' lines to check for other
        // permissions this app might request.

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

