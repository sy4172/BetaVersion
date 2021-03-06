package com.example.betaversion;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.example.betaversion.FBref.fileRef;
import static com.example.betaversion.FBref.refBusinessEqu;
import static com.example.betaversion.FBref.refEnd_Event;
import static com.example.betaversion.FBref.refGreen_Event;
import static com.example.betaversion.FBref.refOrange_Event;
import static com.example.betaversion.FBref.reflive_Event;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    TextView dateRangeTV, statusEventsTV;
    String[] selections;
    String userSelection;
    Date dateSt, dateEd;
    BottomNavigationView bottomNavigationView;
    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList, addressList;
    ArrayList<Integer> employeesList;
    ArrayList<Boolean> isPaidList, hasAcceptedList;
    File rootPath;
    CustomAdapterEvents customAdapterEventsGreen, customAdapterEventsOrange;

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
        statusEventsTV = findViewById(R.id.statusEventsTV);
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

        selections = new String[]{"????????","???????? ?????? ??????????","????????","????????","?????????????? ??????????","?????? ??????????????"}; // Includes all the status of the events
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, selections);
        selectionLV.setAdapter(adp);

        rootPath = new File(this.getExternalFilesDir("/"), "myPDFS");

        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        Intent gi = getIntent();
        if (gi != null) {
            userSelection = gi.getStringExtra("userSelection");
            if (userSelection != null){
                if (userSelection.equals("????????")){
                    selectionLV.setSelection(2);
                    readEvents("orangeEvent");
                }
                else {
                    userSelection = "????????"; // To set as a default selection at start
                    readEvents("greenEvent"); // Show the default option
                    selectionLV.setSelection(0);
                }

            } else {
                userSelection = "????????"; // To set as a default selection at start
                readEvents("greenEvent"); // Show the default option
                selectionLV.setSelection(0);
            }
        } else {
            userSelection = "????????"; // To set as a default selection at start
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
            if (userSelection != null && userSelection.equals("????????")) {
                selectionLV.setSelection(2);
                readEvents("orangeEvent");
            } else {
                userSelection = "????????"; // To set as a default selection at start
                readEvents("greenEvent"); // Show the default option
                selectionLV.setSelection(0);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);
        menu.add("??????????");
        menu.add("????????");
        menu.add("????????");
        menu.add("??????");
        if (userSelection.equals("???????? ?????? ??????????")){
            menu.add("?????? ??????????");
            menu.add("?????? PDF");
        }
        if (userSelection.equals("????????")){
            menu.add("?????? PDF");
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String option = item.getTitle().toString();
        int position = adpInfo.position;

        if(option.equals("??????????")){
            Intent si = new Intent(this, newEventActivity.class);
            si.putExtra("eventID",dateEvents.get(position));
            si.putExtra("flag", eventCharacterizeList.get(position));
            si.putExtra("editingMode",false);
            startActivity(si);
        }
        else if (option.equals("????????")){
            Intent si = new Intent(this, newEventActivity.class);
            si.putExtra("eventID",dateEvents.get(position));
            si.putExtra("flag", eventCharacterizeList.get(position));
            si.putExtra("updatingMode",true);
            startActivity(si);
        }
        else if (option.equals("????????")){
            Intent si = new Intent(this, MapActivity.class);
            si.putExtra("address", addressList.get(position));
            startActivity(si);
        }
        else if (option.equals("??????")){
            Snackbar.make(selectionLV, "???????????? "+titleEvents.get(position)+" ????????", 3000).show();
            removeFromFB(dateEvents.get(position), eventCharacterizeList.get(position), namesList.get(position), position); // Remove from the FireBase
            File fileToDelete = new File(rootPath, titleEvents.get(position)+dateEvents.get(position)+".pdf");
            fileToDelete.delete(); // Deleting for the storage
            titleEvents.remove(position);
            dateEvents.remove(position);
            eventCharacterizeList.remove(position);
            employeesList.remove(position);
            namesList.remove(position);
            addressList.remove(position);

            CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
            eventsList.setAdapter(customAdapterEvents);
        }
        else if (option.equals("?????? ??????????")){
            isPaidList.set(position, true);
            // Update the FireBase DataBase
            refGreen_Event.child(dateEvents.get(position)).child("paid").setValue(true);
            checkMoney();
            Snackbar.make(selectionLV, "???????????? "+titleEvents.get(position)+" ????????", 5000).setAction("??????", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPaidList.set(position, false);
                    // Update the FireBase DataBase
                    refGreen_Event.child(dateEvents.get(position)).child("paid").setValue(false);
                    CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                    eventsList.setAdapter(customAdapterEvents);
                }
            }).show();
        } else if (option.equals("?????? PDF")){
            String tempUrl = titleEvents.get(position)+dateEvents.get(position)+".pdf";
            File[] files = new File(rootPath.getPath()).listFiles();
            boolean isExist = true;
            for (File file : Objects.requireNonNull(files)) {
                if (file.isFile() && file.getName().equals(tempUrl)) {
                    isExist = true;
                    openFile(file.getPath());
                } else isExist = false;
            }

            if (!isExist){
                StorageReference filePath = fileRef.child(tempUrl);
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        openFile(downLoadFile(eventsActivity.this, uri, tempUrl,DIRECTORY_DOWNLOADS).getPath());
                    }
                });
            }
        }
        return super.onContextItemSelected(item);
    }

    private void openFile(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File dataFile = new File(filePath);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Uri contentUri = FileProvider.getUriForFile(eventsActivity.this, "com.example.betaversion.provider", dataFile);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/pdf");
            }else{
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(dataFile), "application/vnd.android.package-archive");

            }

            eventsActivity.this.startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    private File downLoadFile(Context context, Uri uri, String fileUrl, String destinationDirectory) {
        File tempFile = new File(rootPath, fileUrl);
        if (!tempFile.exists()){
            DownloadManager downloadManager = (DownloadManager)
                    context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalFilesDir(context, destinationDirectory,tempFile.getAbsolutePath());
            downloadManager.enqueue(request);
            return tempFile;
        }
        return tempFile;
    }

    /**
     * removeFromFB is responsible to remove the event when it's selected
     * @param eventId the event id of the selected event
     * @param character the character of the selected event
     * @param eventName the name of the event
     * @param position the current position
     */
    private void removeFromFB(String eventId, String character, String eventName, int position) {
        switch (character){
            case ("G"):{
                final int[] countervailEmployees = {0};

                refBusinessEqu.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (int i = 0; i < 1; i++) {
                            countervailEmployees[0] = snapshot.child("availableEmployees").getValue(Integer.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                refBusinessEqu.child("availableEmployees").setValue(countervailEmployees[0] - employeesList.get(position));
                refGreen_Event.child(eventId).removeValue();
                // Remove the PDF file of the event
                StorageReference deleteFile = fileRef.child(eventName+eventId+".pdf"); // The complete url of the PDF file.
                deleteFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(dateRangeTV,"?????????? ????????", 1000).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(dateRangeTV,"?????????? ??????????: "+e.getMessage(), 1000).show();
                    }
                });
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
        statusEventsTV.setText("");
        eventsList.setAdapter(null);
        titleEvents.clear();
        dateEvents.clear();
        phonesList.clear();
        employeesList.clear();
        eventCharacterizeList.clear();
        namesList.clear();
        addressList.clear();
        isPaidList.clear();
        hasAcceptedList.clear();
        switch (userSelection) {
            case "????????": {
                readEvents("greenEvent");
            }
            break;
            case "???????? ?????? ??????????":{
                checkMoney();
            }
            break;
            case "????????": {
                readEvents("orangeEvent");
            }
            break;
            case "????????": {
                readEvents("redEvent");
            }
            break;
            case "?????????????? ??????????": {
                final int[] eventCount = {0};
                refEnd_Event.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            eventCount[0]++;
                        }
                        CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(), titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                        eventsList.setAdapter(customAdapterEvents);

                        statusEventsTV.setText("????''?? "+eventCount[0]+" ?????????????? ???????? ????.");
                        statusEventsTV.setTextColor(getResources().getColor(R.color.black));

                        if (titleEvents.isEmpty()){
                            statusEventsTV.setText("?????? ?????????????? ???????? ????.");
                            statusEventsTV.setTextColor(getResources().getColor(R.color.red_flag));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            break;
            case "?????? ??????????????": {
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
        final int[] eventCount = {0};
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
                    eventCount[0] ++;
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEvents);

                if (titleEvents.isEmpty()){
                    statusEventsTV.setText("?????? ?????????????? ???????? ????.");
                    statusEventsTV.setTextColor(getResources().getColor(R.color.red_flag));
                } else {
                    statusEventsTV.setText("????''?? "+eventCount[0]+" ?????????????? ???????? ????.");
                    statusEventsTV.setTextColor(getResources().getColor(R.color.black));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * checkMoney is for reading all the events that is paid and display them when the
     * userSelection is "???????? ?????? ??????????"
     */
    private void checkMoney() {
        final int[] eventCount = {0};
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
                        eventCount[0] ++;
                    }
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEvents);

                if (eventCount[0] == 0){
                    statusEventsTV.setText("?????? ?????????????? ???????? ????.");
                    statusEventsTV.setTextColor(getResources().getColor(R.color.red_flag));
                } else {
                    statusEventsTV.setText("????''?? "+eventCount[0]+" ?????????????? ???????? ????.");
                    statusEventsTV.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * openDateRangeAD is for opening an AlertDialog for getting all the events that is in the range
     * of the dates and display them when the userSelection is "?????? ??????????????"
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
        titleTV.setText("?????????? ??????????");
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
     * according to userSelection is "?????? ??????????????"
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
                    dateRangeTV.setText(strDateSt+" - "+strDateEd);
                    readAllEventsByDate();
                }
            }
        }, year, month, day);
        final TextView titleTV = new TextView(this);
        titleTV.setText("?????????? ????????");
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
            Snackbar.make(dateRangeTV, "?????????????? ????????", 3000).show();
            flag = false;
        } else if (dateEd.before(dateSt)){
            Snackbar.make(dateRangeTV, "???????? ???? ??????????", 3000).show();
            flag = false;
        } else if (dateSt.after(dateEd)){
            Snackbar.make(dateRangeTV, "???????? ???? ??????????", 3000).show();
            flag = false;
        }
        return flag;
    }

    /**
     * readAllEventsByDate is for reading all the events from the FireBase DataBase that is in the range
     * of the dates which have been detected and display them when the userSelection is "?????? ??????????????"
     */
    private void readAllEventsByDate() {
        eventsList.setAdapter(null);
        int[] eventCount = {0};

        refOrange_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    Date tempEventSelectedDate = DateConvertor.stringToDate(Objects.requireNonNull(tempEvent).getDateOfEvent(), "yyyyMMddHHmm");

                    if (Objects.requireNonNull(tempEventSelectedDate).after(dateSt) && tempEventSelectedDate.before(dateEd)){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                        isPaidList.add(tempEvent.isPaid());
                        hasAcceptedList.add(tempEvent.isHasAccepted());
                        eventCount[0] ++;
                    }
                }
                customAdapterEventsOrange = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEventsOrange);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        refGreen_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    Date tempEventSelectedDate = DateConvertor.stringToDate(Objects.requireNonNull(tempEvent).getDateOfEvent(), "yyyyMMddHHmm");

                    if (dateSt.compareTo(tempEventSelectedDate) <= 0 && dateEd.compareTo(tempEventSelectedDate) >= 0){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                        isPaidList.add(tempEvent.isPaid());
                        hasAcceptedList.add(tempEvent.isHasAccepted());
                        eventCount[0] ++;
                    }
                }
                customAdapterEventsGreen = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEventsGreen);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        refEnd_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                Event tempEvent;
                for(DataSnapshot data : dS.getChildren()) {
                    tempEvent = data.getValue(Event.class);

                    Date tempEventSelectedDate = DateConvertor.stringToDate(Objects.requireNonNull(tempEvent).getDateOfEvent(), "yyyyMMddHHmm");

                    if (Objects.requireNonNull(tempEventSelectedDate).after(dateSt) && tempEventSelectedDate.before(dateEd)){
                        titleEvents.add(Objects.requireNonNull(tempEvent).getEventName());
                        dateEvents.add(tempEvent.getDateOfEvent());
                        phonesList.add(tempEvent.getCustomerPhone());
                        employeesList.add(tempEvent.getEventEmployees());
                        eventCharacterizeList.add(tempEvent.getEventCharacterize());
                        namesList.add(tempEvent.getCustomerName());
                        isPaidList.add(tempEvent.isPaid());
                        hasAcceptedList.add(tempEvent.isHasAccepted());
                        eventCount[0] ++;
                    }
                }
                CustomAdapterEvents customAdapterEvents = new CustomAdapterEvents(getApplicationContext(),titleEvents, dateEvents, namesList, phonesList, employeesList, eventCharacterizeList, isPaidList, hasAcceptedList);
                eventsList.setAdapter(customAdapterEvents);

                if (eventCount[0] != 0){
                    statusEventsTV.setText("????''?? "+eventCount[0]+" ?????????????? ????????????????:");
                    statusEventsTV.setTextColor(getResources().getColor(R.color.black));
                } else {
                    statusEventsTV.setText("?????? ?????????????? ????????????????:");
                    statusEventsTV.setTextColor(getResources().getColor(R.color.red_flag));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
        titleTV.setText("?????????? ????????????");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);

        final TextView messageTV = new TextView(this);
        messageTV.setText("???????????? "+ FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))+" ??????????.");
        messageTV.setTextSize(18);
        messageTV.setGravity(Gravity.RIGHT);
        messageTV.setPadding(0,15,30,0);
        messageTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        adb.setView(messageTV);

        adb.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("????", new DialogInterface.OnClickListener() {
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