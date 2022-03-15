package com.example.betaversion;

import static com.example.betaversion.FBref.refGreen_Event;
import static com.example.betaversion.FBref.refOrange_Event;
import static com.example.betaversion.FBref.refReminders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


/**
 * * @author    Shahar Yani
 * * @version  	4.1
 * * @since		25/11/2021
 *
 * * This MainActivity.class displays the main control on the business.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    BottomNavigationView bottomNavigationView;

    ListView beforeApprovalLV, closeEventsLV, remaindersLV, missionsLV;
    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList;
    ArrayList<Integer> employeesList;

    ArrayList<String> remindersTitleList, remindersContextList;
    ArrayList<String> remindersAudioContentList;
    ArrayList<String> remindersLastDateToRemindList;
    ArrayList<Boolean> isTextList;

    ArrayList<String> missionTitlesList, missionLastDatesList, missionContentsList;
    ArrayList<Boolean> missionStatusList;
    ArrayList<Integer> frequencyList;

    CustomAdapterMissions customAdapterMissions;
    CustomAdapterEvents customAdapterEvents;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        beforeApprovalLV = findViewById(R.id.beforeApprovalLV);
        closeEventsLV = findViewById(R.id.closeEventsLV);
        remaindersLV = findViewById(R.id.remaindersLV);
        missionsLV = findViewById(R.id.missionsLV);

        bottomNavigationView.setSelectedItemId(R.id.empty); // clear the selection of the bottomNavigationView object

        beforeApprovalLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        closeEventsLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        remaindersLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        missionsLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
        beforeApprovalLV.setOnItemClickListener(this);
        closeEventsLV.setOnItemClickListener(this);
        missionsLV.setOnItemClickListener(this);
        remaindersLV.setOnItemClickListener(this);


        remindersTitleList = new ArrayList<>();
        remindersContextList = new ArrayList<>();
        remindersAudioContentList = new ArrayList<>();
        remindersLastDateToRemindList = new ArrayList<>();
        isTextList = new ArrayList<>();

        titleEvents = new ArrayList<>();
        dateEvents = new ArrayList<>();
        phonesList = new ArrayList<>();
        employeesList = new ArrayList<>();
        eventCharacterizeList = new ArrayList<>();
        namesList = new ArrayList<>();

        missionTitlesList = new ArrayList<>();
        missionContentsList = new ArrayList<>();
        missionStatusList = new ArrayList<>();
        missionLastDatesList = new ArrayList<>();
        frequencyList = new ArrayList<>();

        //readAllRemainders();
        readAllCloseEvents();
        readAllBeforeApproval();
    }

    private void readAllBeforeApproval() {
        Query query = refOrange_Event.limitToFirst(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                namesList.clear();
                eventCharacterizeList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                    dateEvents.add(tempEvent.getDateOfEvent());
                    phonesList.add(tempEvent.getCustomerPhone());
                    employeesList.add(tempEvent.getEventEmployees());
                    eventCharacterizeList.add(tempEvent.getEventCharacterize());
                    namesList.add(tempEvent.getCustomerName());
                }

                customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList,eventCharacterizeList);
                closeEventsLV.setAdapter(customAdapterEvents);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readAllCloseEvents() {
        Query query = refGreen_Event.limitToFirst(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                namesList.clear();
                eventCharacterizeList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                    dateEvents.add(tempEvent.getDateOfEvent());
                    phonesList.add(tempEvent.getCustomerPhone());
                    employeesList.add(tempEvent.getEventEmployees());
                    eventCharacterizeList.add(tempEvent.getEventCharacterize());
                    namesList.add(tempEvent.getCustomerName());
                }
                customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList,eventCharacterizeList);
                closeEventsLV.setAdapter(customAdapterEvents);

                readAllMissions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readAllRemainders() {
        Query query = refReminders.limitToFirst(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                remindersTitleList.clear();
                remindersContextList.clear();
                remindersAudioContentList.clear();
                isTextList.clear();
                remindersLastDateToRemindList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Reminder temp = data.getValue(Reminder.class);

                    remindersTitleList.add(Objects.requireNonNull(temp).getTitle());
                    remindersContextList.add(temp.getTextContent());
                    remindersAudioContentList.add(temp.getAudioContent());
                    isTextList.add(temp.isText());
                    remindersLastDateToRemindList.add(temp.getLastDateToRemind());
                }

                // checkDate();
                CustomAdapterReminder customAdapterReminder = new CustomAdapterReminder(getApplicationContext(), remindersTitleList, remindersContextList, remindersAudioContentList, isTextList, remindersLastDateToRemindList);
                remaindersLV.setAdapter(customAdapterReminder);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readAllMissions() {
        Toast.makeText(this, dateEvents.size()+"", Toast.LENGTH_SHORT).show();
        if (dateEvents.size() != 0){
            for (int i = 0; i < dateEvents.size(); i++) {
                if (eventCharacterizeList.get(i).equals("G")){
                    refGreen_Event.child(dateEvents.get(i)).child("eventMissions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dS) {
                            missionTitlesList.clear();
                            missionStatusList.clear();
                            missionContentsList.clear();
                            missionLastDatesList.clear();
                            frequencyList.clear();

                            for(DataSnapshot data : dS.getChildren()) {
                                Mission tempMission = data.getValue(Mission.class);

                                missionTitlesList.add(Objects.requireNonNull(tempMission).getTitle());
                                missionStatusList.add(tempMission.isText());
                                missionContentsList.add(tempMission.getTextContent());
                                missionLastDatesList.add(tempMission.getLastDateToRemind());
                                frequencyList.add(tempMission.getFrequency());
                            }

                            customAdapterMissions = new CustomAdapterMissions(getApplicationContext(), titleEvents, missionTitlesList, missionStatusList, missionContentsList, missionLastDatesList, frequencyList);
                            missionsLV.setAdapter(customAdapterMissions);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (view  == beforeApprovalLV){
//            Intent si = new Intent(this, events.class);
//            si.putExtra("Index", i);
//            startActivity(si);
        }
        else if (view == missionsLV){
            Intent si = new Intent(this, missionsActivity.class);
            si.putExtra("Index", i);
            startActivity(si);
        }
        else if (view == closeEventsLV){
            Intent si = new Intent(this, eventsActivity.class);
            si.putExtra("Index", i);
            startActivity(si);
        }
        else if (view == remaindersLV){
            Intent si = new Intent(this, reminderActivity.class);
            si.putExtra("Index", i);
            startActivity(si);
        }
    }

    public void moveToCreateAnEvent(View view) {
        Intent si = new Intent(this, newEventActivity.class);
        startActivity(si);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;

        if (id == R.id.settingsAct){
            si = new Intent(this, settingsActivity.class);
            startActivity(si);
        }
        else if (id == R.id.remainder){
            si = new Intent(this, reminderActivity.class);
            startActivity(si);
        }
        else if (id == R.id.events){
            si = new Intent(this, eventsActivity.class);
            startActivity(si);
        }
        else if (id == R.id.missions){
            si = new Intent(this, missionsActivity.class);
            startActivity(si);
        }
        else{
            return false;
        }

        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;

       if (id == R.id.settingsAct){
            si = new Intent(this, settingsActivity.class);
            startActivity(si);
        }
        else if (id == R.id.remainder){
            si = new Intent(this, reminderActivity.class);
            startActivity(si);
        }
        else if (id == R.id.events){
            si = new Intent(this,eventsActivity.class);
            startActivity(si);
        }
        else if (id == R.id.missions){
            si = new Intent(MainActivity.this,missionsActivity.class);
            startActivity(si);
        }
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
                Intent si = new Intent(MainActivity.this, LoginActivity.class);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bottomNavigationView.setSelectedItemId(R.id.empty);
        //readAllRemainders();
//        readAllCloseEvents();
//        readAllBeforeApproval();
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    public void moveToReminderAct(View view) {
        startActivity(new Intent(this, reminderActivity.class));
    }

    public void moveToEventsActivity(View view) {
        startActivity(new Intent(this, eventsActivity.class));
    }

    public void moveToMissionsActivity(View view) {
        startActivity(new Intent(this, missionsActivity.class));
    }
}