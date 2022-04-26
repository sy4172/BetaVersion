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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
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
public class eventsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener, BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    ListView eventsList, selectionLV;
    TextView dateRangeTV;
    String[] selections;
    String userSelection;
    Date dateSt, dateEd;
    BottomNavigationView bottomNavigationView;
    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList, addressList;
    ArrayList<Integer> employeesList;
    ArrayList<Boolean> isPaidList, hasAcceptedList;

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
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        selectionLV.setOnItemClickListener(this);
        eventsList.setOnCreateContextMenuListener(this);

        dateRangeTV.setVisibility(View.GONE);
        bottomNavigationView.setSelectedItemId(R.id.events);
        bottomNavigationView.setItemTextColor(ColorStateList.valueOf(Color.rgb(255, 165, 0)));
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        titleEvents = new ArrayList<>();
        dateEvents = new ArrayList<>();
        phonesList = new ArrayList<>();
        eventCharacterizeList = new ArrayList<>();
        employeesList = new ArrayList<>();
        namesList = new ArrayList<>();
        addressList = new ArrayList<>();
        hasAcceptedList = new ArrayList<>();
        isPaidList = new ArrayList<>();

        dateSt = new Date();
        dateEd = new Date();

        selections = new String[]{"ירוק","ירוק ללא תשלום","כתום","אדום","אירועים שעברו","לפי תאריכים"}; // Includes all the status of the events
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, selections);
        selectionLV.setAdapter(adp);

        Intent gi = getIntent();
        if (gi != null) {
            userSelection = gi.getStringExtra("userSelection");
            if (userSelection != null){
                if (userSelection.equals("כתום")){
                    selectionLV.setSelection(2);
                    readEvents("orangeEvent");
                }
                else {
                    userSelection = "ירוק"; // To set as a default selection at start
                    readEvents("greenEvent"); // Show the default option
                    selectionLV.setSelection(0);
                }

            } else {
                userSelection = "ירוק"; // To set as a default selection at start
                readEvents("greenEvent"); // Show the default option
                selectionLV.setSelection(0);
            }
        } else {
            userSelection = "ירוק"; // To set as a default selection at start
            readEvents("greenEvent"); // Show the default option
            selectionLV.setSelection(0);
        }
    }

    /**
     * onResume method inorder to react when the user return to THIS activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        bottomNavigationView.setSelectedItemId(R.id.events);
        bottomNavigationView.setItemTextColor(ColorStateList.valueOf(Color.rgb(255, 165, 0)));

        Intent gi = getIntent();
        if (gi != null) {
            userSelection = gi.getStringExtra("userSelection");
            if (userSelection != null && userSelection.equals("כתום")) {
                selectionLV.setSelection(2);
                readEvents("orangeEvent");
            } else {
                userSelection = "ירוק"; // To set as a default selection at start
                readEvents("greenEvent"); // Show the default option
                selectionLV.setSelection(0);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);
        menu.add("צפייה");
        menu.add("ערוך");
        menu.add("נווט");
        menu.add("מחק");
        if (userSelection.equals("ירוק ללא תשלום")){
            menu.add("סמן כשולם");
        }

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
            // Remove from the FireBase
            Snackbar.make(selectionLV, "האירוע "+titleEvents.get(position)+" נמחק", 3000).show();
            removeFromFB(dateEvents.get(position), eventCharacterizeList.get(position));
            titleEvents.remove(position);
            dateEvents.remove(position);
            eventCharacterizeList.remove(position);
            employeesList.remove(position);
            namesList.remove(position);
            addressList.remove(position);

            CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
            eventsList.setAdapter(customAdapterEvents);
        }
        else if (option.equals("סמן כשולם")){
            isPaidList.set(position, true);
            // Update the FireBase DataBase
            refGreen_Event.child(dateEvents.get(position)).child("paid").setValue(true);
            checkMoney();
            Snackbar.make(selectionLV, "האירוע "+titleEvents.get(position)+" שולם", 5000).setAction("בטל", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPaidList.set(position, false);
                    // Update the FireBase DataBase
                    refGreen_Event.child(dateEvents.get(position)).child("paid").setValue(false);
                    CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                    eventsList.setAdapter(customAdapterEvents);
                }
            }).show();
        }
        return super.onContextItemSelected(item);
    }

    /**
     * removeFromFB is responsible to remove the event when it's selected
     * @param eventId the event id of the selected event
     * @param character the character of the selected event
     */
    private void removeFromFB(String eventId, String character) {
        switch (character){
            case ("G"):{
                refGreen_Event.child(eventId).removeValue();
            }
            break;

            case ("O"):{
                refOrange_Event.child(eventId).removeValue();
            }
            break;

            case ("R"):{
                reflive_Event.child("redEvent").child(eventId).removeValue();
            }
            break;

            default:{

            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        userSelection = selections[i];
        dateRangeTV.setText("");
        switch (userSelection) {
            case "ירוק": {
                readEvents("greenEvent");
            }
            break;
            case "ירוק ללא תשלום":{
                checkMoney();
            }
            break;
            case "כתום": {
                readEvents("orangeEvent");
            }
            break;
            case "אדום": {
                readEvents("redEvent");
            }
            break;
            case "אירועים שעברו": {
                refEnd_Event.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        titleEvents.clear();
                        dateEvents.clear();
                        phonesList.clear();
                        employeesList.clear();
                        namesList.clear();
                        isPaidList.clear();
                        hasAcceptedList.clear();

                        Event tempEvent;
                        for (DataSnapshot data : dS.getChildren()) {
                            tempEvent = data.getValue(Event.class);

                            titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                            dateEvents.add(tempEvent.getDateOfEvent());
                            phonesList.add(tempEvent.getCustomerPhone());
                            employeesList.add(tempEvent.getEventEmployees());
                            eventCharacterizeList.add(tempEvent.getEventCharacterize());
                            namesList.add(tempEvent.getCustomerName());
                            isPaidList.add(tempEvent.isPaid());
                            hasAcceptedList.add(tempEvent.isHasAccepted());
                        }
                        CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(), titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                        eventsList.setAdapter(customAdapterEvents);

                        if (titleEvents.isEmpty()){
                            Snackbar.make(dateRangeTV, "אין אירועים במצב זה.", 3000).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            break;
            case "לפי תאריכים": {
                openDateRangeAD();
                if (dateRangeTV.getText().toString().isEmpty()){
                    dateRangeTV.setVisibility(View.GONE);
                } else dateRangeTV.setVisibility(View.VISIBLE);
            }
            break;

            default: {
            }
        }
    }

    /**
     * readEvents is responsible to read and display all events that are in the same status
     * @param status the event status
     */
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
                isPaidList.clear();
                hasAcceptedList.clear();

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
                    isPaidList.add(tempEvent.isPaid());
                    hasAcceptedList.add(tempEvent.isHasAccepted());
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEvents);

                if (titleEvents.isEmpty()){
                    Snackbar.make(dateRangeTV, "אין אירועים במצב זה.", 3000).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * checkMoney is for reading all the events that is paid and display them when the
     * userSelection is "ירוק ללא תשלום"
     */
    private void checkMoney() {
        refGreen_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                namesList.clear();
                isPaidList.clear();
                hasAcceptedList.clear();

                Event tempEvent;
                for (DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    if (!Objects.requireNonNull(tempEvent).isPaid()){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                        isPaidList.add(tempEvent.isPaid());
                        hasAcceptedList.add(tempEvent.isHasAccepted());
                    }
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * openDateRangeAD is for opening an AlertDialog for getting all the events that is in the range
     * of the dates and display them when the userSelection is "לפי תאריכים"
     */
    @SuppressLint("ResourceAsColor")
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
        titleTV.setTextColor(Color.WHITE);
        titleTV.setBackgroundResource(R.color.orange_500);
        titleTV.setPadding(0,10,10,0);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.rubik_semibold);
        titleTV.setTextSize(25);
        titleTV.setTypeface(typeface);

        dpd.setCustomTitle(titleTV);
        dpd.setCancelable(false);
        dpd.show();
    }

    /**
     * openDateEndPicker is for opening an AlertDialog for getting the end date for the dates range
     * according to userSelection is "לפי תאריכים"
     */
    @SuppressLint("ResourceAsColor")
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
                dateRangeTV.setVisibility(View.VISIBLE);
                if (checkDates()){
                    dateRangeTV.setText("טווח תאריכים: "+strDateEd+" - "+strDateSt);
                    readAllEventsByDate();
                }
            }
        }, year, month, day);
        final TextView titleTV = new TextView(this);
        titleTV.setText("תאריך סיום");
        titleTV.setTextColor(Color.WHITE);
        titleTV.setBackgroundResource(R.color.orange_500);
        titleTV.setPadding(0,10,10,0);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.rubik_semibold);
        titleTV.setTextSize(25);
        titleTV.setTypeface(typeface);

        dpd.setCustomTitle(titleTV);
        dpd.setCancelable(false);
        dpd.show();
    }

    /**
     * checkDates method is for checking the selected dates from the user and the return accordingly.
     * @return True when the selected dates are both good. Otherwise, False
     */
    private boolean checkDates() {
        boolean flag = true;

        if (dateSt.equals(dateEd)){
            Snackbar.make(dateRangeTV, "תאריכים זהים", 3000).show();
            flag = false;
        } else if (dateEd.before(dateSt)){
            Snackbar.make(dateRangeTV, "טווח לא אפשרי", 3000).show();
            flag = false;
        } else if (dateSt.after(dateEd)){
            Snackbar.make(dateRangeTV, "טווח לא אפשרי", 3000).show();
            flag = false;
        }
        return flag;
    }

    /**
     * readAllEventsByDate is for reading all the events from the FireBase DataBase that is in the range
     * of the dates which have been detected and display them when the userSelection is "לפי תאריכים"
     */
    private void readAllEventsByDate() {
        refGreen_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titleEvents.clear();
                dateEvents.clear();
                phonesList.clear();
                employeesList.clear();
                namesList.clear();
                isPaidList.clear();
                hasAcceptedList.clear();

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

                    if (dateSt.compareTo(tempEventSelectedDate) <= 0 && dateEd.compareTo(tempEventSelectedDate) >= 0){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                        isPaidList.add(tempEvent.isPaid());
                        hasAcceptedList.add(tempEvent.isHasAccepted());
                    }
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
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
                        isPaidList.add(tempEvent.isPaid());
                        hasAcceptedList.add(tempEvent.isHasAccepted());
                    }
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEvents);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (namesList.isEmpty()){
            Snackbar.make(selectionLV, "אין אירועים במערכת", 5000).show();
        }
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    public void moveToCreateAnEvent(View view) {
        startActivity(new Intent(this, newEventActivity.class));
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

        if (id == R.id.settingsAct){
            startActivity(new Intent(this, settingsActivity.class));
        }
        else if (id == R.id.remainder){
            startActivity(new Intent(this, remindersActivity.class));
        }
        else if (id == R.id.missions){
            startActivity(new Intent(this, missionsActivity.class));
        }
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
                Intent si = new Intent(eventsActivity.this, LoginActivity.class);

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