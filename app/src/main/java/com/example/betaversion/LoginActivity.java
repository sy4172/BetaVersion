package com.example.betaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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
    TextView statusTV, messageTV;
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

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        statusTV.setText("הרשמה");
        isSignUp = true;
        phoneLayout.setVisibility(View.VISIBLE);

        if (!checkInternetConnection()){
            // Asking to connect to the Internet
            // Action: startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                // Responds to click on the action
            Snackbar.make(layoutView, "לא זוהה חיבור לאינטרנט", 10000).setAction("התחבר", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).show();
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
    }
    /**
     * checkInternetConnection method checks is there is any INTERNET connection.
     * if there is no connection - returns False. Otherwise, returns Ture;
     *
     */
    private boolean checkInternetConnection() {
        boolean connected = false;
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
            Variable.setEmailVer(emailET.getText().toString());
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
            finish();
        } else{
            // Asking to connect to the Internet
            // Action: startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

            // Responds to click on the action
            Snackbar.make(layoutView, "לא זוהה חיבור לאינטרנט", 10000).setAction("התחבר", new View.OnClickListener() {
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
        String phone = phoneET.getText().toString();

        if (TextUtils.isEmpty(email)){
            emailET.setError("The email field can not be empty");
            emailET.requestFocus();
        }
        else if (TextUtils.isEmpty(password)){
            passwordET.setError("The password field can not be empty");
            passwordET.requestFocus();
        }
        else {
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
                                Variable.setEmailVer(email);
                                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("email", Variable.getEmailVer());
                                editor.apply();
                                startActivity(si);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "It might be no user",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    /**
     * Create user method checks the inputted data when the button is clicked.
     *
     */
    public void createUser() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        if (TextUtils.isEmpty(email)){
            emailET.setError("The email field can not be empty");
            emailET.requestFocus();
        }
        else if (TextUtils.isEmpty(password)){
            passwordET.setError("The password filed can not be empty");
            passwordET.requestFocus();
        }
        else{
            User tempUser = new User(passwordET.getText().toString(),emailET.getText().toString());
            mAuth.createUserWithEmailAndPassword(tempUser.getEmail(), tempUser.getPassword() )
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                                Intent si = new Intent(LoginActivity.this, MainActivity.class);
                                Variable.setEmailVer(tempUser.getEmail());
                                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("email", Variable.getEmailVer());
                                editor.apply();
                                startActivity(si);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "The user might be created",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
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