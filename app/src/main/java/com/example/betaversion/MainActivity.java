package com.example.betaversion;

import static com.example.betaversion.FBref.refGreen_Event;
import static com.example.betaversion.FBref.refOrange_Event;
import static com.example.betaversion.FBref.refReminders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
 * * @version  	7.2
 * * @since		25/11/2021
 *
 * * This MainActivity.class displays the main control on the business.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    BottomNavigationView bottomNavigationView;

    ListView beforeApprovalLV, closeEventsLV, remaindersLV, missionsLV; // ListView for ORANGE events, GREEN events, Remainders and Missions

    // ArrayLists for Event objects
    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList;
    ArrayList<Boolean> isPaidList, hasAcceptedList;
    ArrayList<Integer> employeesList;

    // ArrayLists for Reminder objects
    ArrayList<String> remindersTitleList, remindersContextList, remindersAudioContentList, remindersLastDateToRemindList;
    ArrayList<Boolean> isTextList;

    // ArrayLists for Mission objects
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
        isPaidList = new ArrayList<>();
        hasAcceptedList = new ArrayList<>();

        missionTitlesList = new ArrayList<>();
        missionContentsList = new ArrayList<>();
        missionStatusList = new ArrayList<>();
        missionLastDatesList = new ArrayList<>();
        frequencyList = new ArrayList<>();

        readAllRemainders();
        readAllCloseEvents();
        readAllBeforeApproval();
    }

    /**
     * readAllBeforeApproval method gets all the events that with eventCharacterize equals to 'O' from the FireBase DataBase.
     */
    private void readAllBeforeApproval() {
        Query query = refOrange_Event.limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                namesList.clear();
                eventCharacterizeList.clear();
                isPaidList.clear();
                hasAcceptedList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                    dateEvents.add(tempEvent.getDateOfEvent());
                    phonesList.add(tempEvent.getCustomerPhone());
                    employeesList.add(tempEvent.getEventEmployees());
                    eventCharacterizeList.add(tempEvent.getEventCharacterize());
                    namesList.add(tempEvent.getCustomerName());
                    isPaidList.add(tempEvent.isPaid());
                    hasAcceptedList.add(tempEvent.isHasAccepted());
                }

                customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList,eventCharacterizeList, isPaidList, hasAcceptedList);
                beforeApprovalLV.setAdapter(customAdapterEvents);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * readAllCloseEvents method gets all the events that with eventCharacterize equals to 'G' from the FireBase DataBase.
     */
    private void readAllCloseEvents() {
        Query query = refGreen_Event.limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                namesList.clear();
                eventCharacterizeList.clear();
                isPaidList.clear();
                hasAcceptedList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                    dateEvents.add(tempEvent.getDateOfEvent());
                    phonesList.add(tempEvent.getCustomerPhone());
                    employeesList.add(tempEvent.getEventEmployees());
                    eventCharacterizeList.add(tempEvent.getEventCharacterize());
                    namesList.add(tempEvent.getCustomerName());
                    isPaidList.add(tempEvent.isPaid());
                    hasAcceptedList.add(tempEvent.isHasAccepted());
                }
                customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList,eventCharacterizeList, isPaidList, hasAcceptedList);
                closeEventsLV.setAdapter(customAdapterEvents);

                readAllMissions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * readAllRemainders method gets all the Remainder objects that were created to display on the ListView object objects from the FireBase DataBase.
     */
    private void readAllRemainders() {
        Query query = refReminders.limitToFirst(1);
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

    /**
     * readAllMissions method gets all the Mission objects that were created to display on the ListView object from the FireBase DataBase.
     */
    private void readAllMissions() {
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
            Intent si = new Intent(this, eventsActivity.class);
            si.putExtra("Index", i);
            startActivity(si);
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
            Intent si = new Intent(this, remindersActivity.class);
            si.putExtra("Index", i);
            startActivity(si);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsAct){
            startActivity(new Intent(this, settingsActivity.class));
        }
        else if (id == R.id.remainder){
            startActivity(new Intent(this, remindersActivity.class));
        }
        else if (id == R.id.events){
            startActivity(new Intent(this, eventsActivity.class));
        }
        else if (id == R.id.missions){
            startActivity(new Intent(this, missionsActivity.class));
        }
        else{
            return false;
        }

        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;

        if (id == R.id.settingsAct){
            si = new Intent(this, settingsActivity.class);
            startActivity(si);
        }
        else if (id == R.id.remainder){
            si = new Intent(this, remindersActivity.class);
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }

    /**
     * onResume method inorder to react when the user return to THIS activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        bottomNavigationView.setSelectedItemId(R.id.empty);
        readAllRemainders();
        readAllCloseEvents();
        readAllBeforeApproval();
    }

    /**
     * Move to create an event. when the FloatingActionButton is pressed.
     * @param view the FloatingActionButton
     */
    public void moveToCreateAnEvent(View view) {
        startActivity(new Intent(this, newEventActivity.class));
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    public void moveToReminderAct(View view) {
        startActivity(new Intent(this, remindersActivity.class));
    }

    /**
     * Move to eventsActivity.java inorder to display all the GREEN events that appears in the FireBase DateBase.
     *
     * @param view the button that next to the title "אירועים קרובים" is pressed
     */
    public void moveToGreenEvents(View view) {
        Intent si = new Intent(this, eventsActivity.class);
        si.putExtra("userSelection", "ירוק");
        startActivity(si);
    }

    /**
     * Move to eventsActivity.java inorder to display all the ORANGE events that appears in the FireBase DateBase.
     *
     * @param view the button that next to the title "אירועים לפי אישור" is pressed
     */
    public void moveToOrangeEvents(View view) {
        Intent si = new Intent(this, eventsActivity.class);
        si.putExtra("userSelection", "כתום");
        startActivity(si);
    }

    public void moveToMissionsActivity(View view) {
        startActivity(new Intent(this, missionsActivity.class));
    }

    public void moveToCreditsActivity(View view) {
        startActivity(new Intent(this, creditsActivity.class));
    }

    /**
     * Logout method for logout from the user.
     *
     * @param item the item in the layout_top_bar.xml
     */
    public void Logout(MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
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
                Intent si = new Intent(MainActivity.this, LoginActivity.class);

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
}