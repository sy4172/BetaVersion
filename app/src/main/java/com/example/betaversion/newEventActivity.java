package com.example.betaversion;

import static com.example.betaversion.FBref.refBusinessEqu;
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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

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

    String customerName, customerPhone, customerEmail, eventLocation, eventContent, userSelection, eventStrDate;
    int eventCost;
    Date selectedDate, currentDate;

    ArrayList<Shows> allShows, selectedShows;
    ArrayList<Material> allMaterials, selectedMaterials;
    ArrayList<String> showsKeyList, showsDesList, materialsKeyList, materialsUsedList;
    ArrayList<Integer> showsDataList, materialsDataList;

    Spinner paymentSpinner;
    String [] paymentSelection = new String[]{" ","שוטף +30","שוטף +60","שוטף +90"};

    Event newEvent;
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

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, paymentSelection);
        paymentSpinner.setAdapter(adp);

        paymentSpinner.setOnItemSelectedListener(this);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        eventTitleTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                changeTitleEvent();
                return false;
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

        getAllShowsToDisplay();
        getAllMaterialsToDisplay();

        newEvent = new Event();
        selectedMaterials = new ArrayList<>();
        selectedShows = new ArrayList<>();

        rootPath = new File(this.getExternalFilesDir("/"), "myPDFS");

        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
    }

    /**
     * getAllShowsToDisplay method gets all the Shows objects that were created to display on the TextView objects from the FireBase DataBase.
     *
     */
    private void getAllShowsToDisplay() {
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
                }

                for (int i = 0; i < showsKeyList.size() - 1; i++) {
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
                                selectedShows.add(allShows.get(index));
                                eventCost += showsDataList.get(index);
                                eventCostTV.setText(String.valueOf(eventCost));
                            }
                            else{
                                tempChip.setChipIconResource(R.drawable.null1);
                                isToAdd[0] = true;

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * getAllMaterialsToDisplay method gets all the Materials objects that were created to display on the TextView objects from the FireBase DataBase.
     *
     */
    private void getAllMaterialsToDisplay() {
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
                }

                for (int i = 0; i < materialsKeyList.size() - 1; i++) {
                    Chip tempChip = new Chip(newEventActivity.this);
                    tempChip.setText(materialsKeyList.get(i));
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
                                selectedMaterials.add(allMaterials.get(index));
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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void changeTitleEvent() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("כותרת האירוע");
        final EditText titleET = new EditText(this);
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

                eventTitleTV.setText(newTitle);
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    public void checkIfToExit(View view) {
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

    private void moveToPreviousAct() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
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
                        if (selectedDate.after(new Date())){
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
        if (checkEvent()){
            // Creates the event as a constructor
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
            newEvent.setEventEmployees(eventCost);
            newEvent.setEventPayment(userSelection);
            newEvent.setEventShows(selectedShows);
            newEvent.setEventEquipments(selectedMaterials);

            if (userSelection.equals(" ")) openADCheckPayment();

            File eventFile = creatingFile();
            sendingFileToEmail(eventFile);
            uploadFileToFB(eventFile);

            // Save in the RealTimeDataBase TODO: need to check the child writing of the events.
            if (newEvent.getEventCharacterize() == 'G'){
                reflive_Event.child("greenEvent").child("").setValue(newEvent);
            } else{
                reflive_Event.child("orangeEvent").child("").setValue(newEvent);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void uploadFileToFB(File eventFile) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        final TextView title = new TextView(this);
        title.setText(newEvent.getEventName()+"מבצע שמירה בענן ל");
        title.setTextSize(18);
        title.setPadding(0,15,30,15);
        title.setTypeface(ResourcesCompat.getFont(title.getContext(), R.font.rubik_medium));
        progressDialog.setCustomTitle(title);
        progressDialog.setIcon(R.drawable.logo_white);
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);


        Uri file = Uri.fromFile(new File(eventFile.getPath()));
        StorageReference riversRef = storageRef.child("files/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Snackbar.make(layout,"שמירה נכשלה", 3000).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setCancelable(true);
                progressDialog.show();
                Snackbar.make(layout,"שמירה בוצעה בהצלחה", 3000).show();
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
    private void sendingFileToEmail(File fileToSend) {
        Uri uri = Uri.parse("mailto:" + "shahryani96@gmail.com")
                .buildUpon()
                .appendQueryParameter("to", newEvent.getCustomerEmail())
                .appendQueryParameter("subject", "סיכום יצירת אירוע: "+newEvent.getEventName())
                .appendQueryParameter("body", "להלן קובץ אישור העסקה:")
                .build();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        Uri uriFile =  Uri.fromFile(fileToSend);

        emailIntent.putExtra(Intent.EXTRA_STREAM, uriFile);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private File creatingFile() {
        File dataFile = new File(rootPath, System.currentTimeMillis()+newEvent.getEventName()+".docx");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        try {
            XWPFDocument xwpfDocument = new XWPFDocument();
            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
            XWPFRun xwpfRun = xwpfParagraph.createRun();

            xwpfRun.setText("TEST");
            xwpfRun.setFontSize(24);

            FileOutputStream fileOutputStream = new FileOutputStream(dataFile.getPath());
            xwpfDocument.write(fileOutputStream);

            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            xwpfDocument.close();

            // Casting from 'Word' to 'Pdf'
//            InputStream in = new FileInputStream(new File(dataFile.getPath()));
//            XWPFDocument document = new XWPFDocument(in);
//            PdfOptions options = PdfOptions.create();
//            OutputStream out = new FileOutputStream(new File(dataFile.getName()+".pdf"));
//            PdfConverter.getInstance().convert(document, out, options);
//
//            document.close();
//            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // TODO: Save the PDF file that is created in the internal storage
        return dataFile;
    }

    private void openADCheckPayment() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("תשלום לפני שמסיימים..");
        titleTV.setGravity(Gravity.RIGHT);
        titleTV.setTextSize(20);
        adb.setCustomTitle(titleTV);

        adb.setNegativeButton("אוקיי", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newEvent.setEventCharacterize('G');
                DrawableCompat.setTint(flag.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.green_flag));
                dialogInterface.dismiss();
            }
        });

        adb.setPositiveButton("לא עכשיו", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newEvent.setEventCharacterize('O');
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
        else if (!customerEmail.contains("@") || !customerEmail.endsWith(".com") || customerEmail.contains(" ")){
            flag = false;
            Snackbar.make(layout, "כתובת האימייל לא חוקית", 3000).show();
        }
        else if (customerEmail.substring(customerEmail.indexOf("@"),customerEmail.indexOf(".com") - 1).isEmpty()){
            flag = false;
            Snackbar.make(layout, "כתובת האימייל לא חוקית", 3000).show();
        }

        else if (customerPhone.length() < 9 || customerPhone.length() > 10){
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

        else if  (selectedMaterials.isEmpty()){
            flag = false;
            Snackbar.make(layout,"נא לבחור ציוד לאירוע", 3000).show();
        }
        else if (selectedMaterials.isEmpty()){
            flag = false;
            Snackbar.make(layout,"נא לבחור מופע/ים לאירוע", 3000).show();
        }

        return  flag;
    }

}