package com.example.betaversion;

import static com.example.betaversion.FBref.refReminders;
import static com.example.betaversion.FBref.reflive_Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


/**
 * * @author    Shahar Yani
 * * @version  	4.0
 * * @since		25/11/2021
 *
 * * This MainActivity.class displays the main control on the business.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    BottomNavigationView bottomNavigationView;

    ListView closeEventsLV, remaindersLV, missionsLV;
    ArrayList<String> titleEvents, dateEvents, phonesList;
    ArrayList<Integer> employeesList;
    ArrayList<Character> eventsCharacterizeList;

    ArrayList<String> remindersTitleList, remindersContextList;
    ArrayList<String> remindersAudioContentList;
    ArrayList<String> remindersLastDateToRemindList;
    ArrayList<Boolean> isTextList;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        closeEventsLV = findViewById(R.id.closeEventsLV);
        remaindersLV = findViewById(R.id.remaindersLV);
        missionsLV = findViewById(R.id.missionsLV);

        bottomNavigationView.setSelectedItemId(R.id.empty); // clear the selection of the bottomNavigationView object

        closeEventsLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        remaindersLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        missionsLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
        closeEventsLV.setOnItemClickListener(this);
        missionsLV.setOnItemClickListener(this);
        remaindersLV.setOnItemClickListener(this);

        remindersTitleList = new ArrayList<>();
        remindersContextList = new ArrayList<>();
        remindersAudioContentList = new ArrayList<>();
        remindersLastDateToRemindList = new ArrayList<>();
        isTextList = new ArrayList<>();

        readAllCloseEvents();
        readAllRemainders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.empty);
    }

    private void readAllCloseEvents() {
        Query query = reflive_Event.child("greenEvent").limitToFirst(2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                eventsCharacterizeList.clear();

                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                    dateEvents.add(tempEvent.getDateOfEvent());
                    phonesList.add(tempEvent.getCustomerPhone());
                    employeesList.add(tempEvent.getEventEmployees());
                    eventsCharacterizeList.add(tempEvent.getEventCharacterize());
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, phonesList, employeesList, eventsCharacterizeList);
                closeEventsLV.setAdapter(customAdapterEvents);

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
                CustomAdapterReminder customAdapterReminder = new CustomAdapterReminder(getApplicationContext(), remindersTitleList, remindersContextList, remindersAudioContentList, isTextList, remindersLastDateToRemindList);
                remaindersLV.setAdapter(customAdapterReminder);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (view == missionsLV){
//            Intent si = new Intent(this, newMissionsActivity.class);
//            si.putExtra("isRemainder", false);
//            si.putExtra("Index", i);
//            startActivity(si);
        }
        else if (view == closeEventsLV){
//            Intent si = new Intent(this, eventsActivity.class);
//            si.putExtra("Index", i);
//            startActivity(si);
        }
        else if (view == remaindersLV){
//            Intent si = new Intent(this, newMissionsActivity.class);
//            si.putExtra("isRemainder", true);
//            si.putExtra("Index", i);
//            startActivity(si);
        }
    }

    public void moveToCreateAnEvent(View view) {
        Toast.makeText(this, "New Event", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "All Events", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,eventsActivity.class);
//                startActivity(si);
//                finish();
        }
        else if (id == R.id.newMissions){
            Toast.makeText(this, "New Missions", Toast.LENGTH_SHORT).show();
//            si = new Intent(this, newMissionsActivity.class);
//            startActivity(si);
//            finish();
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
            Toast.makeText(this, "All Events", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,eventsActivity.class);
//                startActivity(si);
//                finish();
        }
        else if (id == R.id.newMissions){
            Toast.makeText(this, "New Missions", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,newMissionsActivity.class);
//                startActivity(si);
//                finish();
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

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    public void moveToReminderAct(View view) {
        startActivity(new Intent(this, reminderActivity.class));
    }
}