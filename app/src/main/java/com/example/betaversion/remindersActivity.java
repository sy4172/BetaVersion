package com.example.betaversion;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.example.betaversion.FBref.refReminders;
import static com.example.betaversion.FBref.remindersRef;
import static com.example.betaversion.FBref.storageRef;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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
 * * @version  	4.4
 * * @since		24/12/2021
 * <p>
 * * This reminderActivity.class displays and creates all the reminds for the business.
 */
public class remindersActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener, AdapterView.OnItemClickListener {

    BottomNavigationView bottomNavigationView;

    boolean isTextNewReminder, creationMode, isRecording, isPlaying, toDisplay;
    LinearLayout textLayout, recordLayout, reminderLayout;
    ImageView recordIV, playIV;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    File mediaSaverFile;

    File rootPath, dataFile;
    String fileName;

    int fileDuration, index;
    SeekBar seekBar;

    Date selectedDate, currentDate;
    TextView selectedDateTV, statusRemindersTV;
    EditText titleReminderET, contextReminderET;

    ListView remaindersLV;
    ArrayList<String> remindersKeyList, remindersTitleList, remindersContextList, remindersAudioContentList, remindersLastDateToRemindList;
    ArrayList<Boolean> isTextList;
    CustomAdapterReminder customAdapterReminder;

    private static final int PERMISSION_CODE = 12;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        recordLayout = findViewById(R.id.recordLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textLayout = findViewById(R.id.textLayout);
        reminderLayout = findViewById(R.id.reminderLayout);
        remaindersLV = findViewById(R.id.remaindersLV);
        statusRemindersTV = findViewById(R.id.statusRemindersTV);

        bottomNavigationView.setSelectedItemId(R.id.remainder); // set the selection of the bottomNavigationView object to the current activity
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        remaindersLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        remaindersLV.setOnItemClickListener(this);

        createNotificationCH();

        rootPath = new File(this.getExternalFilesDir("/"), "myRemindersRECOREDS");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        dataFile = null;
        mediaPlayer = new MediaPlayer();

        creationMode = isRecording = false;
        toDisplay = true;
        currentDate = new Date();
        selectedDate = new Date();

        remindersKeyList = new ArrayList<>();
        remindersTitleList = new ArrayList<>();
        remindersAudioContentList = new ArrayList<>();
        remindersContextList = new ArrayList<>();
        remindersAudioContentList = new ArrayList<>();
        remindersLastDateToRemindList = new ArrayList<>();
        isTextList = new ArrayList<>();

        readAllRemainders();
        Intent gi = getIntent();
        if (gi.getBooleanExtra("playFromNotification",false)){
            playARecordInAD(gi.getStringExtra("audioContent"),gi.getStringExtra("ContentTitle"));
        }
    }

    private void createNotificationCH() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminders Channel";
            String description = "Reminders for the business";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("remindersChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        // according to the boolean
        if (!creationMode) {
            super.onBackPressed();
        }
    }

