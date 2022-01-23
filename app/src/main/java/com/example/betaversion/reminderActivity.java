package com.example.betaversion;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.example.betaversion.FBref.refReminders;
import static com.example.betaversion.FBref.reflive_Event;
import static com.example.betaversion.FBref.storage;
import static com.example.betaversion.FBref.storageRef;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DirectAction;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.ContextMenu;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	2.0
 * * @since		24/12/2021
 *
 * * This reminderActivity.class displays and creates all the reminds for the business.
 */
public class reminderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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
    TextView selectedDateTV;
    EditText titleReminderET, contextReminderET;

    ListView remaindersLV;
    ArrayList<String> remindersTitleList, remindersContextList, remindersAudioContentList;
    ArrayList<Date> remindersLastDateToRemindList;
    ArrayList<Boolean> isTextList;
    CustomAdapterReminder customAdapterReminder;

    private static final int PERMISSION_CODE = 12;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        recordLayout = findViewById(R.id.recordLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textLayout = findViewById(R.id.textLayout);
        reminderLayout = findViewById(R.id.reminderLayout);
        remaindersLV = findViewById(R.id.remaindersLV);

        remaindersLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        remaindersLV.setOnItemClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        // Creates a channel of notification in the system
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("remainderCH", "AfikForReminders", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        rootPath = new File(this.getExternalFilesDir("/"), "myRECORDS");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        dataFile = null;
        mediaPlayer = new MediaPlayer();

        Intent gi = getIntent();
//        if (gi != null){
//            if(gi.getStringExtra("audioContent").isEmpty()) {
//                Toast.makeText(this, getIntent().getStringExtra("audioContent"), Toast.LENGTH_SHORT).show();
//            }
//        }


        bottomNavigationView.setSelectedItemId(R.id.remainder); // set the selection of the bottomNavigationView object to the current activity
        creationMode = isRecording = false;
        toDisplay = true;
        currentDate = new Date();
        selectedDate = new Date();

        remindersTitleList = new ArrayList<>();
        remindersAudioContentList = new ArrayList<>();
        remindersContextList = new ArrayList<>();
        remindersAudioContentList = new ArrayList<>();
        remindersLastDateToRemindList = new ArrayList<>();
        isTextList = new ArrayList<>();

        readAllRemainders();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!isTextList.get(i)){
            methodsOnItemAD(true, i);
        } else {
            methodsOnItemAD(false, i);
        }
    }

    private void methodsOnItemAD(boolean isRecordItem, int pos) {
        LinearLayout adScreen = new LinearLayout(this);
        adScreen.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        AlertDialog ad;

        final TextView titleTV = new TextView(this);
        titleTV.setText(remindersTitleList.get(pos));
        titleTV.setTextSize(32);
        titleTV.setPadding(0,15,30,20);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_bold));

        final TextView playTV = new TextView(this);
        final TextView removeTV = new TextView(this);
        removeTV.setText("הסר תזכורת");
        removeTV.setTextColor(Color.BLACK);
        removeTV.setPadding(0,15,30,10);
        removeTV.setTypeface(ResourcesCompat.getFont(removeTV.getContext(), R.font.rubik_medium));
        removeTV.setTextSize(24);

        adScreen.addView(titleTV);
        adScreen.addView(removeTV);

        if(isRecordItem) {
            playTV.setText("נגן הקלטה");
            playTV.setTextColor(Color.BLACK);
            playTV.setPadding(0,10,30,25);
            playTV.setTypeface(ResourcesCompat.getFont(playTV.getContext(), R.font.rubik_medium));
            playTV.setTextSize(24);
            adScreen.addView(playTV);
        }



        adb.setView(adScreen);

        ad = adb.create();
        removeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reminderID = String.valueOf(remindersLastDateToRemindList.get(pos).getDate()*remindersLastDateToRemindList.get(pos).getYear()*remindersLastDateToRemindList.get(pos).getMonth()*remindersTitleList.get(pos).length());
                refReminders.child(reminderID).removeValue();
                // Remove from the FireBase Storage
                removeTheRecord(remindersTitleList.get(pos));
                remindersTitleList.remove(pos);
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

                playARecordInAD(remindersAudioContentList.get(pos).substring(9,remindersAudioContentList.get(pos).length()), remindersTitleList.get(pos));

                ad.dismiss();
            }
        });
        ad.show();
    }

    @SuppressLint("SetTextI18n")
    private void playARecordInAD(String tempUrl, String title) {
        LinearLayout adScreen = new LinearLayout(this);
        adScreen.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("מנגן את "+ title);
        titleTV.setTextSize(20);
        titleTV.setPadding(0,15,30,20);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        adScreen.addView(titleTV);
        final SeekBar seekbar1 = new SeekBar(this);

        mediaPlayer = new MediaPlayer();
        // get the file with the url

        StorageReference filePath = storageRef.child("/records").child(tempUrl);
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    mediaPlayer.setDataSource(downLoadFile(reminderActivity.this, uri, tempUrl, DIRECTORY_DOWNLOADS).getAbsolutePath());
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

    private void readAllRemainders() {
        Query query = refReminders.orderByChild("LastDateToRemind");
        currentDate = new Date();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                remindersAudioContentList.clear();
                remindersTitleList.clear();
                remindersContextList.clear();
                remindersAudioContentList.clear();
                isTextList.clear();
                remindersLastDateToRemindList.clear();

                Reminder temp;
                for(DataSnapshot data : dS.getChildren()) {
                    temp = data.getValue(Reminder.class);

                    if (Objects.requireNonNull(temp).getLastDateToRemind().compareTo(currentDate) > 0){
                        remindersTitleList.add(temp.getTitle());
                        remindersContextList.add(temp.getTextContent());
                        remindersAudioContentList.add(temp.getAudioContent());
                        isTextList.add(temp.isText());
                        remindersLastDateToRemindList.add(temp.getLastDateToRemind());
                    }
                    else{
                        refReminders.child(Objects.requireNonNull(data.getKey())).removeValue();
                    }
                }
                index = remindersAudioContentList.size()-1;
                customAdapterReminder = new CustomAdapterReminder(getApplicationContext(), remindersTitleList, remindersContextList, remindersAudioContentList, isTextList, remindersLastDateToRemindList);
                remaindersLV.setAdapter(customAdapterReminder);

                displayRemainders();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayRemainders() {
        for (int i = 0; i < remindersLastDateToRemindList.size(); i++) {
            Intent notificationIntent = new Intent(reminderActivity.this, notificationPublisher.class) ;

            notificationIntent.putExtra("Content",remindersContextList.get(i)) ;
            notificationIntent.putExtra("SubText","(קיים עד "+ remindersLastDateToRemindList.get(i).getDate()+"/"+ remindersLastDateToRemindList.get(i).getMonth()+"/"+ remindersLastDateToRemindList.get(i).getYear()+")");
            notificationIntent.putExtra("Title", remindersTitleList.get(i));
            notificationIntent.putExtra("audioContent",remindersAudioContentList.get(i));
            notificationIntent.putExtra("index",i);


            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            c.set(Calendar.HOUR_OF_DAY, 8);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(reminderActivity.this,i ,notificationIntent, 0) ;
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE) ;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            // String reminderID = String.valueOf(remindersLastDateToRemindList.get(pos).getDate()+remindersLastDateToRemindList.get(pos).getYear()*remindersLastDateToRemindList.get(pos).getMonth()*remindersTitleList.get(pos).length());
            //
            //            refReminders.child(reminderID).removeValue();
            //            remindersTitleList.remove(pos);
            //            remindersContextList.remove(pos);
            //            remindersLastDateToRemindList.remove(pos);
            //            isTextList.remove(pos);
            //            customAdapterReminder.notifyDataSetChanged();
            //            remaindersLV.setAdapter(customAdapterReminder);
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            ActivityCompat.requestPermissions(this,new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        // according to the boolean
        if (!creationMode) {
            super.onBackPressed();
        }
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
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
                Intent si = new Intent(reminderActivity.this, LoginActivity.class);

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

    public void openAlertDialogSelection(View view) {
        LinearLayout adScreenMain = new LinearLayout(this);
        adScreenMain.setOrientation(LinearLayout.VERTICAL);
        final TextView title = new TextView(this);
        title.setText("בחר את סוג התזכורת");
        title.setTextSize(30);
        title.setPadding(0,15,30,15);
        title.setTypeface(ResourcesCompat.getFont(title.getContext(), R.font.rubik_bold));
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

    public void moveToMainOfReminder(View view){
        setContentView(R.layout.activity_reminder);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        remaindersLV = findViewById(R.id.remaindersLV);
        bottomNavigationView.setSelectedItemId(R.id.remainder); // set the selection of the bottomNavigationView object to the current activity

        readAllRemainders();
        creationMode = false;
        remaindersLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        remaindersLV.setOnItemClickListener(this);
    }

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
        dateTV.setTextSize(17);
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
//                    else if (currentDate.compareTo(selectedDate) < 0){
//                        Snackbar.make(recordLayout,"התאריך עבר", 1000).show();
//                    }
                    else {
                        tempReminder.setLastDateToRemind(selectedDate);
                        tempReminder.setTextContent("<קטע קול>");
                        Snackbar.make(recordLayout,"התזכורת נשמרה", 1000).show();

                        readAllRemainders();

                        if (mediaSaverFile != null){

                            String reminderID = String.valueOf(selectedDate.getDate()*selectedDate.getYear()*selectedDate.getMonth()*tempReminder.getTitle().length());

                            String urlOfRecord = uploadTheRecording();
                            tempReminder.setAudioContent(urlOfRecord);

                            refReminders.child(reminderID).setValue(tempReminder);

                            remindersTitleList.add((tempReminder).getTitle());
                            remindersContextList.add(tempReminder.getTextContent());
                            remindersAudioContentList.add(tempReminder.getAudioContent());
                            isTextList.add(tempReminder.isText());
                            remindersLastDateToRemindList.add(tempReminder.getLastDateToRemind());
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

                DatePickerDialog dpd = new DatePickerDialog(reminderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month ++;
                        selectedDate = new Date(year,month, dayOfMonth,0,0);
                        dateTV.setText(selectedDate.getDate()+"/"+ selectedDate.getMonth()+"/"+ selectedDate.getYear());
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
        StorageReference recordRef = storageRef.child("records/"+file.getLastPathSegment());
        recordRef.putFile(file);
        url = recordRef.getPath();
        return url;
    }

    private void removeTheRecord(String titleToSearch) {
        StorageReference deleteFile = storageRef.child("records/"+titleToSearch+".3gp");
        deleteFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(reminderLayout,"התזכורת נמחקה", 1000).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(reminderLayout,"מחיקה נכשלה", 1000).show();
                Toast.makeText(reminderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                month ++;
                selectedDate = new Date(year,month, dayOfMonth,0,0);
                selectedDateTV.setText(selectedDate.getDate()+"/"+selectedDate.getMonth()+"/"+selectedDate.getYear());

            }
        }, year, month, day);

        dpd.show();
    }

    public void saveTheTextReminder(View view) {
        textLayout = findViewById(R.id.textLayout);
        titleReminderET = findViewById(R.id.titleReminderET);
        contextReminderET = findViewById(R.id.contextReminderET);

        String tempTitle = titleReminderET.getText().toString();
        String tempContext = contextReminderET.getText().toString();


        currentDate = new Date();
        if (tempTitle.isEmpty() || tempContext.isEmpty() || selectedDateTV.getText().toString().isEmpty()){
            Snackbar.make(textLayout,"נא למלא את כל השדות", 1000).show();
        }
        else if (currentDate.after(selectedDate)){
            Snackbar.make(textLayout,"התאריך עבר", 1000).show();
        }
        else if (tempTitle.matches("[0-9]+") || tempContext.matches("[0-9]+")){
            Snackbar.make(textLayout,"השדה לא יתחיל עם ספרות", 1000).show();
        }
        else{
           Reminder tempReminder = new Reminder(tempTitle, true, tempContext," ",selectedDate);

            readAllRemainders();

            String reminderID = String.valueOf(selectedDate.getDate()*selectedDate.getYear()*selectedDate.getMonth()*tempReminder.getTitle().length());
            refReminders.child(reminderID).setValue(tempReminder);

            remindersTitleList.add((tempReminder).getTitle());
            remindersContextList.add(tempReminder.getTextContent());
            remindersAudioContentList.add(tempReminder.getAudioContent());
            isTextList.add(tempReminder.isText());
            remindersLastDateToRemindList.add(tempReminder.getLastDateToRemind());

            titleReminderET.setText("");
            contextReminderET.setText("");
            selectedDateTV.setText("");

            Snackbar.make(textLayout,"התזכורת נשמרה", 1000).show();
        }

    }
}