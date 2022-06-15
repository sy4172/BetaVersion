package com.example.betaversion;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  * @author		Shahar Yani
 *  * @version  	3.0
 *  * @since		25/11/2021
 *
 *  * This SignUpActivity.class displays the sign up screen with all the inputs
 *    and add an option for sign in automatically for the next time
 *  */
public class LoginActivity extends AppCompatActivity {

    EditText emailET, passwordET, phoneET;
    TextView statusTV, messageTV, currentUserTV;
    CheckBox checkBox; // The CheckBox object to get the stay connect state.

    LinearLayout phoneLayout;
    boolean isSignUp;

    LinearLayout layoutView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private static final String TAG = "EmailPassword";

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        phoneET = findViewById(R.id.phoneET);
        checkBox = findViewById(R.id.checkBox);
        statusTV = findViewById(R.id.statusTV);
        phoneLayout = findViewById(R.id.phoneLayout);
        messageTV = findViewById(R.id.messageTV);
        layoutView = findViewById(R.id.layoutView);
        currentUserTV = findViewById(R.id.currentUserTV);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        statusTV.setText("הרשמה");
        isSignUp = true;
        phoneLayout.setVisibility(View.VISIBLE);

        if (!checkInternetConnection()){
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run(){
                    // Asking to connect to the Internet connection
                    // Action: startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                    // Responds to click on the action
                    Snackbar.make(layoutView, "אין חיבור לאינטרנט", 5000).setAction("התחבר", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).show();
                }
            };
            handler.postDelayed(r,1000);
        }

        mAuth = FirebaseAuth.getInstance();
        // Getting the current user from the FirebaseAuth
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                currentUser = user;
            }
        };

        mAuthListener.onAuthStateChanged(mAuth);
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            currentUserTV.setText("אין משתמש מחובר במערכת");
        } else currentUserTV.setText("משתמש מחובר במערכת:"+"\n"+currentUser.getEmail());
    }

    /**
     * checkInternetConnection method checks is there is any INTERNET connection.
     * if there is no connection - returns False. Otherwise, returns Ture;
     *
     */
    @SuppressLint("MissingPermission")
    private boolean checkInternetConnection() {
        boolean connected = true;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        boolean toSkip = settings.getBoolean("stayConnect",false);

        currentUser = mAuth.getCurrentUser();

        if (toSkip && (currentUser != null) && checkInternetConnection()){
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
            finish();
        }
        else if (!checkInternetConnection()){
            // Asking to connect to the Internet
           // Action: startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

           // Responds to click on the action
           Snackbar.make(layoutView, "אין חיבור לאינטרנט", 10000).setAction("התחבר", new View.OnClickListener() {
                @Override
               public void onClick(View view) {
                   startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).show();
        }
    }

    /**
     * login method get the user in the system (if the typed user exists) when the button is clicked.
     *
     */
    public void login() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean flag = true; // In order to chekc if all the conditions are good.
        if(email.isEmpty()){
            emailET.setError("חסר כתובת אימייל");
            emailET.requestFocus();
            flag = false;
        } else{
            // Checks if the email is valid and verified
            Matcher matcher =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email);
            if (!matcher.find()){
                emailET.setError("כתובת אימייל לא חוקית");
                emailET.requestFocus();
                flag = false;
            }
        }
        if (TextUtils.isEmpty(password)){
            passwordET.setError("שדה לא יהיה ריק");
            passwordET.requestFocus();
            flag = false;
        }

        if (currentUser == null && flag){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                                Intent si = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(si);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Snackbar.make(layoutView, "אין משתמש כזה במערכת", 3000).show();
                                updateUI(null);
                            }
                        }
                    });
        }
        else if (flag){
            anotherUserAD();
        }
    }

    private void anotherUserAD() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("אין אפשרות כניסה");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);

        final TextView messageTV = new TextView(this);
        messageTV.setText("החשבון "+FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))+" מחובר למערכת.");
        messageTV.setTextSize(18);
        messageTV.setGravity(Gravity.RIGHT);
        messageTV.setPadding(0,15,30,0);
        messageTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        adb.setView(messageTV);

        adb.setNegativeButton("אוקיי", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * Create user method checks the inputted data when the button is clicked.
     *
     */
    public void createUser() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String phone = phoneET.getText().toString();

        boolean flag = true; // In order to check that all the conditions are good
        if(email.isEmpty()){
            emailET.setError("חסר כתובת אימייל");
            emailET.requestFocus();
            flag = false;
        } else{
            // Checks if the email is valid and verified
            Matcher matcher =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email);
            if (!matcher.find()){
                emailET.setError("כתובת אימייל לא חוקית");
                emailET.requestFocus();
                flag = false;
            }
        }
        if (TextUtils.isEmpty(password)){
            passwordET.setError("שדה לא יהיה ריק");
            passwordET.requestFocus();
            flag = false;
        }
        else if (phone.length() < 9 || phone.length() > 10){
            phoneET.setError("מספר הטלפון לא חוקי");
            phoneET.requestFocus();
            flag = false;
        }
        else if(phone.length() == 10){
            if (!phone.startsWith("05") || phone.contains("#")){
                phoneET.setError("מספר הטלפון לא חוקי");
                phoneET.requestFocus();
                flag = false;
            }
        }
        else if (phone.length() == 9){
            if (!phone.startsWith("0")){
                phoneET.setError("מספר הטלפון לא חוקי");
                phoneET.requestFocus();
                flag = false;
            }
        }

        if (currentUser == null && flag){
            mAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                                Intent si = new Intent(LoginActivity.this, MainActivity.class);
                                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("smsPhone", phoneET.getText().toString());
                                editor.apply();
                                startActivity(si);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Snackbar.make(layoutView, "יש משתמש רשום במערכת עם פרטים דומים", 3000).show();
                                updateUI(null);
                            }
                        }
                    });
        } else if (flag) {
            anotherUserAD();
        }
    }

    private void updateUI(FirebaseUser user) {
        currentUser = user;
    }

    /**
     * Change connection method changes the state of connecting automatically in the SharedPreference by clicking
     * on the CheckBox object.
     *
     * @param view the CheckBox object
     */
    @SuppressLint("ApplySharedPref")
    public void changeConnection(View view) {
        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect",checkBox.isChecked());
        editor.commit();
    }

    /**
     * Change status method changes the status of the layoutView Sing-In to Sign-Up
     *
     * @param view the TextView object
     */
    public void changeStatus(View view) {
        isSignUp = !isSignUp;
        if (isSignUp){
            messageTV.setText(R.string.toSignIn);
            statusTV.setText("הרשמה");
            phoneLayout.setVisibility(View.VISIBLE);
        }
        else {
            messageTV.setText(R.string.toSignUp);
            statusTV.setText("התחברות");
            phoneLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * reactToLogin method identifies the state of the connecting to the application.
     * [createUser() or login()]
     * @param view the Button object
     */
    public void reactToLogin(View view) {
        if (!checkInternetConnection()){
            // Asking to connect to the Internet
            // Responds to click on the action
            Snackbar.make(layoutView, "אין חיבור לאינטרנט", 10000).setAction("התחבר", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).show();
        }

        if (isSignUp){
            createUser();
        }
        else{
            login();
        }
    }
}