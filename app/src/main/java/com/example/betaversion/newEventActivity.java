package com.example.betaversion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	1.0
 * * @since		30/12/2021
 *
 * * This newEventActivity.class displays and creates all the events of the customers.
 */
public class newEventActivity extends AppCompatActivity {

    TextView eventTitleTV, dateTV;
    ImageView flag;

    Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        eventTitleTV = findViewById(R.id.eventTitleTV);
        flag = findViewById(R.id.flag);
        dateTV = findViewById(R.id.dateTV);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        eventTitleTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeTitleEvent();
                return false;
            }
        });

        selectedDate = new Date();
        // For setting the flag color to green
        DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green_flag));
    }

    private void changeTitleEvent() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("כותרת האירוע");
        final EditText titleET = new EditText(this);
        adb.setView(titleET);

        titleET.setText(eventTitleTV.getText());

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newTitle = titleET.getText().toString();

                eventTitleTV.setText(newTitle);
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    public void checkIfToExit(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("לצאת ולא לשמור?");

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveToPreviousAct();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    private void moveToPreviousAct() {
        super.onBackPressed();
    }


    public void Logout(MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure?");
        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        Variable.setEmailVer(settings.getString("email",""));
        adb.setMessage(Variable.getEmailVer().substring(0,Variable.emailVer.indexOf("@"))+" will logged out");

        adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent si = new Intent(newEventActivity.this, LoginActivity.class);

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

    public void openDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month ++;
                selectedDate = new Date(year,month, dayOfMonth,0,0);
                dateTV.setText(selectedDate.getDate()+"/"+selectedDate.getMonth()+"/"+selectedDate.getYear());

                openTimePicker();
            }
        }, year, month, day);

        dpd.show();
    }

    private void openTimePicker() {
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        TimePickerDialog picker = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker tp, int hour, int minute) {
                        selectedDate.setHours(hour);
                        selectedDate.setMinutes(minute);
                        String temp = dateTV.getText().toString();

                        dateTV.setText(temp +"  "+ selectedDate.getHours()+":"+selectedDate.getMinutes());
                    }
                }, hour, minutes, true);
        picker.show();
    }
}