    /**
     * Move to previous act.
     *
     * @param view the view
     */
    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    /**
     * Logout.
     *
     * @param item the item
     */
    @SuppressLint("SetTextI18n")
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
                Intent si = new Intent(remindersActivity.this, LoginActivity.class);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsAct){
            startActivity(new Intent(this, settingsActivity.class));
        }
        else if (id == R.id.remainder){
            startActivity(new Intent(this, remindersActivity.class));
        }
        else if (id == R.id.events){
            startActivity(new Intent(this,eventsActivity.class));
        }
        else if (id == R.id.missions){
            startActivity(new Intent(this, newMissionActivity.class));
        }
        else{
            return false;
        }

        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!isTextList.get(i)){
            methodsOnItemAD(true, i);
        } else {
            methodsOnItemAD(false, i);
        }
    }

    /**
     * Open alert dialog selection.
     *
     * @param view the view
     */
    public void openAlertDialogSelection(View view) {
        LinearLayout adScreenMain = new LinearLayout(this);
        adScreenMain.setOrientation(LinearLayout.VERTICAL);
        final TextView title = new TextView(this);
        title.setText("בחר את סוג התזכורת");
        title.setTextSize(30);
        title.setPadding(0,15,30,15);
        title.setTypeface(ResourcesCompat.getFont(title.getContext(), R.font.rubik_semibold));
        adScreenMain.addView(title);

        LinearLayout adScreen = new LinearLayout(this);
        adScreen.setOrientation(LinearLayout.HORIZONTAL);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        AlertDialog ad;
        final ImageView textIV = new ImageView(this);
        final ImageView recordIV = new ImageView(this);

        textIV.setImageResource(R.drawable.ic_text_sign);
        recordIV.setImageResource(R.drawable.ic_mic);
        DrawableCompat.setTint(textIV.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.orange_700));
        DrawableCompat.setTint(recordIV.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.orange_700));

        textIV.setMinimumWidth(500);
        textIV.setMinimumHeight(500);
        recordIV.setMinimumWidth(500);
        recordIV.setMinimumHeight(500);

        adScreen.addView(textIV);
        adScreen.addView(recordIV);
        adScreenMain.addView(adScreen);
        adb.setView(adScreenMain);
        adb.setCancelable(false);

        ad = adb.create();
        creationMode = true;
        textIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTextNewReminder = true;
                setContentView(R.layout.text_layout_reminder);
                ad.dismiss();
            }
        });

        recordIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTextNewReminder = false;
                setContentView(R.layout.record_layout_reminder);
                ad.dismiss();
            }
        });
        ad.show();

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            ActivityCompat.requestPermissions(this,new String[]{recordPermission}, PERMISSION_CODE);
            Snackbar.make(recordLayout,"אין הרשאת מיקרופון", 1000).show();
            return false;
        }
    }

    /**
     * methodsOnItemAD according to its staus
     *
     * @param isRecordItem its status
     * @param pos the index in the list
     * */
    private void methodsOnItemAD(boolean isRecordItem, int pos) {
        LinearLayout adScreen = new LinearLayout(this);
        adScreen.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        AlertDialog ad;

        final TextView titleTV = new TextView(this);
        titleTV.setText(remindersTitleList.get(pos));
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));

        final TextView playTV = new TextView(this);
        final TextView removeTV = new TextView(this);
        removeTV.setText("הסר תזכורת");
        removeTV.setTextSize(18);
        removeTV.setGravity(Gravity.RIGHT);
        removeTV.setPadding(0,15,30,0);
        removeTV.setTextColor(Color.BLACK);
        removeTV.setTypeface(ResourcesCompat.getFont(removeTV.getContext(), R.font.rubik_medium));

        adScreen.addView(titleTV);
        adScreen.addView(removeTV);

        if(isRecordItem) {
            playTV.setText("נגן הקלטה");
            playTV.setTextSize(18);
            playTV.setGravity(Gravity.RIGHT);
            playTV.setPadding(0,15,30,30);
            playTV.setTextColor(Color.BLACK);
            playTV.setTypeface(ResourcesCompat.getFont(playTV.getContext(), R.font.rubik_medium));
            adScreen.addView(playTV);
        }

        adb.setView(adScreen);

        ad = adb.create();
        removeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refReminders.child(remindersKeyList.get(pos)).removeValue();
                // Remove from the FireBase Storage
                if (isRecordItem){
                    removeTheRecord(remindersAudioContentList.get(pos));
                }
                remindersTitleList.remove(pos);
                remindersKeyList.remove(pos);
                remindersContextList.remove(pos);
                remindersLastDateToRemindList.remove(pos);
                isTextList.remove(pos);
                customAdapterReminder.notifyDataSetChanged();
                remaindersLV.setAdapter(customAdapterReminder);

                ad.dismiss();
            }
        });

        playTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readAllRemainders();

                playARecordInAD(remindersAudioContentList.get(pos), remindersTitleList.get(pos));

                ad.dismiss();
            }
        });
        ad.show();
    }

    /**
     * downLoadFile from the storage
     *
     * @param context tof the activity
     * @param uri the object that tranforms the data
     * @param fileUrl the Path of the file
     * @param destinationDirectory the destinationDirectory for the file
     */
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
     * Save the recording reminder AlertDialog.
     *
     * @param tempUrl the paht of the recordings
     * @param title the title to display
     */
    @SuppressLint("SetTextI18n")
    private void playARecordInAD(String tempUrl, String title) {
        LinearLayout adScreen = new LinearLayout(this);
        adScreen.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("מנגן את "+ title);
        titleTV.setTextSize(20);
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setPadding(0,15,30,20);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        adScreen.addView(titleTV);
        final SeekBar seekbar1 = new SeekBar(this);

        mediaPlayer = new MediaPlayer();
        // get the file with the url
        tempUrl = tempUrl.substring(19); // Subtracted the unwanted parts from the url - SAVE only the file's name
        StorageReference filePath = remindersRef.child(tempUrl);
        String finalTempUrl = tempUrl;
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    mediaPlayer.setDataSource(downLoadFile(remindersActivity.this, uri, finalTempUrl, DIRECTORY_DOWNLOADS).getAbsolutePath());
                    mediaPlayer.prepare();
                    seekbar1.setMax(mediaPlayer.getDuration() / 1000);
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        adScreen.addView(seekbar1);
        adb.setView(adScreen);
        Handler mHandler = new Handler();
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekbar1.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress() * 1000);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });

        adb.setNegativeButton("עצור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                stopAudio();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }


    /**
     * Gets and Display all the Remainders form FB.
     *
     *
     */
    private void readAllRemainders() {
        Query query = refReminders.orderByChild("lastDateToRemind");
        currentDate = new Date();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                remindersKeyList.clear();
                remindersAudioContentList.clear();
                remindersTitleList.clear();
                remindersContextList.clear();
                remindersAudioContentList.clear();
                isTextList.clear();
                remindersLastDateToRemindList.clear();

                Reminder temp;
                for (DataSnapshot data : dS.getChildren()) {
                    temp = data.getValue(Reminder.class);

                    // Cast from String to Date
                    DateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);
                    Date selectedDate = null;
                    try {
                        selectedDate = format.parse(Objects.requireNonNull(temp).getLastDateToRemind());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (Objects.requireNonNull(selectedDate).compareTo(currentDate) > 0) {
                        remindersKeyList.add(data.getKey());
                        remindersTitleList.add(temp.getTitle());
                        remindersContextList.add(temp.getTextContent());
                        remindersAudioContentList.add(temp.getAudioContent());
                        isTextList.add(temp.isText());
                        remindersLastDateToRemindList.add(temp.getLastDateToRemind());
                    } else {
                        refReminders.child(Objects.requireNonNull(data.getKey())).removeValue();
                    }
                }
                index = remindersAudioContentList.size() - 1;
                customAdapterReminder = new CustomAdapterReminder(getApplicationContext(), remindersTitleList, remindersContextList, remindersAudioContentList, isTextList, remindersLastDateToRemindList);
                remaindersLV.setAdapter(customAdapterReminder);

                if (remindersTitleList.isEmpty()){;
                    statusRemindersTV.setText("אין תזכורות במערכת");
                    statusRemindersTV.setTextColor(Color.rgb(178, 34,34));
                }
                //publishRemainders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Save the recording reminder AlertDialog.
     *
     * @param view the view
     */
    @SuppressLint("SetTextI18n")
    public void saveTheRecordingReminderAD(View view) {
        Reminder tempReminder = new Reminder();
        tempReminder.setText(false);

        LinearLayout adScreen = new LinearLayout(this);
        adScreen.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        AlertDialog ad;

        final TextView message = new TextView(this);
        message.setMinimumWidth(200);
        message.setMinimumHeight(100);
        message.setTextColor(Color.parseColor("#fe991d"));
        message.setText("שמירת התזכורת במערכת");
        message.setTextSize(25);
        message.setPadding(0,15,30,5);
        message.setTypeface(ResourcesCompat.getFont(message.getContext(), R.font.rubik_bold));
        adScreen.addView(message);
        final EditText titleET = new EditText(this);
        titleET.setInputType(InputType.TYPE_CLASS_TEXT);
        titleET.setHint("כותרת התזכורת");
        adScreen.addView(titleET);

        LinearLayout adDate = new LinearLayout(this);
        adDate.setOrientation(LinearLayout.HORIZONTAL);
        adDate.setGravity(Gravity.RIGHT);
        final ImageView dateIV = new ImageView(this);
        dateIV.setImageResource(R.drawable.ic_event);
        dateIV.setMinimumWidth(150);
        dateIV.setMinimumHeight(75);

        final TextView dateTV = new TextView(this);
        dateTV.setText("התאריך האחרון לתזכורת");
        dateTV.setTextSize(16);
        dateTV.setGravity(Gravity.CENTER);
        dateTV.setTypeface(ResourcesCompat.getFont(message.getContext(), R.font.rubik_medium));

        adDate.addView(dateTV);
        adDate.addView(dateIV);

        adScreen.addView(adDate);

        final TextView noteTV = new TextView(this);
        noteTV.setText("הערה: התזכורת תוצג פעם אחת ביום בשעה 8:00 עד לתאריך שנבחר");
        noteTV.setPadding(0,15,30,0);
        dateTV.setTypeface(ResourcesCompat.getFont(message.getContext(), R.font.rubik_regular));
        noteTV.setTextColor(Color.parseColor("#B22222"));
        noteTV.setGravity(Gravity.RIGHT);
        noteTV.setTextSize(10);

        adScreen.addView(noteTV);
        adb.setView(adScreen);

        adb.setPositiveButton("שמור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recordLayout = findViewById(R.id.recordLayout);

                String reminderTitle = titleET.getText().toString();
                if (reminderTitle.isEmpty()){
                    Snackbar.make(recordLayout,"הכותרת לא תהיה ריקה", 1000).show();
                }
                else if (reminderTitle.matches("[0-9]+")){
                    Snackbar.make(recordLayout,"הכותרת לא תתחיל עם מספרים", 1000).show();
                }
                else {
                    tempReminder.setTitle(reminderTitle);
                    if (selectedDate.equals(new Date())){
                        Snackbar.make(recordLayout,"נא לבחור תאריך", 1000).show();
                    }
                    else {
                        // Cast the selected Date to String
                        String childID = DateConvertor.dateToString(selectedDate, "yyyyMMddHHmm");
                        childID += DateConvertor.dateToString(currentDate, "yyyyMMddHHmmss");
                        String strDate = DateConvertor.dateToString(selectedDate, "yyyyMMddHHmm");
                        tempReminder.setLastDateToRemind(strDate);
                        tempReminder.setTextContent("<קטע קול>");
                        Snackbar.make(recordLayout,"התזכורת נשמרה", 1000).show();
                        // TODO: Notification
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selectedDate.getTime());
                        calendar.set(Calendar.HOUR_OF_DAY, 8);;
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND,0);
                        if (selectedDate.getHours() >=8){
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        readAllRemainders();

                        if (mediaSaverFile != null){
                            String urlOfRecord = uploadTheRecording();
                            tempReminder.setAudioContent(urlOfRecord);

                            refReminders.child(childID).setValue(tempReminder);

                            remindersKeyList.add(strDate);
                            remindersTitleList.add((tempReminder).getTitle());
                            remindersContextList.add(tempReminder.getTextContent());
                            remindersAudioContentList.add(tempReminder.getAudioContent());
                            isTextList.add(tempReminder.isText());
                            remindersLastDateToRemindList.add(tempReminder.getLastDateToRemind());

                            // TODO: Notification
                            Intent intentToBrod = new Intent(remindersActivity.this, notificationPublisher.class);
                            intentToBrod.putExtra("SubText", tempReminder.getTextContent());
                            intentToBrod.putExtra("Title", tempReminder.getTitle());
                            intentToBrod.putExtra("Content", "תזכורת לעסק | ");
                            intentToBrod.putExtra("channel", "remindersChannel");
                            intentToBrod.putExtra("audioContent", tempReminder.getAudioContent());
                            intentToBrod.putExtra("index", remindersKeyList.get(remindersKeyList.indexOf(strDate)));

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(remindersActivity.this,200, intentToBrod,PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                            readAllRemainders();
                        }
                        else{
                            Snackbar.make(recordLayout,"אין הקלטה לשמירה. הקלט כדי לשמור", 1000).show();
                        }
                    }
                }
            }
        });

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        ad = adb.create();


        dateIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(remindersActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month++, dayOfMonth,0,0,0);
                        selectedDate = new Date(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),8,0,0);
                        if (selectedDate.after(new Date())){
                            String strDate = DateConvertor.dateToString(selectedDate, "dd/MM/yyyy");
                            dateTV.setText(strDate);
                        } else{
                            Snackbar.make(dateTV,"תאריך לא רלוונטי", 1000).show();
                        }
                    }
                }, year, month, day);

                dpd.show();
            }
        });

        ad.show();
    }

    private String uploadTheRecording() {
        String url;
        Uri file = Uri.fromFile(mediaSaverFile);
        StorageReference recordRef = remindersRef.child(file.getLastPathSegment());
        recordRef.putFile(file);
        url = recordRef.getPath();
        return url;
    }

    private void removeTheRecord(String fileUrl) {
        StorageReference deleteFile = storageRef.child(fileUrl);
        deleteFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(reminderLayout,"התזכורת נמחקה", 1000).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(reminderLayout,"מחיקה נכשלה: "+e.getMessage(), 1000).show();
            }
        });
    }

    /**
     * Open date picker.
     *
     * @param view the button of date
     */
