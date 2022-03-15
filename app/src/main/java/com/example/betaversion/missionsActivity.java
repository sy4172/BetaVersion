package com.example.betaversion;

import static com.example.betaversion.FBref.refGreen_Event;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
    * * @author    Shahar Yani
    * * @version  	1.0
    * * @since		03/03/2022
    *
    * * This missionsActivity.class displays the all missions by an Event in the business
 * */
public class missionsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    BottomNavigationView bottomNavigationView;
    ListView generalLV;
    ImageView backToStart;

    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList, missionTitlesList, missionContentsList, missionLastDatesList, missionsKeysList;
    HashMap<String, Mission> allMissions;
    ArrayList<Integer> employeesList, frequencyList;
    ArrayList<Boolean> missionStatusList;
    TextView eventIdTV;
    boolean toMissionsMenu;

    CustomAdapterMissions customAdapterMissions;
    CustomAdapterEvents customAdapterEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        generalLV = findViewById(R.id.generalLV);
        eventIdTV = findViewById(R.id.eventIdTV);
        backToStart = findViewById(R.id.backToStart);

        backToStart.setVisibility(View.INVISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.missions);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        bottomNavigationView.setSelectedItemId(R.id.missions);
        generalLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        generalLV.setOnCreateContextMenuListener(this);

        titleEvents = new ArrayList<>();
        dateEvents = new ArrayList<>();
        phonesList = new ArrayList<>();
        employeesList = new ArrayList<>();
        eventCharacterizeList = new ArrayList<>();
        namesList = new ArrayList<>();

        allMissions = new HashMap<>();
        missionTitlesList = new ArrayList<>();
        missionStatusList = new ArrayList<>();
        missionContentsList = new ArrayList<>();
        missionLastDatesList = new ArrayList<>();
        frequencyList = new ArrayList<>();
        missionsKeysList = new ArrayList<>();

        eventIdTV.setText("בחר אירוע כדי להמשיך");

        readAllCloseEvents();
    }

    public void displayAllEvents(View view) {
        readAllCloseEvents();
        toMissionsMenu = false;
        backToStart.setVisibility(View.INVISIBLE);
        eventIdTV.setText("בחר אירוע כדי להמשיך");
    }

    private void readAllCloseEvents() {
        refGreen_Event.addListenerForSingleValueEvent(new ValueEventListener() {
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
                generalLV.setAdapter(customAdapterEvents);
                toMissionsMenu = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void readAllMissionsEvent(String eventID){
        refGreen_Event.child(eventID).child("eventMissions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                missionTitlesList.clear();
                missionStatusList.clear();
                missionContentsList.clear();
                missionLastDatesList.clear();
                allMissions.clear();
                frequencyList.clear();
                missionsKeysList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Mission tempMission = data.getValue(Mission.class);

                    missionTitlesList.add(Objects.requireNonNull(tempMission).getTitle());
                    missionStatusList.add(tempMission.isText());
                    missionContentsList.add(tempMission.getTextContent());
                    missionLastDatesList.add(tempMission.getLastDateToRemind());
                    frequencyList.add(tempMission.getFrequency());
                    allMissions.put(dS.getKey(),tempMission);
                    missionsKeysList.add(dS.getKey());
                }

                customAdapterMissions = new CustomAdapterMissions(getApplicationContext(),titleEvents ,missionTitlesList, missionStatusList, missionContentsList, missionLastDatesList, frequencyList);
                generalLV.setAdapter(customAdapterMissions);
                toMissionsMenu = true;
                backToStart.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    public void moveToEventsActivity(View view) {
        startActivity(new Intent(this, eventsActivity.class));
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
                Intent si = new Intent(missionsActivity.this, LoginActivity.class);

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
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.missions);

        readAllCloseEvents();
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
            si = new Intent(this, reminderActivity.class);
            startActivity(si);
        }
        else if (id == R.id.events){
            si = new Intent(this,eventsActivity.class);
            startActivity(si);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);

        if (toMissionsMenu){
            menu.add("צפה");
            menu.add("עדכן");
            menu.add("מחק");
        } else {
            menu.add("צור משימה");
            menu.add("משימות");
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String option = item.getTitle().toString();
        int position = adpInfo.position;

        if(option.equals("צור משימה")){
            Intent si = new Intent(this, newMissionActivity.class);
            si.putExtra("eventTitle", titleEvents.get(position));
            si.putExtra("eventID", dateEvents.get(position));
            si.putExtra("updateMode", true);
            startActivity(si);
        }
        else if (option.equals("משימות")){
            readAllMissionsEvent(dateEvents.get(position));
            eventIdTV.setText("עבור האירוע: "+titleEvents.get(position));
        }
        else if (option.equals("צפה")){
            Intent si = new Intent(this, newMissionActivity.class);
            si.putExtra("eventTitle", titleEvents.get(position));
            si.putExtra("eventID", dateEvents.get(position));
            si.putExtra("missionMode", missionStatusList.get(position));
            si.putExtra("updateMode", false);
            si.putExtra("mission", allMissions.get(missionsKeysList.get(position)));
            startActivity(si);
        }
        else if (option.equals("עדכן")){
            Intent si = new Intent(this, newMissionActivity.class);
            si.putExtra("eventTitle", titleEvents.get(position));
            si.putExtra("eventID", dateEvents.get(position));
            si.putExtra("mission",allMissions.get(dateEvents.get(position)+titleEvents.get(position)));
            si.putExtra("missionMode", missionStatusList.get(position));
            si.putExtra("updateMode", true);
            startActivity(si);
        }
        else if (option.equals("מחק")){
            Intent si = new Intent(this, newMissionActivity.class);
            si.putExtra("eventTitle", titleEvents.get(position));
            si.putExtra("eventID", dateEvents.get(position));
            si.putExtra("missionMode", missionStatusList.get(position));
            si.putExtra("updateMode", false);
            startActivity(si);
        }


        return super.onContextItemSelected(item);
    }
}