package com.example.betaversion;

import static com.example.betaversion.FBref.refEnd_Event;
import static com.example.betaversion.FBref.refGreen_Event;
import static com.example.betaversion.FBref.refOrange_Event;
import static com.example.betaversion.FBref.reflive_Event;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	2.5
 * * @since		07/02/2022
 *
 * * This eventsActivity.class displays whole events with sort.
 */
public class eventsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {

    ListView eventsList, selectionLV;
    TextView dateRangeTV;
    // TODO: Need to think about all the options to display in the ListView eventsList object
    String[] selections = new String[]{"ירוק","כתום","אדום","אפור","לפי תאריך"}; // Includes all the status of the events
    String userSelection;

    Date dateSt, dateEd;

    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList, addressList;
    ArrayList<Integer> employeesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        eventsList = findViewById(R.id.eventsList);
        selectionLV = findViewById(R.id.selectionLV);
        dateRangeTV = findViewById(R.id.dateRangeTV);


        selectionLV.setOnItemClickListener(this);
        eventsList.setOnCreateContextMenuListener(this);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, selections);
        selectionLV.setAdapter(adp);

        userSelection = "ירוק"; // To set as a default selection at start

        titleEvents = new ArrayList<>();
        dateEvents = new ArrayList<>();
        phonesList = new ArrayList<>();
        eventCharacterizeList = new ArrayList<>();
        employeesList = new ArrayList<>();
        namesList = new ArrayList<>();
        addressList = new ArrayList<>();

        readEvents("greenEvent"); // Show the default option

        dateSt = new Date();
        dateEd = new Date();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);
        menu.add("צפייה");
        menu.add("ערוך");
        menu.add("נווט");
        menu.add("מחק");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String option = item.getTitle().toString();
        int position = adpInfo.position;

        if(option.equals("צפייה")){
            Intent si = new Intent(this, newEventActivity.class);
            si.putExtra("eventID",dateEvents.get(position));
            si.putExtra("flag", eventCharacterizeList.get(position));
            si.putExtra("editingMode",false);
            startActivity(si);
        }
        else if (option.equals("ערוך")){
            Intent si = new Intent(this, newEventActivity.class);
            si.putExtra("eventID",dateEvents.get(position));
            si.putExtra("flag", eventCharacterizeList.get(position));
            si.putExtra("updatingMode",true);
            startActivity(si);
        }
        else if (option.equals("נווט")){
            Intent si = new Intent(this, MapActivity.class);
            si.putExtra("address", addressList.get(position));
            startActivity(si);
        }
        else if (option.equals("מחק")){
        }

        return super.onContextItemSelected(item);
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


    private void readEvents(String status) {
        reflive_Event.child(status).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                eventCharacterizeList.clear();
                namesList.clear();
                addressList.clear();

                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                    dateEvents.add(tempEvent.getDateOfEvent());
                    phonesList.add(tempEvent.getCustomerPhone());
                    eventCharacterizeList.add(tempEvent.getEventCharacterize());
                    employeesList.add(tempEvent.getEventEmployees());
                    namesList.add(tempEvent.getCustomerName());
                    addressList.add(tempEvent.getEventLocation());
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList);
                eventsList.setAdapter(customAdapterEvents);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        userSelection = selections[i];
        dateRangeTV.setText("");
        switch (userSelection) {
            case "ירוק": {
                Toast.makeText(this, "Green", Toast.LENGTH_SHORT).show();
                readEvents("greenEvent");
            }
            break;
            case "כתום": {
                Toast.makeText(this, "ORANGE", Toast.LENGTH_SHORT).show();
                readEvents("orangeEvent");
            }
            break;
            case "אדום": {
                Toast.makeText(this, "RED", Toast.LENGTH_SHORT).show();
                readEvents("redEvent");
            }
            break;
            case "אפור": {
                refEnd_Event.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        titleEvents.clear();
                        dateEvents.clear();
                        phonesList.clear();
                        employeesList.clear();
                        namesList.clear();

                        Event tempEvent;
                        for (DataSnapshot data : dS.getChildren()) {
                            tempEvent = data.getValue(Event.class);

                            titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                            dateEvents.add(tempEvent.getDateOfEvent());
                            phonesList.add(tempEvent.getCustomerPhone());
                            employeesList.add(tempEvent.getEventEmployees());
                            eventCharacterizeList.add(tempEvent.getEventCharacterize());
                            namesList.add(tempEvent.getEventName());
                        }
                        CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(), titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList);
                        eventsList.setAdapter(customAdapterEvents);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            break;
            case "לפי תאריך": {
                openDateRangeAD();
                readAllEventsByDate();
            }
            break;

            default: {
            }

//        if (view == selectionLV){
//
//            }
//        } else {
//
//        }
        }
    }

    private void openDateRangeAD() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month++, dayOfMonth);
                dateSt = new Date(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),0,0,0);
                openDateEndPicker();
            }
        }, year, month, day);
        final TextView titleTV = new TextView(this);
        titleTV.setText("תאריך התחלה");

        dpd.setCustomTitle(titleTV);
        dpd.show();
    }

    private void openDateEndPicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month++, dayOfMonth);
                dateEd = new Date(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDateEd = dateFormat.format(dateEd);
                String strDateSt = dateFormat.format(dateSt);
                dateRangeTV.setText("טווח תאריכים: "+strDateSt+" - "+strDateEd);
            }
        }, year, month, day);
        final TextView titleTV = new TextView(this);
        titleTV.setText("תאריך סיום");

        dpd.setCustomTitle(titleTV);
        dpd.show();
    }

    private void readAllEventsByDate() {
        refGreen_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                namesList.clear();

                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    DateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);
                    Date tempEventSelectedDate  = null;
                    try {
                        tempEventSelectedDate = format.parse(Objects.requireNonNull(tempEvent).getDateOfEvent());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (Objects.requireNonNull(tempEventSelectedDate).after(dateSt) && tempEventSelectedDate.before(dateEd)){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                    }
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList);
                eventsList.setAdapter(customAdapterEvents);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        refOrange_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    DateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);
                    Date tempEventSelectedDate  = null;
                    try {
                        tempEventSelectedDate = format.parse(Objects.requireNonNull(tempEvent).getDateOfEvent());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (Objects.requireNonNull(tempEventSelectedDate).after(dateSt) && tempEventSelectedDate.before(dateEd)){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                    }
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList);
                eventsList.setAdapter(customAdapterEvents);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }
}