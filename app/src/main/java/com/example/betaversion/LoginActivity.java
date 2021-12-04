package com.example.betaversion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		25/11/2021
 *
 *  * This SignUpActivity.class displays the sign up screen with all the inputs
 *    and add an option for sign in automatically for the next time
 *  */
public class LoginActivity extends AppCompatActivity {

    EditText emailET, passwordET;

    CheckBox checkBox; // The CheckBox object to get the stay connect state.


    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        checkBox = findViewById(R.id.checkBox);

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
     * login method get the user in the system (if the typed user exists) when the button is clicked.
     *
     * @param view the button
     */
    public void login(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
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
     * Move to SignUp Activity method works when the textView in the bottom of the
     * layout is pressed, in order to apply the user to create new account.
     *
     * @param view the the textView in the bottom of the layout
     */
    public void moveToSignUpAct(View view) {
        Intent si = new Intent(this, SignUpActivity.class);
        startActivity(si);
        finish();
    }
}