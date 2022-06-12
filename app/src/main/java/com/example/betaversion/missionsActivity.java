package com.example.betaversion;

import static com.example.betaversion.FBref.refGreen_Event;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	4.1
 * * @since		03/03/2022
 * <p>
 * * This missionsActivity.class displays the all missions by an Event in the business
 */
public class missionsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    BottomNavigationView bottomNavigationView;
    ListView generalLV;
    ImageView backToStart;

    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList, missionTitlesList, missionContentsList, missionLastDatesList, missionsKeysList;
    HashMap<String, Mission> allMissions;
    ArrayList<Integer> employeesList, frequencyList;
    ArrayList<Boolean> missionStatusList, isPaidList, hasAcceptedList;
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
        bottomNavigationView.setItemTextColor(ColorStateList.valueOf(Color.rgb(255, 165, 0)));
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
        isPaidList = new ArrayList<>();
        hasAcceptedList = new ArrayList<>();

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

    /**
     * Display all events.
     *
     * @param view the view
     */
    public void displayAllEvents(View view) {
        readAllCloseEvents();
        toMissionsMenu = false;
        backToStart.setVisibility(View.INVISIBLE);
        eventIdTV.setText("בחר אירוע כדי להמשיך");
        eventIdTV.setTextColor(Color.rgb(115, 115, 115));
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
                isPaidList.clear();
                hasAcceptedList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    if (Objects.requireNonNull(tempEvent).isHasAccepted()){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                        isPaidList.add(tempEvent.isPaid());
                        hasAcceptedList.add(true);
                    }
                }

                customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList,eventCharacterizeList, isPaidList, hasAcceptedList);
                generalLV.setAdapter(customAdapterEvents);
                toMissionsMenu = false;

                if (titleEvents.isEmpty()){
                    eventIdTV.setText("אין אירועים במערכת");
                    eventIdTV.setTextColor(Color.rgb(178, 34, 34));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Read all missions event.
     *
     * @param eventID the event id
     */
    public void readAllMissionsEvent(String eventID){
        String eventTitle = titleEvents.get(dateEvents.indexOf(eventID));
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

                customAdapterMissions = new CustomAdapterMissions(getApplicationContext(),eventTitle ,missionTitlesList, missionStatusList, missionContentsList, missionLastDatesList, frequencyList);
                generalLV.setAdapter(customAdapterMissions);
                toMissionsMenu = true;
                backToStart.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Move to previous act.
     *
     * @param view the view
     */
    public void moveToPreviousAct(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Move to create an event.
     *
     * @param view the view
     */
    public void moveToCreateAnEvent(View view) {
        startActivity(new Intent(this, newEventActivity.class));
    }

    /**
     * Logout.
     *
     * @param item the item
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
        messageTV.setText("החשבון "+FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))+" ינותק.");
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
                Intent si = new Intent(missionsActivity.this, LoginActivity.class);

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
            si = new Intent(this, remindersActivity.class);
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
            si = new Intent(this, remindersActivity.class);
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
            refGreen_Event.child(dateEvents.get(position)).child("eventMissions").removeValue();  // Remove from FireBase DataBase
            dateEvents.remove(position);
            allMissions.remove(missionsKeysList.get(position));
            missionStatusList.remove(position);
            missionsKeysList.remove(position);

            if (!missionStatusList.isEmpty()){
                customAdapterMissions = new CustomAdapterMissions(getApplicationContext(),titleEvents.get(position) ,missionTitlesList, missionStatusList, missionContentsList, missionLastDatesList, frequencyList);
                generalLV.setAdapter(customAdapterMissions);
            } else{
                readAllCloseEvents();
                toMissionsMenu = false;
                backToStart.setVisibility(View.INVISIBLE);
                eventIdTV.setText("בחר אירוע כדי להמשיך");
                eventIdTV.setTextColor(Color.rgb(115, 115, 115));
            }
        }

        return super.onContextItemSelected(item);
    }
}