// The section of the text reminder
    public void openDatePicker(View view){
        selectedDateTV = findViewById(R.id.selectedDateTV);

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
                selectedDate = new Date(c.get(Calendar.YEAR) -1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),8,0,0);
                if (selectedDate.after(new Date())){
                    String strDate = DateConvertor.dateToString(selectedDate, "dd/MM/yyyy");
                    selectedDateTV.setText(strDate);
                } else{
                    Snackbar.make(selectedDateTV,"תאריך לא רלוונטי", 3000).show();
                }
            }
        }, year, month, day);

        final TextView titleTV = new TextView(this);
        titleTV.setText("תאריך אחרון לתזכורת");
        titleTV.setTextColor(Color.WHITE);
        titleTV.setBackgroundResource(R.color.orange_500);
        titleTV.setPadding(0,10,10,0);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.rubik_semibold);
        titleTV.setTextSize(25);
        titleTV.setTypeface(typeface);

        dpd.setCustomTitle(titleTV);
        dpd.show();
    }

    /**
     * Start record.
     *
     * @param view the button
     */
// The section of startRecording methods
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    public void startRecord(View view) {
        setContentView(R.layout.record_layout_reminder);
        recordIV = findViewById(R.id.recordIV);
        if (isRecording){
            // stop recording

            stopRecording();
            isRecording = false;
            recordIV.setImageResource(R.drawable.ic_mic_off);
        }
        else{
            // Start recording
            recordIV.setImageResource(R.drawable.ic_mic);
            isRecording = true;

            if (checkPermissions()){
                startRecording();
                isRecording = true;
            }

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run(){
                    recordIV.setImageResource(R.drawable.ic_mic);
                }
            };
            handler.postDelayed(r,2000);
        }
    }

    /**
     * startRecording and setting the layout
     *
     *
     */
    private void startRecording() {
        recordLayout = findViewById(R.id.recordLayout);
        Snackbar.make(recordLayout,"מקליט..", 1000).show();
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        fileName = String.valueOf(System.currentTimeMillis());
        dataFile = new File(rootPath, fileName+".3gp");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(dataFile);
            mediaSaverFile = new File(rootPath, fileName+".3gp");
            mediaRecorder.setOutputFile(mediaSaverFile);
        }

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopRecording() {
        recordLayout = findViewById(R.id.recordLayout);
        Snackbar.make(recordLayout,"הקלטה הופסקה", 1000).show();
        mediaRecorder.stop();
        mediaRecorder.release();

        mediaRecorder = null;
    }

    /**
     * Play the record.
     *
     * @param view the view
     */
