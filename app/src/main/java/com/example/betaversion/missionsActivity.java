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
import android.widget.ListView;
import android.widget.TextView;

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
    * * @version  	1.0
    * * @since		03/03/2022
    *
    * * This missionsActivity.class displays the all missions by an Event in the business
 * */
public class missionsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    BottomNavigationView bottomNavigationView;
    ListView closeEventsLV;

    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList;
    ArrayList<Integer> employeesList;
    ArrayList<Mission> missionsList;
    TextView eventIdTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        closeEventsLV = findViewById(R.id.closeEventsLV);
        eventIdTV = findViewById(R.id.eventIdTV);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        bottomNavigationView.setSelectedItemId(R.id.missions);
        closeEventsLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        closeEventsLV.setOnCreateContextMenuListener(this);

        titleEvents = new ArrayList<>();
        dateEvents = new ArrayList<>();
        phonesList = new ArrayList<>();
        employeesList = new ArrayList<>();
        eventCharacterizeList = new ArrayList<>();
        namesList = new ArrayList<>();
        missionsList = new ArrayList<>();

        eventIdTV.setText("בחר את האירוע כדי להמשיך");
        readAllCloseEvents();
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

                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList,eventCharacterizeList);
                closeEventsLV.setAdapter(customAdapterEvents);
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

        menu.add("צור אירוע");
        menu.add("משימות");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String option = item.getTitle().toString();
        int position = adpInfo.position;

        if(option.equals("צור אירוע")){
            //Intent si = new Intent(this, newMissionsActivity.class);

            // The parameters

            //startActivity(si);
        } else if (option.equals("משימות")){
            readAllMissionsEvent(titleEvents.get(position));
            eventIdTV.setText("עבור האירוע: "+titleEvents.get(position));
        }

        return super.onContextItemSelected(item);
    }

    public void readAllMissionsEvent(String eventID){
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
                missionsList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    if (Objects.requireNonNull(tempEvent).getEventName().equals(eventID)){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                        missionsList = tempEvent.getEventMissions();
                    }
                }

                CustomAdapterMissions customAdapterEvents = new CustomAdapterMissions(getApplicationContext());
                closeEventsLV.setAdapter(customAdapterEvents);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}