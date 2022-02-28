package com.example.betaversion;

import static com.example.betaversion.FBref.refBusinessEqu;
import static com.example.betaversion.FBref.refGreen_Event;
import static com.example.betaversion.FBref.refOrange_Event;
import static com.example.betaversion.FBref.reflive_Event;
import static com.example.betaversion.FBref.storageRef;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * * @author    Shahar Yani
 * * @version  	2.0
 * * @since		30/12/2021
 *
 * * This newEventActivity.class displays and creates all the events of the customers.
 */
public class newEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    LinearLayout layout;
    TextView eventTitleTV, dateTV, totalEmployeeTV, eventCostTV;
    ImageView flag;
    ChipGroup chipGroupShows, chipGroupMaterials;
    EditText nameCustomerET, emailCustomerET, phoneCustomerET, locationET, contentET;
    FloatingActionButton fab;

    String customerName, customerPhone, customerEmail, eventLocation, eventContent, userSelection, eventStrDate;
    int eventCost, amountOfUsedMaterial;
    Date selectedDate, currentDate;
    boolean editingMode, updateMode;
    String previousEventDate, previousEventName, previousEventStatus;

    ArrayList<Shows> allShows;
    ArrayList<Material> allMaterials;
    ArrayList<Boolean> selectedMaterials;
    ArrayList<Boolean> selectedShows;
    ArrayList<String> showsKeyList, showsDesList, materialsKeyList, materialsUsedList, allEventsDates;
    ArrayList<Integer> showsDataList, materialsDataList;

    Spinner paymentSpinner;
    String [] paymentSelection; // Need to check according to Yakkov request

    Event newEvent, updatedEvent;
    File rootPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        layout = findViewById(R.id.layout);
        eventTitleTV = findViewById(R.id.eventTitleTV);
        flag = findViewById(R.id.flag);
        nameCustomerET = findViewById(R.id.nameCustomerET);
        emailCustomerET = findViewById(R.id.emailCustomerET);
        phoneCustomerET = findViewById(R.id.phoneCustomerET);
        locationET = findViewById(R.id.locationET);
        contentET = findViewById(R.id.contentET);
        eventCostTV = findViewById(R.id.eventCostTV);

        dateTV = findViewById(R.id.selectedDateTV);
        chipGroupShows = findViewById(R.id.chipGroupShows);
        chipGroupMaterials = findViewById(R.id.chipGroupMaterials);
        paymentSpinner = findViewById(R.id.paymentSpinner);
        totalEmployeeTV = findViewById(R.id.totalEmployeeTV);
        fab = findViewById(R.id.fab);

        paymentSelection = new String[]{"בחר אמצעי תשלום","שוטף +30","שוטף +60","שוטף +90"};
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, paymentSelection);
        paymentSpinner.setAdapter(adp);

        paymentSpinner.setOnItemSelectedListener(this);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        eventTitleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTitleEvent();
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

        editingMode = true;
        updateMode = false;
        amountOfUsedMaterial = -1;
        userSelection = paymentSelection[0];

        Intent gi = getIntent();
        if (gi.getStringExtra("flag") != null){
            String eventId = gi.getStringExtra("eventID");
            String status = "";
            editingMode = gi.getBooleanExtra("editingMode",false);
            updateMode = gi.getBooleanExtra("updatingMode", false);
            if (gi.getStringExtra("flag").equals("G")){
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green_flag));
                status = "greenEvent";
            }
            else if (gi.getStringExtra("flag").equals("O")){
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.orange_flag));
                status = "orangeEvent";
            }
            else if (gi.getStringExtra("flag").equals("R")){
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.red_flag));
                status = "redEvent";
            }
            else{
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.gray_700));
                status = "endEvent";
            }

            if (updateMode) {
                fab.setVisibility(View.VISIBLE);
                editingMode = false;
            } else if (!editingMode){
                fab.setVisibility(View.GONE);
                // make the location intent here
            }
            else{
                fab.setVisibility(View.VISIBLE);
            }


            displayEvent(eventId, status);
        }

        getAllShowsToDisplay();
        getAllMaterialsToDisplay();
        getAllEventsToDisplay();

        allEventsDates = new ArrayList<>();
        newEvent = new Event();

        rootPath = new File(this.getExternalFilesDir("/"), "myPDFS");

        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
    }

    private void displayEvent(String eventId, String status) {
        reflive_Event.child(status).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {

                for (DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    String tempEventId = Objects.requireNonNull(tempEvent).getDateOfEvent();
                    if (eventId.equals(tempEventId)){
                        eventTitleTV.setText(tempEvent.getEventName());
                        nameCustomerET.setText(tempEvent.getCustomerName());
                        emailCustomerET.setText(tempEvent.getCustomerEmail());
                        phoneCustomerET.setText(tempEvent.getCustomerPhone());
                        locationET.setText(tempEvent.getEventLocation());

                        selectedShows = tempEvent.getEventShows();
                        selectedMaterials = tempEvent.getEventEquipments();

                        // Formatting the date to be visualizer
                        String fullDatestr = tempEvent.getDateOfEvent();
                        selectedDate.setYear(Integer.parseInt(fullDatestr.substring(0,4))-1900);
                        fullDatestr = fullDatestr.substring(4);
                        selectedDate.setMonth(Integer.parseInt(fullDatestr.substring(0,2))-1);
                        fullDatestr = fullDatestr.substring(2);
                        selectedDate.setDate(Integer.parseInt(fullDatestr.substring(0,2)));
                        fullDatestr = fullDatestr.substring(2);
                        selectedDate.setHours(Integer.parseInt(fullDatestr.substring(0,2)));
                        fullDatestr = fullDatestr.substring(2);
                        selectedDate.setMinutes(Integer.parseInt(fullDatestr.substring(0,2)));
                        selectedDate.setSeconds(0);
                        selectedDate.setTime(selectedDate.getTime());
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String strDate = dateFormat.format(selectedDate);
                        dateTV.setText(strDate);

                        contentET.setText(tempEvent.getEventContent());
                        totalEmployeeTV.setText(String.valueOf(tempEvent.getEventEmployees()));
                        eventCost = tempEvent.getEventCost();
                        eventCostTV.setText(String.valueOf(eventCost));

                        int pos = 0;
                        for (int i=0; i < paymentSelection.length; i++){
                            if (paymentSelection[i].equals(tempEvent.getEventPayment())){
                                pos = i;
                            }
                        }
                        paymentSpinner.setSelection(pos);

                        if (updateMode){
                            previousEventDate = tempEvent.getDateOfEvent();
                            previousEventName = tempEvent.getEventName();
                            previousEventStatus = tempEvent.getEventCharacterize();
                            selectedMaterials = tempEvent.getEventEquipments();
                            selectedShows = tempEvent.getEventShows();
                            updatedEvent = new Event(tempEvent.getCustomerName(), tempEvent.getCustomerPhone(), tempEvent.getCustomerEmail(), tempEvent.getDateOfEvent(), tempEvent.getDateOfCreation(), tempEvent.getEventName(), tempEvent.getEventLocation(), tempEvent.getEventCost(), tempEvent.getEventInformation(), tempEvent.getEventContent(), tempEvent.getEventCharacterize(), tempEvent.getEventPayment(), tempEvent.getEventEmployees(), tempEvent.getEventEquipments(), tempEvent.getEventMissions(), tempEvent.getEventShows());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // TODO: Continue to reading from the FB
    private void getAllEventsToDisplay() {
        refGreen_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                allEventsDates.clear();

                for (DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    allEventsDates.add(Objects.requireNonNull(tempEvent).getDateOfEvent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        refOrange_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {

                for (DataSnapshot data : dS.getChildren()) {
                    Event tempEvent = data.getValue(Event.class);

                    allEventsDates.add(Objects.requireNonNull(tempEvent).getDateOfEvent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * getAllShowsToDisplay method gets all the Shows objects that were created to display on the TextView objects from the FireBase DataBase.
     *
     */
    private void getAllShowsToDisplay() {
        selectedShows = new ArrayList<>();
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
                    selectedShows.add(false);
                }

                if (editingMode){
                    for (int i = 0; i < showsKeyList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(showsKeyList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);
                        tempChip.setChipIconResource(R.drawable.null1);
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);
                        final boolean[] isToAdd = {true};
                        int index = i;

                        // Adding the onClick method that will add the selected shows to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (isToAdd[0]){
                                    tempChip.setChipIconResource(R.drawable.ic_check);
                                    tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                    isToAdd[0] = false;
                                    selectedShows.set(index, true);
                                    eventCost += showsDataList.get(index);
                                    eventCostTV.setText(String.valueOf(eventCost));
                                }
                                else{
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    isToAdd[0] = true;

                                    if (eventCost > 0){
                                        eventCost -= showsDataList.get(index);
                                        selectedShows.set(index, false);
                                        eventCostTV.setText(String.valueOf(eventCost));
                                    }
                                }
                            }
                        });
                        chipGroupShows.addView(tempChip);
                    }
                }
                else if (updateMode){
                    for (int i = 0; i < showsKeyList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(showsKeyList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);

                        if (selectedShows.get(i)){
                            tempChip.setChipIconResource(R.drawable.ic_check);
                            tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                        } else {
                            tempChip.setChipIconResource(R.drawable.null1);
                        }
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);
                        boolean[] isToAdd = {true};
                        int index = i;

                        // Adding the onClick method that will add the selected shows to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (selectedShows.get(index) && isToAdd[0] && updateMode){
                                    Toast.makeText(newEventActivity.this, "true", Toast.LENGTH_SHORT).show();
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    isToAdd[0] = true;
                                    selectedShows.set(index, false);
                                    if (eventCost > 0){
                                        eventCost -= showsDataList.get(index);
                                        eventCostTV.setText(String.valueOf(eventCost));
                                    }
                                }
                                else if (isToAdd[0]){
                                    tempChip.setChipIconResource(R.drawable.ic_check);
                                    tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                    isToAdd[0] = false;
                                    selectedShows.set(index, true);
                                    eventCost += showsDataList.get(index);
                                    eventCostTV.setText(String.valueOf(eventCost));
                                }
                                else{
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    isToAdd[0] = true;
                                    selectedShows.set(index, false);
                                    if (eventCost > 0){
                                        eventCost -= showsDataList.get(index);
                                        eventCostTV.setText(String.valueOf(eventCost));
                                    }
                                }
                            }
                        });
                        chipGroupShows.addView(tempChip);
                    }
                }
                else {
                    for (int i = 0; i < showsKeyList.size(); i++) {
                        if (selectedShows.get(i)){
                            Chip tempChip = new Chip(newEventActivity.this);
                            tempChip.setText(showsKeyList.get(i));
                            tempChip.setTextAppearance(R.style.ChipTextAppearance);
                            tempChip.setChipIconResource(R.drawable.ic_check);
                            tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                            tempChip.setChipBackgroundColorResource(R.color.brown_200);
                            chipGroupShows.addView(tempChip);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getMaterialAmountAD( int index) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("כמה לקחת מ"+materialsKeyList.get(index));
        final EditText amountET = new EditText(this);
        amountET.setInputType(InputType.TYPE_CLASS_NUMBER);
        int maxLength = 6;
        amountET.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        adb.setView(amountET);

        amountET.setText(materialsUsedList.get(index));
        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                amountOfUsedMaterial = -1;
                dialogInterface.dismiss();
            }
        });

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                amountOfUsedMaterial = Integer.parseInt(amountET.getText().toString());
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * getAllMaterialsToDisplay method gets all the Materials objects that were created to display on the TextView objects from the FireBase DataBase.
     *
     */
    private void getAllMaterialsToDisplay() {
        selectedMaterials = new ArrayList<>();
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
                    selectedMaterials.add(false);
                }

                if (editingMode){
                    for (int i = 0; i < materialsKeyList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(materialsKeyList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);
                        tempChip.setChipIconResource(R.drawable.null1);
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);
                        boolean[] isToAdd = {true};
                        int index = i;

                        // Adding the onClick method that will add the selected materials to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (isToAdd[0] && editingMode){
                                    getMaterialAmountAD(index);
                                    Toast.makeText(newEventActivity.this, amountOfUsedMaterial+"", Toast.LENGTH_SHORT).show();
                                    // Checks if the user selected a real amount of the CURRENT material
                                    if (amountOfUsedMaterial > 0 && amountOfUsedMaterial < (materialsDataList.get(index) - Integer.parseInt(materialsUsedList.get(index)))) {
                                        tempChip.setChipIconResource(R.drawable.ic_check);
                                        tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                        isToAdd[0] = false;
                                        Material tempMaterial = new Material(materialsKeyList.get(index), materialsDataList.get(index), amountOfUsedMaterial);
                                        allMaterials.set(index, tempMaterial);
                                        materialsUsedList.set(index, String.valueOf(amountOfUsedMaterial));
                                        selectedMaterials.set(index, true);
                                        amountOfUsedMaterial = -1;
                                    }
                                }
                                else if (editingMode){
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    selectedMaterials.set(index, false);
                                    isToAdd[0] = true;
                                }
                            }
                        });

                        chipGroupMaterials.addView(tempChip);
                    }
                }
                else if (updateMode){
                    for (int i = 0; i < materialsKeyList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(materialsKeyList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);
                        if (selectedMaterials.get(i)){
                            tempChip.setChipIconResource(R.drawable.ic_check);
                            tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                        } else {
                            tempChip.setChipIconResource(R.drawable.null1);

                        }
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);

                        boolean[] isToAdd = {true};
                        int index = i;

                        // Adding the onClick method that will add the selected materials to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (selectedMaterials.get(index) && isToAdd[0] && updateMode) {
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    isToAdd[0] = true;
                                    selectedMaterials.set(index, false);
                                }
                                else if (isToAdd[0]){
                                    // Adding a MATERIAL must be with the amount of it that will be used.
                                    getMaterialAmountAD(index);
                                    // Checks if the user selected a real amount of the CURRENT material
                                    if (amountOfUsedMaterial > 0 && amountOfUsedMaterial > (materialsDataList.get(index) - Integer.parseInt(materialsUsedList.get(index)))){
                                        tempChip.setChipIconResource(R.drawable.ic_check);
                                        tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                        isToAdd[0] = false;
                                        Material tempMaterial  = new Material(materialsKeyList.get(index),materialsDataList.get(index), amountOfUsedMaterial);
                                        allMaterials.set(index, tempMaterial);
                                        materialsUsedList.set(index, String.valueOf(amountOfUsedMaterial));
                                        selectedMaterials.set(index, true);
                                        amountOfUsedMaterial = -1;
                                    }
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
                else {
                    for (int i = 0; i < materialsKeyList.size(); i++) {
                        if (selectedMaterials.get(i)){
                            Chip tempChip = new Chip(newEventActivity.this);
                            tempChip.setText(materialsKeyList.get(i));
                            tempChip.setTextAppearance(R.style.ChipTextAppearance);
                            tempChip.setChipIconResource(R.drawable.ic_check);
                            tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                            tempChip.setChipBackgroundColorResource(R.color.brown_200);
                            chipGroupMaterials.addView(tempChip);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void changeTitleEvent() {
        if (editingMode || updateMode){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("כותרת האירוע");
            final EditText titleET = new EditText(this);
            // Limiting the length of the Title String
            // TODO
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
                    dialogInterface.dismiss();
                    if (newTitle.isEmpty()){
                        Snackbar.make(layout,"כתורת אירוע לא תהיה ריקה",3000).show();
                    } else eventTitleTV.setText(newTitle);
                }
            });

            AlertDialog ad = adb.create();
            ad.show();
        } else{
            Snackbar.make(layout,"לא ניתן במצב צפייה", 3000).show();
        }
    }

    public void checkIfToExit(View view) {
        if (editingMode || updateMode){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            TextView titleTV = new TextView(this);
            titleTV.setText("היציאה לא תשמור");
            titleTV.setGravity(Gravity.RIGHT);
            titleTV.setTextSize(18);
            adb.setCustomTitle(titleTV);

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
        else{
            moveToPreviousAct();
        }
    }

    private void moveToPreviousAct() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (editingMode || updateMode){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            TextView titleTV = new TextView(this);
            titleTV.setText("היציאה לא תשמור");
            titleTV.setGravity(Gravity.RIGHT);
            titleTV.setTextSize(18);
            adb.setCustomTitle(titleTV);

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
        else {
            super.onBackPressed();
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
        if (editingMode || updateMode){
            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month++, dayOfMonth,0,0,0);
                    selectedDate = new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),0,0,0);
                    openTimePicker();
                }
            }, year, month, day);

            dpd.show();
        }
        else{
            Snackbar.make(layout,"לא ניתן במצב צפייה", 3000).show();
        }
    }

    private void openTimePicker() {
        Calendar cldr = Calendar.getInstance();
        final int hour = cldr.get(Calendar.HOUR_OF_DAY);
        final int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        TimePickerDialog picker = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker tp, int hour, int minute) {
                        //Calendar c = Calendar.getInstance();
                        //c.set(selectedDate.getYear(), selectedDate.getMonth(),selectedDate.getDay(), hour, minute);
                        selectedDate.setYear(selectedDate.getYear() - 1900);
                        selectedDate.setHours(hour);
                        selectedDate.setMinutes(minute);
                        selectedDate.setSeconds(0);
                        if (selectedDate.getDate() == new Date().getDate() && selectedDate.getMonth() == new Date().getMonth() && selectedDate.getYear() == new Date().getYear()){
                            Snackbar.make(layout,"תאריך לא רלוונטי", 3000).show();
                        }
                        else if (selectedDate.after(new Date())){
                            selectedDate.setTime(selectedDate.getTime());
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            String strDate = dateFormat.format(selectedDate);
                            dateTV.setText(strDate);
                        }
                        else{
                            Snackbar.make(layout,"תאריך לא רלוונטי", 3000).show();
                        }
                    }
                }, hour, minutes, true);
        picker.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        userSelection = paymentSelection[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void createNewEvent(View view) {
        currentDate = new Date();
        if (checkEvent() && !allEventsDates.contains(eventStrDate)){
            // Creates the event as a constructor

            if (userSelection.equals(paymentSelection[0])){
                // TODO: fixing the ERROR
                //openADCheckPayment(false);
            }
            else {
                newEvent.setEventCharacterize("G");
            }

            newEvent.setEventName(eventTitleTV.getText().toString());
            newEvent.setCustomerName(customerName);
            newEvent.setCustomerPhone(customerPhone);
            newEvent.setCustomerEmail(customerEmail);
            newEvent.setEventLocation(eventLocation);

            newEvent.setDateOfEvent(eventStrDate);
            // Casting the Date to String
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String strDate = dateFormat.format(currentDate);
            newEvent.setDateOfCreation(strDate);

            newEvent.setEventContent(eventContent);
            newEvent.setEventCost(eventCost);
            //newEvent.setEventEmployees();
            newEvent.setEventPayment(userSelection);
            newEvent.setEventShows(selectedShows);
            newEvent.setEventEquipments(selectedMaterials);


            File eventFile = creatingFile(newEvent);
            uploadFileToFB(eventFile, newEvent);

            String childID = newEvent.getDateOfEvent();
            if (newEvent.getEventCharacterize().equals("G")){
                refGreen_Event.child(childID).setValue(newEvent);
            } else{
                refOrange_Event.child(childID).setValue(newEvent);
            }
            sendingFileToEmail(eventFile, newEvent);
            Snackbar.make(layout,eventTitleTV.getText().toString()+" נוצר בהצלחה", 3000).show();
            clearAllFields();
        }
        else if (allEventsDates.contains(eventStrDate) && editingMode){
            Snackbar.make(layout,"תאריך תפוס", 3000).show();
        }
        else if (checkEvent() && updateMode){
            // Removing the previous event
            String previousEventPath = previousEventDate+previousEventName+".pdf";
            removeEventFile(previousEventPath);
            if (previousEventStatus.equals("G")){
                refGreen_Event.child(previousEventDate).removeValue();
            } else{
                refOrange_Event.child(previousEventDate).removeValue();
            }

            // Redoing the all the process again with the changes
            updatedEvent.setEventName(eventTitleTV.getText().toString());
            updatedEvent.setCustomerName(customerName);
            updatedEvent.setCustomerPhone(customerPhone);
            updatedEvent.setCustomerEmail(customerEmail);
            updatedEvent.setEventLocation(eventLocation);

            updatedEvent.setDateOfEvent(eventStrDate);
            // Casting the Date to String
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String strDate = dateFormat.format(currentDate);
            updatedEvent.setDateOfCreation(strDate);

            updatedEvent.setEventContent(eventContent);
            updatedEvent.setEventCost(eventCost);
            //newEvent.setEventEmployees();
            updatedEvent.setEventPayment(userSelection);
            updatedEvent.setEventShows(Lists.newArrayList(selectedShows));
            updatedEvent.setEventEquipments(Lists.newArrayList(selectedMaterials));

            if (userSelection.equals(paymentSelection[0])) openADCheckPayment(true);
            else  updatedEvent.setEventCharacterize("G");

            File eventFile = creatingFile(updatedEvent);
            uploadFileToFB(eventFile, updatedEvent);

            if (updatedEvent.getEventCharacterize().equals("G")){
                refGreen_Event.child(updatedEvent.getDateOfEvent()).setValue(updatedEvent);
            } else{
                refOrange_Event.child(updatedEvent.getDateOfEvent()).setValue(updatedEvent);
            }
            sendingFileToEmail(eventFile, updatedEvent);
            Snackbar.make(layout,"התעדכן בהצלחה", 3000).show();
            updateMode = false;
        }
    }

    private void clearAllFields() {
        newEvent = null;
        updatedEvent = null;
        eventTitleTV.setText("כותרת אירוע");
        nameCustomerET.setText("");
        emailCustomerET.setText("");
        phoneCustomerET.setText("");
        locationET.setText("");

        selectedDate = null;
        dateTV.setText("dd/mm/yyyy hh:mm");
        contentET.setText("");
        eventCost = 0;
        eventCostTV.setText(eventCost+"");
        paymentSpinner.setSelection(0); // Return to the default selection

        for (int i = 0; i < selectedShows.size(); i++) {
            if (selectedShows.get(i)){
                selectedShows.set(i, false);
            }
        }

        for (int i = 0; i < selectedMaterials.size(); i++) {
            if (selectedMaterials.get(i)){
                selectedMaterials.set(i, false);
            }
        }
    }

    private void removeEventFile(String previousEventPath) {
        File fileDir = new File(rootPath + "/"+previousEventPath);
        fileDir.delete();
        StorageReference deleteFile = storageRef.child("files/"+previousEventPath);
        deleteFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void uploadFileToFB(File eventFile, Event event) {
        ProgressDialog progressDialog = new ProgressDialog(newEventActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

//        final TextView title = new TextView(this);
//        title.setText("מבצע שמירה בענן ל"+newEvent.getEventName());
//        title.setTextSize(20);
//        title.setPadding(0,15,30,15);
//        title.setTextColor(Color.GRAY);
//        title.setTypeface(ResourcesCompat.getFont(title.getContext(), R.font.rubik_semibold));

//        progressDialog.setCustomTitle(title);
        progressDialog.setProgress(0);
        progressDialog.setMessage("שומר בענן "+event.getEventName());
        progressDialog.setCancelable(false);


        Uri file = Uri.fromFile(new File(eventFile.getPath()));
        StorageReference riversRef = storageRef.child("files/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.setCancelable(true);
                progressDialog.dismiss();
                Snackbar.make(layout,"שמירה נכשלה", 3000).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) {
                    progressDialog.setCancelable(true);
                    progressDialog.dismiss();
                    Snackbar.make(layout,"שמירה בוצעה בהצלחה", 3000).show();
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int currentProgress = (int) (100*(snapshot.getBytesTransferred()/snapshot.getTotalByteCount()));
                progressDialog.setProgress(currentProgress);
                progressDialog.show();
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void sendingFileToEmail(File fileToSend, Event event) {
        Uri uri = Uri.parse("mailto:" + "shahryani96@gmail.com").buildUpon()
                .appendQueryParameter("to", event.getCustomerEmail())
                .appendQueryParameter("subject", "סיכום יצירת אירוע: "+event.getEventName())
                .appendQueryParameter("body", "להלן קובץ אישור העסקה: ")
                .build();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Uri path = Uri.fromFile(new File(fileToSend.getAbsolutePath()));
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);

        try {
            startActivity(emailIntent);
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private File creatingFile(Event event) {
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        // Size of the document
        int width, height;
        width = 1200;
        height = 2010;

        // Setting the properties of the PDF document
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(width, height,1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo1);
        Canvas canvas = page1.getCanvas();

        // START OD DOCUMENT
        Bitmap headBM = BitmapFactory.decodeResource(getResources(), R.drawable.pdf_head);
        Bitmap scaledbmp1 = Bitmap.createScaledBitmap(headBM,width,height/5,false);
        canvas.drawBitmap(scaledbmp1, 0,0,paint);

        titlePaint.setTextAlign(Paint.Align.RIGHT);
        titlePaint.setTextSize(15);
        paint.setTypeface(Typeface.create("Calibri",Typeface.NORMAL));
        canvas.drawText( ""+event.getCustomerName(), (float) (width*0.7), 320, titlePaint);
        canvas.drawText(""+event.getCustomerPhone(),(float) (width*0.7), 350, titlePaint);
        canvas.drawText(""+event.getCustomerEmail(),(float) (width*0.7), 380, titlePaint);


        Bitmap priceBM = BitmapFactory.decodeResource(getResources(), R.drawable.pdf_prize);
        Bitmap scaledbmp2 = Bitmap.createScaledBitmap(priceBM,858,45,false);
        canvas.drawBitmap(scaledbmp2, (width-858)/2,height/5+100,paint);

        Paint prizePaint = new Paint();
        prizePaint.setTextAlign(Paint.Align.CENTER);
        prizePaint.setTextSize(15);
        paint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
        canvas.drawText( ""+event.getEventCost(), (float) (width*0.6), (float) (height/5+122.5), prizePaint);
        // Parsing the String of the date to a format String
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);
        Date tempSelectedDate  = null;
        try {
            tempSelectedDate = format.parse(event.getDateOfCreation());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(Objects.requireNonNull(tempSelectedDate));
        canvas.drawText(""+strDate,(float) (width*0.43), (float) (height/5+122.5), prizePaint);

        // END OF DOCUMENT
        Bitmap endBM = BitmapFactory.decodeResource(getResources(), R.drawable.pdf_end);
        Bitmap scaledbmp3 = Bitmap.createScaledBitmap(endBM,1191,618,false);
        canvas.drawBitmap(scaledbmp3, (width-1191)/2,height-618,paint);
        // Printing the total price after taxes
        canvas.drawText(""+ (int) Math.floor(eventCost*1.17), (float) (width*0.25), height-590, prizePaint);

        pdfDocument.finishPage(page1);

        File dataFile = new File(rootPath, event.getDateOfEvent()+event.getEventName()+".pdf");;

        try {
            pdfDocument.writeTo(new FileOutputStream(dataFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();

        return dataFile;
    }

    private void openADCheckPayment(boolean toUpdate) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("תשלום לפני שמסיימים..");
        titleTV.setGravity(Gravity.RIGHT);
        titleTV.setTextSize(20);
        adb.setCustomTitle(titleTV);

        adb.setNegativeButton("אוקיי", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (toUpdate){
                    updatedEvent.setEventCharacterize("G");
                } else {
                    newEvent.setEventCharacterize("G");
                }
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green_flag));
                dialogInterface.dismiss();
            }
        });

        adb.setPositiveButton("לא עכשיו", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (toUpdate){
                    updatedEvent.setEventCharacterize("O");
                } else {
                    newEvent.setEventCharacterize("O");
                }
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.orange_flag));
                dialogInterface.dismiss();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    private boolean checkEvent() {
        boolean flag = true;
        customerName = nameCustomerET.getText().toString();
        customerEmail = emailCustomerET.getText().toString();
        customerPhone = phoneCustomerET.getText().toString();
        eventLocation = locationET.getText().toString();
        eventContent = contentET.getText().toString();

        if (selectedDate != null){
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            eventStrDate = dateFormat.format(selectedDate);
            selectedDate = null;
        } else if (!dateTV.getText().toString().equals("dd/mm/yyyy hh:mm")){
            String tempDate = dateTV.getText().toString();
            tempDate = tempDate.replaceAll("/","");
            tempDate = tempDate.replaceAll(":","");
            tempDate = tempDate.replaceAll(" ","");

            String day = tempDate.substring(0,2);
            String month = tempDate.substring(2,4);
            String year = tempDate.substring(4,8);
            String hour = tempDate.substring(8,10);
            String minute = tempDate.substring(10);

            eventStrDate = year+""+month+""+day+""+hour+""+minute;
        }
        else{
            flag = false;
            Snackbar.make(layout, "נא לבחור תאריך", 3000).show();
        }

        if (eventTitleTV.getText().toString().equals("כותרת האירוע")){
            flag = false;
            Snackbar.make(layout, "כותרת אירוע לא תהיה ריקה", 3000).show();
        }
        else if (eventTitleTV.getText().toString().isEmpty() || customerName.isEmpty() || customerEmail.isEmpty() || customerPhone.isEmpty() || eventLocation.isEmpty() || eventContent.isEmpty()){
            flag = false;
            Snackbar.make(layout, "שדה לא יהיה ריק", 3000).show();
        }

        // Checks if the email is valid and verified
        Matcher matcher =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(customerEmail);
        if (!matcher.find()){
            flag = false;
            Snackbar.make(layout, "כתובת אימייל לא חוקית", 3000).show();
        }

        if (customerPhone.length() < 9 || customerPhone.length() > 10){
            flag = false;
            Snackbar.make(layout, "מספר הטלפון לא חוקי", 3000).show();
        }
        else if(customerPhone.length() == 10){
            if (!customerPhone.startsWith("05") || customerPhone.contains("#")){
                flag = false;
                Snackbar.make(layout, "מספר הטלפון לא חוקי", 3000).show();
            }
        }
        else if (customerPhone.length() == 9){
            if (!customerPhone.startsWith("0")){
                flag = false;
                Snackbar.make(layout, "מספר הטלפון לא חוקי", 3000).show();
            }
        }

        else if (!(Lists.newArrayList(selectedShows)).contains(true)){
            flag = false;
            Snackbar.make(layout,"נא לבחור ציוד לאירוע", 3000).show();
        }
        else if (!(Lists.newArrayList(selectedShows)).contains(true)){
            flag = false;
            Snackbar.make(layout,"נא לבחור מופע/ים לאירוע", 3000).show();
        }

        return flag && checkLocation();
    }

    @SuppressLint("MissingPermission")
    private boolean checkLocation() {
        boolean[] locationFlag = {true};
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(newEventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(newEventActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(newEventActivity.this);
                        try {
                            List<Address> addressList = geocoder.getFromLocationName(eventLocation, 6);
                        } catch (Exception e) {
                            locationFlag[0] = false;
                            Snackbar.make(layout, "אין כתובת כזו", 3000).show();
                        }
                    }
                }
            });
        } else {
            locationFlag[0] = false;
            Snackbar.make(layout, "אין הרשאות מיקום", 3000).show();
        }
        return locationFlag[0];
    }
}