// The section of PlayRecording methods
    public void playTheRecord(View view){
        setContentView(R.layout.record_layout_reminder);
        if (!isRecording){
            playIV = findViewById(R.id.playIV);
            if (isPlaying){
                stopAudio();
                isPlaying = false;
            } else{
                playIV.setImageResource(R.drawable.ic_play_arrow);
                isPlaying = true;
                playAudio();
            }
            seekBar = findViewById(R.id.seekBar);
            seekBar.setMax(fileDuration/1000);
            Handler mHandler = new Handler();
            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(mediaPlayer != null && fromUser){
                        mediaPlayer.seekTo(progress * 1000);
                    }
                }
            });
        }
        else{
            recordLayout = findViewById(R.id.recordLayout);
            Snackbar.make(recordLayout,"לא ניתן לשמוע במהלך הקלטה", 1000).show();
        }
    }

    private void playAudio() {
        if (dataFile != null) {
            mediaPlayer = new MediaPlayer();
            try {
                playIV.setImageResource(R.drawable.ic_pause);
                mediaPlayer.setDataSource(dataFile.getAbsolutePath());
                mediaPlayer.prepare();
                fileDuration = mediaPlayer.getDuration();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            isPlaying = true;
        }
        else{
            if (creationMode){
                setContentView(R.layout.record_layout_reminder);
                recordLayout = findViewById(R.id.recordLayout);
                Snackbar.make(recordLayout,"אין תוכן להשמעה. הקלט כדי לשמוע", 1000).show();
            }
        }
    }

    private void stopAudio() {
        mediaPlayer.stop();
        isPlaying = false;
    }

    /**
     * Save the text reminder.
     *
     * @param view the view
     */
    public void saveTheTextReminder(View view) {
        textLayout = findViewById(R.id.textLayout);
        titleReminderET = findViewById(R.id.titleReminderET);
        contextReminderET = findViewById(R.id.contextMissionET);

        String tempTitle = titleReminderET.getText().toString();
        String tempContext = contextReminderET.getText().toString();

        currentDate = new Date();
        if (tempTitle.isEmpty() || tempContext.isEmpty() || selectedDateTV.getText().toString().isEmpty()){
            Snackbar.make(textLayout,"נא למלא את כל השדות", 1000).show();
        }
        else if (tempTitle.matches("[0-9]+") || tempContext.matches("[0-9]+")){
            Snackbar.make(textLayout,"השדה לא יתחיל עם ספרות", 1000).show();
        }
        else{
            // Cast the selected Date to String
            String childID = DateConvertor.dateToString(selectedDate, "yyyyMMddHHmm");
            String strDate = DateConvertor.dateToString(currentDate, "yyyyMMddHHmmss");
            Reminder tempReminder = new Reminder(tempTitle, true, tempContext,"",strDate);

            readAllRemainders();

            refReminders.child(childID).setValue(tempReminder);

            remindersKeyList.add(strDate);
            remindersTitleList.add((tempReminder).getTitle());
            remindersContextList.add(tempReminder.getTextContent());
            remindersAudioContentList.add(tempReminder.getAudioContent());
            isTextList.add(tempReminder.isText());
            remindersLastDateToRemindList.add(tempReminder.getLastDateToRemind());
            // Clean the layout
            titleReminderET.setText("");
            contextReminderET.setText("");
            selectedDateTV.setText("dd/mm/yyyy");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selectedDate.getTime());
            if (currentDate.getHours() >= 8){
                Snackbar.make(textLayout,"התזכורת נשמרה ותחל ממחר", 1000).show();
            } else Snackbar.make(textLayout,"התזכורת נשמרה ותחל מהיום", 1000).show();

            Intent intentToBrod = new Intent(remindersActivity.this, notificationPublisher.class);
            intentToBrod.putExtra("SubText", "תזכורת לעסק |");
            intentToBrod.putExtra("ContentTitle", tempReminder.getTitle());
            intentToBrod.putExtra("ContentText", tempReminder.getTextContent());
            intentToBrod.putExtra("audioContent", tempReminder.getAudioContent());
            intentToBrod.putExtra("index", remindersKeyList.get(remindersKeyList.indexOf(strDate)));
            intentToBrod.putExtra("channel", "remindersChannel");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(remindersActivity.this,200, intentToBrod,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            readAllRemainders();
        }

    }


    /**
     * Move to main of reminder activity (Where the all reminders).
     *
     * @param view the view
     */
    public void moveToMainOfReminder(View view){
        setContentView(R.layout.activity_reminders);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        remaindersLV = findViewById(R.id.remaindersLV);
        bottomNavigationView.setSelectedItemId(R.id.remainder); // set the selection of the bottomNavigationView object to the current activity

        readAllRemainders();
        creationMode = false;
        remaindersLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        remaindersLV.setOnItemClickListener(this);
    }
}