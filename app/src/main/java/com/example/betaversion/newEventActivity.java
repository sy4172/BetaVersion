package com.example.betaversion;

import static com.example.betaversion.FBref.refBusinessEqu;
import static com.example.betaversion.FBref.refGreen_Event;
import static com.example.betaversion.FBref.refOrange_Event;
import static com.example.betaversion.FBref.refRed_Event;
import static com.example.betaversion.FBref.reflive_Event;
import static com.example.betaversion.FBref.storageRef;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * * @author    Shahar Yani
 * * @version  	7.2
 * * @since		30/12/2021
 * <p>
 * * This newEventActivity.class displays and creates all the events of the customers.
 */
public class newEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    LinearLayout layout;
    TextView eventTitleTV, dateTV, totalEmployeeTV, eventCostTV;
    ImageView flag, hasAcceptedButton, isPaidButton;
    ChipGroup chipGroupShows, chipGroupMaterials;
    EditText nameCustomerET, emailCustomerET, phoneCustomerET, locationET, contentET;
    FloatingActionButton fab;

    String customerName, customerPhone, customerEmail, eventLocation, eventContent, userSelection, eventStrDate;
    int eventCost, amountOfUsedMaterial, eventEmployee;
    Date selectedDate, currentDate;
    boolean editingMode, updateMode, hasAccepted, isPaid;
    String previousEventDate, previousEventName, previousEventStatus;

    ArrayList<Shows> allShows;
    ArrayList<Material> allMaterials;
    ArrayList<Boolean> selectedShows;
    ArrayList<String> selectedMaterials, showTitlesList, showsDesList, materialsKeyList, allEventsDates;
    ArrayList<Integer> materialsUsedList, showsCostsList, materialsDataList, showsEmployeesList;

    Spinner paymentSpinner;
    boolean[] isToAdd;
    String [] paymentSelection; // Need to check according to Yakkov request

    Event newEvent, updatedEvent;
    File rootPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        layout = findViewById(R.id.missionlayout);
        eventTitleTV = findViewById(R.id.eventTitleTV);
        flag = findViewById(R.id.flag);
        isPaidButton = findViewById(R.id.isPaidButton);
        hasAcceptedButton = findViewById(R.id.hasAcceptedButton);
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

        paymentSelection = new String[]{"בחר אמצעי תשלום","שוטף +30","שוטף +60","שוטף +90","מזומן","צ'ק"};
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
        DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.orange_flag));

        allShows = new ArrayList<>();
        showTitlesList = new ArrayList<>();
        showsCostsList = new ArrayList<>();
        showsDesList = new ArrayList<>();
        showsEmployeesList = new ArrayList<>();

        allMaterials = new ArrayList<>();
        materialsKeyList = new ArrayList<>();
        materialsDataList = new ArrayList<>();
        materialsUsedList = new ArrayList<>();

        editingMode = true;
        isPaid = hasAccepted = updateMode = false;
        isToAdd = new boolean[]{true};
        amountOfUsedMaterial = 0;
        eventEmployee = 0;
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
                isPaidButton.setClickable(true);
                hasAcceptedButton.setClickable(true);
                editingMode = false;
            } else if (!editingMode){ // View mode
                fab.setVisibility(View.GONE);
                isPaidButton.setClickable(false);
                hasAcceptedButton.setClickable(false);
                nameCustomerET.setEnabled(false);
                emailCustomerET.setEnabled(false);
                phoneCustomerET.setEnabled(false);
                locationET.setEnabled(false);
                locationET.setEnabled(false);
                contentET.setEnabled(false);
                // make the location intent here
            }
            else{ // Editing mode
                fab.setVisibility(View.VISIBLE);
                isPaidButton.setClickable(true);
                hasAcceptedButton.setClickable(true);
            }

            displayEvent(eventId, status);
        }

        getAllShowsToDisplay();
        getAllMaterialsToDisplay();

        allEventsDates = new ArrayList<>();
        newEvent = new Event();

        rootPath = new File(this.getExternalFilesDir("/"), "myPDFS");

        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
    }

    // Display the event according its status and id
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
                        if (tempEvent.isHasAccepted()){
                            DrawableCompat.setTint(hasAcceptedButton.getDrawable(), ContextCompat.getColor(hasAcceptedButton.getContext(), R.color.green_flag));
                        }
                        if (tempEvent.isPaid()){
                            DrawableCompat.setTint(isPaidButton.getDrawable(), ContextCompat.getColor(isPaidButton.getContext(), R.color.green_flag));
                        }

                        if (updateMode){
                            previousEventDate = tempEvent.getDateOfEvent();
                            previousEventName = tempEvent.getEventName();
                            previousEventStatus = tempEvent.getEventCharacterize();
                            selectedMaterials = tempEvent.getEventEquipments();
                            selectedShows = tempEvent.getEventShows();
                            hasAccepted = tempEvent.isHasAccepted();
                            isPaid = tempEvent.isPaid();
                            updatedEvent = new Event(tempEvent.getCustomerName(), tempEvent.getCustomerPhone(), tempEvent.getCustomerEmail(), tempEvent.getDateOfEvent(), tempEvent.getDateOfCreation(), tempEvent.getEventName(), tempEvent.getEventLocation(), tempEvent.getEventCost(), tempEvent.getEventContent(), tempEvent.getEventCharacterize(), tempEvent.getEventPayment(), tempEvent.getEventEmployees(), tempEvent.getEventEquipments(), tempEvent.getEventMissions(), tempEvent.getEventShows(),tempEvent.isPaid(), tempEvent.isHasAccepted());
                        }
                    }
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
                showTitlesList.clear();
                showsCostsList.clear();
                showsDesList.clear();
                showsEmployeesList.clear();
                allShows.clear();

                for (DataSnapshot data : dS.getChildren()) {
                    Shows tempShow = data.getValue(Shows.class);
                    allShows.add(tempShow);

                    showTitlesList.add(Objects.requireNonNull(tempShow).getShowTitle());
                    showsCostsList.add(tempShow.getCost());
                    showsDesList.add(tempShow.getDescription());
                    showsEmployeesList.add(tempShow.getEmployees());
                    selectedShows.add(false);
                }

                if (editingMode){
                    boolean[] isToAdd = new boolean[showTitlesList.size()];
                    for (int i = 0; i < showTitlesList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(showTitlesList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);
                        tempChip.setChipIconResource(R.drawable.null1);
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);
                        isToAdd[i] = true;
                        int index = i;

                        // Adding the onClick method that will add the selected shows to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (isToAdd[index]){
                                    tempChip.setChipIconResource(R.drawable.ic_check);
                                    tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                    isToAdd[index] = false;
                                    selectedShows.set(index, true);
                                    eventCost += showsCostsList.get(index);
                                    eventEmployee += showsEmployeesList.get(index);
                                    totalEmployeeTV.setText(String.valueOf(eventEmployee));
                                    eventCostTV.setText(String.valueOf(eventCost));
                                }
                                else{
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    isToAdd[index] = true;

                                    if (eventCost > 0){
                                        eventCost -= showsCostsList.get(index);
                                        eventEmployee -= showsEmployeesList.get(index);
                                        totalEmployeeTV.setText(String.valueOf(eventEmployee));
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
                    boolean[] isToAdd = new boolean[showTitlesList.size()];
                    for (int i = 0; i < showTitlesList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(showTitlesList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);

                        if (selectedShows.get(i)){
                            tempChip.setChipIconResource(R.drawable.ic_check);
                            tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                        } else {
                            tempChip.setChipIconResource(R.drawable.null1);
                        }
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);
                        isToAdd[i] = true;
                        int index = i;

                        // Adding the onClick method that will add the selected shows to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (selectedShows.get(index) && isToAdd[index] && updateMode){
                                    Toast.makeText(newEventActivity.this, "true", Toast.LENGTH_SHORT).show();
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    isToAdd[index] = true;
                                    selectedShows.set(index, false);
                                    if (eventCost > 0){
                                        eventCost -= showsCostsList.get(index);
                                        eventCostTV.setText(String.valueOf(eventCost));
                                    }
                                }
                                else if (isToAdd[index]){
                                    tempChip.setChipIconResource(R.drawable.ic_check);
                                    tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                                    isToAdd[index] = false;
                                    selectedShows.set(index, true);
                                    eventCost += showsCostsList.get(index);
                                    eventCostTV.setText(String.valueOf(eventCost));
                                }
                                else{
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    isToAdd[index] = true;
                                    selectedShows.set(index, false);
                                    if (eventCost > 0){
                                        eventCost -= showsCostsList.get(index);
                                        eventCostTV.setText(String.valueOf(eventCost));
                                    }
                                }
                            }
                        });
                        chipGroupShows.addView(tempChip);
                    }
                }
                else {
                    for (int i = 0; i < showTitlesList.size(); i++) {
                        if (selectedShows.get(i)){
                            Chip tempChip = new Chip(newEventActivity.this);
                            tempChip.setText(showTitlesList.get(i));
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

    private void getMaterialAmountAD(int index, Chip tempCh, boolean [] isToAdd) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AdScreen.setPadding(15,0,15,0);
        final TextView titleTV = new TextView(this);
        titleTV.setText("כמה לקחת מ"+materialsKeyList.get(index));
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);
        final TextView totalLeftTV = new TextView(this);
        totalLeftTV.setText("סה''כ נשאר "+(materialsDataList.get(index) - materialsUsedList.get(index)));
        totalLeftTV.setTextColor(Color.rgb(0, 0, 0));
        totalLeftTV.setTextSize(18);
        totalLeftTV.setPadding(0,15,30,15);
        totalLeftTV.setTypeface(ResourcesCompat.getFont(totalLeftTV.getContext(), R.font.rubik_semibold));
        AdScreen.addView(totalLeftTV);
        final EditText amountET = new EditText(this);
        amountET.setInputType(InputType.TYPE_CLASS_NUMBER);
        int maxLength = 6;
        amountET.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        amountET.setTypeface(ResourcesCompat.getFont(amountET.getContext(), R.font.rubik_semibold));
        AdScreen.addView(amountET);
        adb.setView(AdScreen);
        amountET.setText(""+0);
        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                amountOfUsedMaterial = 0;
                dialogInterface.dismiss();
            }
        });

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                amountOfUsedMaterial = Integer.parseInt(amountET.getText().toString());
                if (isToAdd[index]){
                    // Checks if the user selected a real amount of the CURRENT material
                    if (amountOfUsedMaterial > 0 && amountOfUsedMaterial < (materialsDataList.get(index) - materialsUsedList.get(index))) {
                        tempCh.setChipIconResource(R.drawable.ic_check);
                        tempCh.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                        isToAdd[index] = false;
                        // Updating the properties of the CURRENT Material to the list
                        Material tempMaterial = new Material(materialsKeyList.get(index), materialsDataList.get(index), amountOfUsedMaterial);
                        allMaterials.set(index, tempMaterial);
                        materialsUsedList.set(index, amountOfUsedMaterial);
                        selectedMaterials.set(index, "t_"+amountET.getText().toString());
                        amountOfUsedMaterial = 0;
                    }
                }
                else{
                    tempCh.setChipIconResource(R.drawable.null1);
                    selectedMaterials.set(index, "f_0");
                    isToAdd[index] = true;
                }
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
                    materialsUsedList.add(temp.getUsedAmount());
                    selectedMaterials.add("f_0");
                }

                if (editingMode){
                    boolean[] isToAdd = new boolean[materialsKeyList.size()];
                    for (int i = 0; i < materialsKeyList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(materialsKeyList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);
                        tempChip.setChipIconResource(R.drawable.null1);
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);
                        int index = i;
                        isToAdd[i] = true;
                        // Adding the onClick method that will add the selected materials to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (editingMode && !isToAdd[index]){
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        selectedMaterials.set(index, "f_0");
                                    }
                                    isToAdd[index] = true;
                                } else getMaterialAmountAD(index, tempChip, isToAdd);
                            }
                        });

                        chipGroupMaterials.addView(tempChip);
                    }
                }
                else if (updateMode){
                    boolean[] isToAdd = new boolean[materialsKeyList.size()];
                    for (int i = 0; i < materialsKeyList.size(); i++) {
                        Chip tempChip = new Chip(newEventActivity.this);
                        tempChip.setText(materialsKeyList.get(i));
                        tempChip.setTextAppearance(R.style.ChipTextAppearance);
                        if (Integer.parseInt(selectedMaterials.get(i).substring(2)) > 0){
                            tempChip.setChipIconResource(R.drawable.ic_check);
                            tempChip.setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                        } else {
                            tempChip.setChipIconResource(R.drawable.null1);

                        }
                        tempChip.setChipBackgroundColorResource(R.color.brown_200);
                        isToAdd[i] = true;
                        int index = i;

                        // Adding the onClick method that will add the selected materials to the Event constructor

                        tempChip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // the variable isToAdd became as an array due to the fact that it can't possible to use a general variable in an anonymous method
                                if (selectedMaterials.get(index).subSequence(0,1).equals("f") && isToAdd[index] && updateMode) {
                                    getMaterialAmountAD(index, tempChip, isToAdd);
                                }
                                else{
                                    tempChip.setChipIconResource(R.drawable.null1);
                                    selectedMaterials.set(index, "f_0");
                                    isToAdd[index] = true;
                                }
                            }
                        });
                        chipGroupMaterials.addView(tempChip);
                    }
                }
                else {
                    for (int i = 0; i < materialsKeyList.size(); i++) {
                        if (String.valueOf(selectedMaterials.get(i).subSequence(0, 1)).equals("t")){
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
            final TextView titleTV = new TextView(this);
            titleTV.setText("כותרת האירוע");
            titleTV.setTextColor(Color.rgb(143, 90, 31));
            titleTV.setTextSize(25);
            titleTV.setPadding(0,15,30,15);
            titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
            adb.setCustomTitle(titleTV);
            final EditText titleET = new EditText(this);
            titleET.setFilters(new InputFilter[] {new InputFilter.LengthFilter(14)});
            adb.setView(titleET);

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

    /**
     * Check if to exit.
     *
     * @param view the view
     */
    public void checkIfToExit(View view) {
        if (editingMode || updateMode &&(newEvent != null || updatedEvent != null)){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            final TextView titleTV = new TextView(this);
            titleTV.setText("יציאה מהזנת אירוע");
            titleTV.setTextColor(Color.rgb(143, 90, 31));
            titleTV.setTextSize(25);
            titleTV.setPadding(0,15,30,15);
            titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
            adb.setCustomTitle(titleTV);

            final TextView messageTV = new TextView(this);
            messageTV.setText("היציאה לא תשמור את אשר נעשה");
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

    /**
     * Edit employees.
     *
     * @param view the view
     */
    public void editEmployees(View view) {
        if (editingMode || updateMode){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            TextView titleTV = new TextView(this);
            titleTV.setText("עובדים");
            titleTV.setTextColor(Color.rgb(143, 90, 31));
            titleTV.setTextSize(25);
            titleTV.setPadding(0,15,30,15);
            titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
            adb.setCustomTitle(titleTV);
            final EditText employeeET = new EditText(this);
            adb.setView(employeeET);
            employeeET.setText(totalEmployeeTV.getText().toString());

            adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            adb.setPositiveButton("שמור", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    eventEmployee = Integer.parseInt(employeeET.getText().toString());
                    totalEmployeeTV.setText(employeeET.getText().toString());
                }
            });

            AlertDialog ad = adb.create();
            ad.show();
        } else Snackbar.make(layout, "לא ניתן במצב צפייה", 3000).show();
    }

    /**
     * Edit prize.
     *
     * @param view the view
     */
    public void editPrize(View view) {
        if (editingMode || updateMode){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            TextView titleTV = new TextView(this);
            titleTV.setText("מחיר");
            titleTV.setTextColor(Color.rgb(143, 90, 31));
            titleTV.setTextSize(25);
            titleTV.setPadding(0,15,30,15);
            titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
            adb.setCustomTitle(titleTV);
            final EditText priceET = new EditText(this);
            adb.setView(priceET);
            priceET.setText(eventCostTV.getText().toString());

            adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    eventCost = 0;
                }
            });

            adb.setPositiveButton("שמור", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    eventCost = Integer.parseInt(priceET.getText().toString());
                    eventCostTV.setText(priceET.getText().toString());
                }
            });

            AlertDialog ad = adb.create();
            ad.show();
        } else Snackbar.make(layout, "לא ניתן במצב צפייה", 3000).show();
    }

    private void moveToPreviousAct() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if ((editingMode || updateMode) && (newEvent != null || updatedEvent != null)){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            final TextView titleTV = new TextView(this);
            titleTV.setText("יציאה מהזנת אירוע");
            titleTV.setTextColor(Color.rgb(143, 90, 31));
            titleTV.setTextSize(25);
            titleTV.setPadding(0,15,30,15);
            titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
            adb.setCustomTitle(titleTV);

            final TextView messageTV = new TextView(this);
            messageTV.setText("היציאה לא תשמור את אשר נעשה");
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
                Intent si = new Intent(newEventActivity.this, LoginActivity.class);

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

    /**
     * Open date picker.
     *
     * @param view the view
     */
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
                            String strDate = DateConvertor.dateToString(selectedDate, "dd/MM/yyyy HH:mm");
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

    /**
     * Create new event.
     *
     * @param view the view
     */
    public void createNewEvent(View view) {
        currentDate = new Date();
        if (checkEvent() && !allEventsDates.contains(eventStrDate)){
            // Creates the event as a constructor
            if (!hasAccepted) {
                newEvent.setEventCharacterize("O");
            } else {
                newEvent.setEventCharacterize("G");
            }
            newEvent.setEventName(eventTitleTV.getText().toString());
            newEvent.setCustomerName(customerName);
            newEvent.setCustomerPhone(customerPhone);
            newEvent.setCustomerEmail(customerEmail);
            newEvent.setEventLocation(eventLocation);
            newEvent.setEventMissions(null);

            newEvent.setDateOfEvent(eventStrDate);
            String strDate = DateConvertor.dateToString(currentDate,"yyyyMMddHHmm");  // Casting the Date to String
            newEvent.setDateOfCreation(strDate);

            newEvent.setEventContent(eventContent);
            newEvent.setEventCost(eventCost);
            newEvent.setEventEmployees(eventEmployee);
            newEvent.setEventPayment(userSelection);
            newEvent.setEventShows(selectedShows);
            newEvent.setEventEquipments(selectedMaterials);
            newEvent.setHasAccepted(hasAccepted);
            newEvent.setPaid(isPaid);

            ProgressDialog progressDialog = new ProgressDialog(newEventActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.orange_flag),
                    PorterDuff.Mode.SCREEN);
            progressDialog.setIndeterminateDrawable(drawable);
            progressDialog.setIndeterminate(false);
            final TextView title = new TextView(this);
            title.setText(newEvent.getEventName()+" ביצירה");
            title.setTextSize(20);
            title.setPadding(0,15,55,15);
            title.setTextColor(Color.GRAY);
            title.setTypeface(ResourcesCompat.getFont(title.getContext(), R.font.rubik_semibold));
            progressDialog.setCustomTitle(title);
            progressDialog.setMessage("בניית הצעת האירוע בתהליך...");
            final int[] progress = {0};
            new CountDownTimer(6000, 200) {
                @Override
                public void onTick(long l) {
                    progress[0]++;
                    progressDialog.incrementProgressBy(progress[0]);
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                }
            }.start();

            File eventFile = creatingFile(newEvent);

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run(){
                    if (uploadToFB(eventFile, progressDialog)){
                        String childID = newEvent.getDateOfEvent();
                        if (newEvent.getEventCharacterize().equals("G")){
                            refGreen_Event.child(childID).setValue(newEvent);
                            updateFBData(newEvent); // Update the Business data
                        } else{
                            refOrange_Event.child(childID).setValue(newEvent);
                        }
                        progressDialog.setCancelable(true);
                        //progressDialog.setProgress = 100
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            @Override
                            public void run(){
                                progressDialog.dismiss();
                            }
                        };
                        handler.postDelayed(r,3000);
                        Snackbar.make(layout,"תהליך שליחת המייל בהקמה..", 1500).show();
                        Handler handler1 = new Handler();
                        String eventName = newEvent.getEventName();
                        Runnable r1 = new Runnable() {
                            @Override
                            public void run(){
                                sendingToEmail(eventFile, customerEmail, eventName);
                            }
                        };
                        handler1.postDelayed(r1,2000);
                        Snackbar.make(layout,eventTitleTV.getText().toString()+" נוצר בהצלחה", 3000).show();
                        clearAllFields();
                    } else {
                        Snackbar.make(layout,"שגיאה בתהליך ייצור", 3000).show();
                    }
                }
            };
            handler.postDelayed(r,3000);
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
            String strDate = DateConvertor.dateToString(currentDate, "yyyyMMddHHmm");
            updatedEvent.setDateOfCreation(strDate);

            updatedEvent.setEventContent(eventContent);
            updatedEvent.setEventCost(eventCost);
            if (eventEmployee == 0){
                updatedEvent.setEventEmployees(eventEmployee);
            }
            updatedEvent.setEventPayment(userSelection);
            updatedEvent.setEventShows(selectedShows);
            updatedEvent.setEventEquipments(selectedMaterials);
            updatedEvent.setPaid(isPaid);
            updatedEvent.setHasAccepted(hasAccepted);

            if (!hasAccepted) {
                newEvent.setEventCharacterize("O");
            } else {
                newEvent.setEventCharacterize("G");
            }
            ProgressDialog progressDialog = new ProgressDialog(newEventActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.orange_flag),
                    PorterDuff.Mode.SCREEN);
            progressDialog.setIndeterminateDrawable(drawable);
            final TextView title = new TextView(this);
            title.setText(" ביצירה"+updatedEvent.getEventName());
            title.setTextSize(20);
            title.setPadding(0,15,30,15);
            title.setTextColor(Color.GRAY);
            title.setTypeface(ResourcesCompat.getFont(title.getContext(), R.font.rubik_semibold));
            progressDialog.setCustomTitle(title);
            progressDialog.setMessage("בניית הצעת האירוע בתהליך...");
            final int[] progress = {0};
            new CountDownTimer(6000, 200) {
                @Override
                public void onTick(long l) {
                    progress[0]++;
                    progressDialog.incrementProgressBy(progress[0]);
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                }
            }.start();
            File eventFile = creatingFile(updatedEvent);

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run(){
                    if (uploadToFB(eventFile, progressDialog)){
                        if (updatedEvent.getEventCharacterize().equals("G")){
                            refGreen_Event.child(updatedEvent.getDateOfEvent()).setValue(updatedEvent);
                        } else{
                            refOrange_Event.child(updatedEvent.getDateOfEvent()).setValue(updatedEvent);
                        }
                        progressDialog.setCancelable(true);
                        updateMode = false;
                        //progressDialog.setProgress = 100
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            @Override
                            public void run(){
                                progressDialog.dismiss();
                            }
                        };
                        handler.postDelayed(r,3000);
                        Snackbar.make(layout,"תהליך שליחת המייל בהקמה..", 1500).show();
                        Handler handler1 = new Handler();
                        String eventName = updatedEvent.getEventName();
                        Runnable r1 = new Runnable() {
                            @Override
                            public void run(){
                                sendingToEmail(eventFile, customerEmail, eventName);
                            }
                        };
                        handler1.postDelayed(r1,2000);
                        Snackbar.make(layout,"התעדכן בהצלחה", 3000).show();
                        clearAllFields();
                    } else {
                        Snackbar.make(layout,"שגיאה בתהליך עדכון", 3000).show();
                    }
                }
            };
            handler.postDelayed(r,3000);
        }
    }

    private void updateFBData(Event tempEvent) {
        BusinessEqu businessEqu = new BusinessEqu();
        refBusinessEqu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (int i = 0; i < 1; i++) {
                    businessEqu.setAvailableEmployees(snapshot.child("availableEmployees").getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refBusinessEqu.child("availableEmployees").setValue(businessEqu.getAvailableEmployees() - tempEvent.getEventEmployees());

        int usedMaterials;
        for (int i = 0; i < tempEvent.getEventEquipments().size(); i++) {
            if (tempEvent.getEventEquipments().get(i).contains("t")){
                usedMaterials = Integer.parseInt(tempEvent.getEventEquipments().get(i).substring(2));
                refBusinessEqu.child("materials").child(i+"").child("usedAmount").setValue(usedMaterials);
            }
        }

    }

    private boolean uploadToFB(File eventFile, ProgressDialog progressDialog) {
        boolean [] flag = {true};
        progressDialog.setProgress(0);
        progressDialog.setMessage("מבצע העלאת אירוע לענן...");

        // Upload to FireBase DateBase
        Uri file = Uri.fromFile(new File(eventFile.getPath()));
        StorageReference riversRef = storageRef.child("files/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.setCancelable(true);
                progressDialog.dismiss();
                flag[0] = false;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                flag[0] = true;
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int currentProgress = (int) (100*(snapshot.getBytesTransferred()/snapshot.getTotalByteCount()));
                progressDialog.incrementProgressBy(currentProgress);
                progressDialog.show();
            }
        });
        return flag[0];
    }

    private void clearAllFields() {
        newEvent = updatedEvent = null;
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
        paymentSpinner.setSelection(0); // Return to the default selection "אמצעי תשלום"


        for (int i = 0; i < selectedShows.size(); i++) {
            if (selectedShows.get(i)){
                selectedShows.set(i, false);
            }


        }

        for (int i = 0; i < selectedMaterials.size(); i++) {
            if (selectedMaterials.get(i).contains("t")){
                selectedMaterials.set(i, "f_0");
            }
        }
        isPaid = hasAccepted = false;
        DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.orange_flag));
        DrawableCompat.setTint(hasAcceptedButton.getDrawable(), ContextCompat.getColor(hasAcceptedButton.getContext(), R.color.gray_700));
        DrawableCompat.setTint(isPaidButton.getDrawable(), ContextCompat.getColor(isPaidButton.getContext(), R.color.gray_700));
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

    @SuppressLint("LongLogTag")
    private void sendingToEmail(@NonNull File fileToSend, String customerEmail, String eventName) {
        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AdScreen.setPadding(15,0,15,0);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("שליחת אימייל אל הלקוח");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);
        final TextView fromTV = new TextView(this);
        fromTV.setText("ישלח מ "+Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
        fromTV.setTextSize(18);
        fromTV.setGravity(Gravity.RIGHT);
        fromTV.setPadding(0,15,30,30);
        fromTV.setTypeface(ResourcesCompat.getFont(fromTV.getContext(), R.font.rubik_medium));
        final TextView toTV = new TextView(this);
        toTV.setText("ישלח אל "+customerEmail);
        toTV.setTextSize(18);
        toTV.setGravity(Gravity.RIGHT);
        toTV.setPadding(0,15,30,30);
        toTV.setTypeface(ResourcesCompat.getFont(toTV.getContext(), R.font.rubik_medium));
        final EditText desET = new EditText(this);
        desET.setHint("תוכן ההודעה");
        desET.setTextSize(18);
        desET.setGravity(Gravity.RIGHT);
        desET.setPadding(0,15,30,30);
        desET.setTypeface(ResourcesCompat.getFont(desET.getContext(), R.font.rubik_medium));
        final TextView fileNameTV = new TextView(this);
        fileNameTV.setText("הקובץ המצורף: "+fileToSend.getName());
        fileNameTV.setTextSize(16);
        fileNameTV.setGravity(Gravity.RIGHT);
        fileNameTV.setPadding(0,15,30,30);
        fileNameTV.setTypeface(ResourcesCompat.getFont(fileNameTV.getContext(), R.font.rubik_medium));
        fileNameTV.setInputType(InputType.TYPE_CLASS_NUMBER);
        AdScreen.addView(fromTV);
        AdScreen.addView(toTV);
        AdScreen.addView(desET);
        AdScreen.addView(fileNameTV);
        adb.setView(AdScreen);
        adb.setCancelable(false);

        adb.setPositiveButton("שלח", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {customerEmail});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, eventName);
                emailIntent.putExtra(Intent.EXTRA_TEXT, desET.getText().toString());
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri fileUri = FileProvider.getUriForFile(newEventActivity.this,getPackageName()+".provider",fileToSend);
                emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(Intent.createChooser(emailIntent, "ישלח באמצעות..."));
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
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
        titlePaint.setTypeface(Typeface.create("Calibri",Typeface.NORMAL));
        canvas.drawText( ""+event.getCustomerName(), (float) (width*0.7), 320, titlePaint);
        canvas.drawText(""+event.getCustomerPhone(),(float) (width*0.7), 350, titlePaint);
        canvas.drawText(""+event.getCustomerEmail(),(float) (width*0.7), 380, titlePaint);

        if (event.isHasAccepted()){
            titlePaint.setTextSize(20);
            titlePaint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
            titlePaint.setColor(Color.rgb(00,80,00));
            titlePaint.setTypeface(Typeface.create("Calibri",Typeface.NORMAL));
            canvas.drawText("<< אירוע אושר והחלה עבודה >>",(float) width/2, 380, titlePaint);
        }

        Bitmap priceBM = BitmapFactory.decodeResource(getResources(), R.drawable.pdf_prize);
        Bitmap scaledbmp2 = Bitmap.createScaledBitmap(priceBM,858,45,false);
        canvas.drawBitmap(scaledbmp2, (width-858)/2,height/5+100,paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
        canvas.drawText( ""+event.getEventCost()+"₪", (float) (width*0.6), (float) (height/5+100+45*0.6), paint);

        // Parsing the String of the date to a format String
        Date createdDate = DateConvertor.stringToDate(event.getDateOfCreation(), "yyyyMMddHHmm");
        String strCreatedDate = DateConvertor.dateToString(createdDate, "dd/MM/yyyy");
        canvas.drawText(""+strCreatedDate,(float) (width*0.43), (float) (height/5+100+45*0.6), paint);

        // WRITING GENERAL DATA
        float currentHeight = (float) (height/5+122.5+100);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
        Date selectedDate = DateConvertor.stringToDate(event.getDateOfEvent(), "yyyyMMddHHmm");
        String strSelectedDate = DateConvertor.dateToString(selectedDate, "dd/MM/yyyy");
        String strTime = DateConvertor.dateToString(selectedDate, "HH:mm");
        canvas.drawText("האירוע יהיה בתאריך: "+strSelectedDate + " מהשעה: "+strTime,width/2, currentHeight, paint);
        currentHeight += 50;

        // WRITING THE SELECTED SHOW(S)
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
        Integer[] selectedShowIndexes = {-1,-1};
        boolean toAdd = true;
        for (int i = 0; i < event.getEventShows().size(); i++) {
            if (event.getEventShows().get(i) && toAdd){
                selectedShowIndexes[0] = i;
                toAdd = false;
            }
            else if (selectedShowIndexes[0] != -1 && event.getEventShows().get(i)){
                selectedShowIndexes[1] = i;
            }
        }
        canvas.drawText("מופע: "+allShows.get(selectedShowIndexes[0]).getShowTitle(),(858+(width-858)/2), currentHeight, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create("Calibri",Typeface.NORMAL));
        canvas.drawText("תיאור: "+allShows.get(selectedShowIndexes[0]).getDescription(),(858+(width-858)/2), currentHeight+25, paint);
        currentHeight += 25;
        canvas.drawText("עלות: "+allShows.get(selectedShowIndexes[0]).getCost()+"₪",(858+(width-858)/2), currentHeight+25, paint);
        currentHeight += 25;
        if (selectedShowIndexes[1] != -1){
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
            canvas.drawText("מופע: "+allShows.get(selectedShowIndexes[1]).getShowTitle(),(858+(width-858)/2), (float) currentHeight+50, paint);
            currentHeight += 50;
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setTextSize(16);
            paint.setTypeface(Typeface.create("Calibri",Typeface.NORMAL));
            canvas.drawText("תיאור: "+allShows.get(selectedShowIndexes[1]).getDescription(),(858+(width-858)/2), currentHeight+25, paint);
            currentHeight += 25;
            canvas.drawText("עלות: "+allShows.get(selectedShowIndexes[1]).getCost()+"₪",(858+(width-858)/2), currentHeight+25, paint);
            currentHeight += 25;
        }

        // WRITING EXTRA CONTENT <event.getContent()>
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
        canvas.drawText("הערות:",(858+(width-858)/2),  currentHeight+45, paint);
        currentHeight +=45;
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create("Calibri",Typeface.NORMAL));
        String[] strParts = event.getEventContent().split("\n"); // Writing the content as it was written by Jakkob
        for (int i = 0; i < strParts.length ; i++) {
            canvas.drawText(strParts[i],(858+(width-858)/2),  currentHeight+25, paint);
            currentHeight += 25;
        }

        // END OF DOCUMENT
        Bitmap endBM = BitmapFactory.decodeResource(getResources(), R.drawable.pdf_end);
        Bitmap scaledbmp3 = Bitmap.createScaledBitmap(endBM,1191,618,false);
        canvas.drawBitmap(scaledbmp3, (width-1191)/2,height-618,paint);
        paint.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
        canvas.drawText(""+ (int) Math.floor(eventCost*1.17), (float) (width*0.25), height-595, paint); // Printing the total price after taxes (117% from the price)
        canvas.drawText(event.getEventPayment(), (float) (850+(width-1191)/2), (float) (height-535), paint); // Printing the event payment method
        canvas.drawText("** יעקב אברהם **", (float) (1191*0.4+(width-1191)/2), (float) (height-400), paint); // Printing the signature
        if (event.isHasAccepted()){
            canvas.drawText("** "+event.getCustomerName()+" **", (float) (1191*0.7+(width-1191)/2), (float) (height-400), paint); // Printing the customer's signature
        }
        pdfDocument.finishPage(page1);

        File dataFile = new File(rootPath, event.getEventName()+event.getDateOfEvent()+".pdf");;

        try {
            pdfDocument.writeTo(new FileOutputStream(dataFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();

        return dataFile;
    }


    private boolean checkEvent() {
        boolean flag = true;
        customerName = nameCustomerET.getText().toString();
        customerEmail = emailCustomerET.getText().toString();
        customerPhone = phoneCustomerET.getText().toString();
        eventLocation = locationET.getText().toString();
        eventContent = contentET.getText().toString();

        if (eventTitleTV.getText().toString().isEmpty() || customerName.isEmpty() || customerPhone.isEmpty() || eventLocation.isEmpty() || eventContent.isEmpty()){
            flag = false;
            Snackbar.make(layout, "שדה לא יהיה ריק", 3000).show();
        }
        else if (eventTitleTV.getText().toString().equals("כותרת האירוע")){
            flag = false;
            Snackbar.make(layout, "כותרת אירוע לא תהיה ריקה", 3000).show();
        }

        else if (selectedDate != null){
            eventStrDate = DateConvertor.dateToString(selectedDate, "yyyyMMddHHmm");
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

        if(customerEmail.isEmpty()){
            flag = false;
            Snackbar.make(layout, "חסר כתובת אימייל", 3000).show();
        } else{
            // Checks if the email is valid and verified
            Matcher matcher =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(customerEmail);
            if (!matcher.find()){
                flag = false;
                Snackbar.make(layout, "כתובת אימייל לא חוקית", 3000).show();
            }
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
        else {
            if (!customerPhone.startsWith("0")){
                flag = false;
                Snackbar.make(layout, "מספר הטלפון לא חוקי", 3000).show();
            }
        }

        boolean anyMaterial = checkForMaterial();
        if (!anyMaterial){
            flag = false;
            Snackbar.make(layout,"נא לבחור ציוד לאירוע", 3000).show();
        }
        else if (!selectedShows.contains(true)){
            flag = false;
            Snackbar.make(layout,"נא לבחור מופע/ים לאירוע", 3000).show();
        }
        else{
            int showsAmount = 0;
            for (int i = 0; i < selectedShows.size(); i++) {
                if (selectedShows.get(i)) showsAmount ++;
            }
            if (showsAmount > 2){
                flag = false;
                Snackbar.make(layout,"לא ניתן יותר משני מופעים", 3000).show();
            }
        }
        if (userSelection.equals(paymentSelection[0])){
            flag = false;
            Snackbar.make(layout,"חסר אמצעי תשלום", 3000).show();
        }

        return flag && checkLocation();
    }

    private boolean checkForMaterial() {
        int i = 0;
        boolean flag = false;
        while (i < selectedMaterials.size() && !flag) {
            flag = selectedMaterials.get(i).contains("t_");
            i++;
        }
        return flag;
    }

    @SuppressLint("MissingPermission")
    private boolean checkLocation() {
        boolean[] locationFlag = {true};
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(newEventActivity.this);
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(eventLocation, 6);
                        //LatLng latLng = new LatLng(user_address.getLatitude(), user_address.getLongitude());
                    } catch (Exception e) {
                        locationFlag[0] = false;
                        Snackbar.make(layout, "אין כתובת כזו", 3000).show();
                    }
                }
            }
        });
        return locationFlag[0];
    }

    /**
     * Change is paid.
     *
     * @param view the view
     */
    public void changeIsPaid(View view) {
        if (updateMode || editingMode){
            if (!isPaid){
                DrawableCompat.setTint(isPaidButton.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.green_flag));
                isPaid = true;
            }
            else {
                isPaid = false;
                DrawableCompat.setTint(isPaidButton.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.gray_700));
            }
        }
    }

    /**
     * Change has accepted.
     *
     * @param view the view
     */
    public void changeHasAccepted(View view) {
        if (editingMode){
            if (!hasAccepted){
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green_flag));
                DrawableCompat.setTint(hasAcceptedButton.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.green_flag));
                hasAccepted = true;
            }
            else {
                hasAccepted = false;
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.orange_flag));
                DrawableCompat.setTint(hasAcceptedButton.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.gray_700));
            }
        }
        else if (updateMode){
            if (hasAccepted){
                beforeRedEventAD(updatedEvent.getDateOfEvent());
            } else {
                beforeGreenEventAD(updatedEvent.getDateOfEvent());
            }
        }
    }

    private void beforeRedEventAD(String dateOfEvent) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setIcon(R.drawable.maps_sv_error_icon);
        final TextView titleTV = new TextView(this);
        titleTV.setText("ביטול אירוע");
        titleTV.setGravity(Gravity.RIGHT);
        titleTV.setTextColor(Color.rgb(178, 34, 34));
        titleTV.setTextSize(20);
        adb.setCustomTitle(titleTV);
        final TextView messageTV = new TextView(this);
        messageTV.setText("לאשר הגדרה כאירוע 'בוטל'?");
        messageTV.setGravity(Gravity.RIGHT);
        messageTV.setTextSize(16);
        adb.setView(messageTV);

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refGreen_Event.child(dateOfEvent).removeValue();
                // Changing all the properties to a 'RED' event
                updatedEvent.setEventCharacterize("R");
                updatedEvent.setHasAccepted(false);
                refRed_Event.child(dateOfEvent).setValue(updatedEvent);
                clearAllFields();
                Snackbar.make(layout, "אירוע הוגדל כבוטל", 5000).setAction("בטל פעולה", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refRed_Event.child(dateOfEvent).removeValue();
                        // Changing all the properties to a 'GREEN' event back
                        updatedEvent.setEventCharacterize("G");
                        updatedEvent.setHasAccepted(true);
                        refGreen_Event.child(dateOfEvent).setValue(updatedEvent);                    }
                }).show();
            }
        });

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    private void beforeGreenEventAD(String dateOfEvent) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("אישור אירוע");
        titleTV.setGravity(Gravity.RIGHT);
        titleTV.setTextColor(Color.rgb(0, 128, 0));
        titleTV.setTextSize(20);
        adb.setCustomTitle(titleTV);
        final TextView messageTV = new TextView(this);
        messageTV.setText("לאשר הגדרה כאירוע 'קרוב'?");
        messageTV.setGravity(Gravity.RIGHT);
        messageTV.setTextSize(16);
        adb.setView(messageTV);

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refOrange_Event.child(dateOfEvent).removeValue();
                // Changing all the properties to a 'GREEN' event
                updatedEvent.setEventCharacterize("G");
                updatedEvent.setHasAccepted(true);
                refGreen_Event.child(dateOfEvent).setValue(updatedEvent);
                clearAllFields();
                Snackbar.make(layout, "אירוע הוגדל כבוטל", 5000).setAction("בטל פעולה", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refGreen_Event.child(dateOfEvent).removeValue();
                        // Changing all the properties to an 'ORANGE' event back
                        updatedEvent.setEventCharacterize("O");
                        updatedEvent.setHasAccepted(false);
                        refOrange_Event.child(dateOfEvent).setValue(updatedEvent);                    }
                }).show();
            }
        });

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }
}