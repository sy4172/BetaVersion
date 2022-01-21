package com.example.betaversion;

import static com.example.betaversion.FBref.refBusinessEqu;

import androidx.annotation.NonNull;
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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    ChipGroup chipGroupShows, chipGroupMaterials;

    Date selectedDate, currentDate;

    ArrayList<Shows> allShows;
    ArrayList<Material> allMaterials;
    ArrayList<String> showsKeyList, showsDesList, materialsKeyList, materialsUsedList;
    ArrayList<Integer> showsDataList, materialsDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        eventTitleTV = findViewById(R.id.eventTitleTV);
        flag = findViewById(R.id.flag);
        dateTV = findViewById(R.id.selectedDateTV);
        chipGroupShows = findViewById(R.id.chipGroupShows);
        chipGroupMaterials = findViewById(R.id.chipGroupMaterials);

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
        currentDate = Calendar.getInstance().getTime();

        // For setting the flag color to green
        DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green_flag));

        allShows = new ArrayList<>();
        showsKeyList = new ArrayList<>();
        showsDataList = new ArrayList<>();
        showsDesList = new ArrayList<>();

        allMaterials = new ArrayList<>();
        materialsKeyList = new ArrayList<>();
        materialsDataList = new ArrayList<>();
        materialsUsedList = new ArrayList<>();

        getAllShowsToDisplay();
        getAllMaterialsToDisplay();
    }

    /**
     * getAllShowsToDisplay method gets all the Shows objects that were created to display on the TextView objects from the FireBase DataBase.
     *
     */
    private void getAllShowsToDisplay() {
        refBusinessEqu.child("showsList").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                showsKeyList.clear();
                showsDataList.clear();
                showsDesList.clear();
                allShows.clear();

                for (DataSnapshot data : dS.getChildren()) {
                    Shows tempShow = data.getValue(Shows.class);
                    allShows.add(tempShow);

                    showsKeyList.add(Objects.requireNonNull(tempShow).getShowTitle());
                    showsDataList.add(tempShow.getCost());
                    showsDesList.add(tempShow.getDescription());
                }

                for (int i = 0; i < showsKeyList.size() - 1; i++) {
                    Chip tempChip = new Chip(newEventActivity.this);
                    tempChip.setText(showsKeyList.get(i));
                    tempChip.setTextAppearance(R.style.ChipTextAppearance);
                    tempChip.setChipIconResource(R.drawable.null1);
                    tempChip.setChipBackgroundColorResource(R.color.brown_200);
                    final boolean[] isToAdd = {true};

                    // Adding the onClick method that will add the selected shows to the Event constructor

                    tempChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                            if (isToAdd[0]){
                                tempChip.setChipIconResource(R.drawable.ic_check);
                                tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                isToAdd[0] = false;
                            }
                            else{
                                tempChip.setChipIconResource(R.drawable.null1);
                                isToAdd[0] = true;
                            }
                        }
                    });


                    chipGroupShows.addView(tempChip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * getAllMaterialsToDisplay method gets all the Materials objects that were created to display on the TextView objects from the FireBase DataBase.
     *
     */
    private void getAllMaterialsToDisplay() {
        refBusinessEqu.child("materials").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                materialsKeyList.clear();
                materialsDataList.clear();
                materialsUsedList.clear();
                allMaterials.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Material temp = data.getValue(Material.class);
                    allMaterials.add(temp);

                    materialsKeyList.add(Objects.requireNonNull(temp).getTypeOfMaterial());
                    materialsDataList.add(temp.getTotalAmount());
                    materialsUsedList.add(String.valueOf(temp.getUsedAmount()));
                }

                for (int i = 0; i < materialsKeyList.size() - 1; i++) {
                    Chip tempChip = new Chip(newEventActivity.this);
                    tempChip.setText(materialsKeyList.get(i));
                    tempChip.setTextAppearance(R.style.ChipTextAppearance);
                    tempChip.setChipIconResource(R.drawable.null1);
                    tempChip.setChipBackgroundColorResource(R.color.brown_200);
                    final boolean[] isToAdd = {true};

                    // Adding the onClick method that will add the selected shows to the Event constructor

                    tempChip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                            if (isToAdd[0]){
                                tempChip.setChipIconResource(R.drawable.ic_check);
                                tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                isToAdd[0] = false;
                            }
                            else{
                                tempChip.setChipIconResource(R.drawable.null1);
                                isToAdd[0] = true;
                            }
                        }
                    });


                    chipGroupMaterials.addView(tempChip);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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

    @Override
    public void onBackPressed() {
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