package com.example.betaversion;

import static com.example.betaversion.FBref.refEnd_Event;
import static com.example.betaversion.FBref.reflive_Event;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	1.1
 * * @since		07/02/2022
 *
 * * This eventsActivity.class displays whole events with sort.
 */
public class eventsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {


    Spinner selectionSpinner;
    ListView eventsList;

    String[] selections = new String[]{"ירוק","כתום","אדום","אפור"}; // Includes all the status of the events
    String userSelection;


    ArrayList<String> titleEvents, dateEvents, phonesList;
    ArrayList<Integer> employeesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        eventsList = findViewById(R.id.eventsList);
        selectionSpinner = findViewById(R.id.selectionSpinner);

        selectionSpinner.setOnItemSelectedListener(this);
        eventsList.setOnItemClickListener(this);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, selections);
        selectionSpinner.setAdapter(adp);

        userSelection = "ירוק"; // To set as a default selection at start

        titleEvents = new ArrayList<>();
        dateEvents = new ArrayList<>();
        phonesList = new ArrayList<>();
        employeesList = new ArrayList<>();
    }

    /**
     * Logout method works when the button in the up toolBar was clicked, this method disconnects the current user in the system of the FireBase authentication.
     *
     * @param item the item at the topBar
     */
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
                Intent si = new Intent(eventsActivity.this, LoginActivity.class);

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        userSelection = selections[i];
        switch (userSelection) {
            case "ירוק":
            {
                Toast.makeText(this, "Green", Toast.LENGTH_SHORT).show();
                readLiveEvents("greenEvent",0);
            }
            break;
            case "כתום":
            {
                Toast.makeText(this, "ORANGE", Toast.LENGTH_SHORT).show();
                readLiveEvents("orangeEvent",1);
            }
            break;
            case "אדום":
            {
                Toast.makeText(this, "RED", Toast.LENGTH_SHORT).show();
                readLiveEvents("redEvent",2);
            }
            break;
            case "אפור":
            {
                refEnd_Event.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        titleEvents.clear();
                        dateEvents.clear();
                        phonesList.clear();
                        employeesList.clear();

                        Event tempEvent;
                        for(DataSnapshot data : dS.getChildren()) {
                            tempEvent = data.getValue(Event.class);

                            titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                            dateEvents.add(tempEvent.getDateOfEvent());
                            phonesList.add(tempEvent.getCustomerPhone());
                            employeesList.add(tempEvent.getEventEmployees());
                        }
                        CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, phonesList, employeesList, null,3);
                        eventsList.setAdapter(customAdapterEvents);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            break;

            default: {}
        }
    }

    private void readLiveEvents(String status, int flagID) {
        reflive_Event.child(status).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();

                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                    dateEvents.add(tempEvent.getDateOfEvent());
                    phonesList.add(tempEvent.getCustomerPhone());
                    employeesList.add(tempEvent.getEventEmployees());
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, phonesList, employeesList, null, flagID);
                eventsList.setAdapter(customAdapterEvents);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}