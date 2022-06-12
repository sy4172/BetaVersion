package com.example.betaversion;

import static com.example.betaversion.FBref.refGreen_Event;
import static com.example.betaversion.FBref.storageRef;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * The type New mission activity.
 */
public class newMissionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView eventTitle, finalDateTV, dateForNotificationTV;
    EditText titleMissionET, contextMissionET;
    Spinner frequencySpinner;

    ImageView recordIV, playIV;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    File mediaSaverFile, rootPath, dataFile;
    String fileName;

    int fileDuration, index;
    SeekBar seekBar;

    boolean isText,updateMode, isRecording, isPlaying, creationMode;
    String eventID, line1, line2, title, context, dateOfChange, finalDateStr, eventDateStr;
    String [] frequencyOptions;
    int frequency;
    Date selectedDate;

    Mission tempMission;
    HashMap <String, Mission> allMissions;

    private static final int PERMISSION_CODE = 12;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mission);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        eventTitle = findViewById(R.id.eventTitle);

        Intent gi = getIntent();

        line1 = "הגדרת משימות לאירוע";
        line2 = gi.getStringExtra("eventTitle");
        eventID = gi.getStringExtra("eventID");
        eventDateStr = gi.getStringExtra("eventID");
        eventTitle.setText(line1 + System.lineSeparator() + line2);

        isText = gi.getBooleanExtra("missionMode",false);
        updateMode = gi.getBooleanExtra("updateMode", false); // TRUE for editing. Otherwise, FALSE

        allMissions = new HashMap<>();
        tempMission = new Mission();
        frequencyOptions = new String []{"1","2","3","4"};
        frequency = 0;

        rootPath = new File(this.getExternalFilesDir("/"), "myMissionsRECOREDS");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        dataFile = null;
        isPlaying = true;
        mediaPlayer = new MediaPlayer();

        checkPermissions(recordPermission);
        checkPermissions(storagePermission);
        createChannel();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, frequencyOptions);

        if (isText && !updateMode){
            setContentView(R.layout.text_layout_mission);
            dateForNotificationTV = findViewById(R.id.dateForNotificationTV);
            eventTitle = findViewById(R.id.eventTitle);
            finalDateTV = findViewById(R.id.finalDateTV);
            titleMissionET = findViewById(R.id.titleMissionET);
            contextMissionET = findViewById(R.id.contextMissionET);
            frequencySpinner = findViewById(R.id.frequencySpinner);

            titleMissionET.setEnabled(false);
            contextMissionET.setEnabled(false);
            frequencySpinner.setEnabled(false);

            tempMission = gi.getParcelableExtra("mission");
            line1 = "צפייה במשימה לאירוע";
            eventTitle.setText(line1 + System.lineSeparator() + line2);
            titleMissionET.setText(tempMission.getTitle());
            contextMissionET.setText(tempMission.getTextContent());
            frequencySpinner.setAdapter(adp);
            frequencySpinner.setSelection(tempMission.getFrequency()-1); // To calibrate between the user selection the frequencies list. (The list starts with 0).
            finalDateStr = tempMission.getLastDateToRemind();
            DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            Date tempDate = null;
            try {
                tempDate = format.parse(finalDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            finalDateTV.setText(DateConvertor.dateToString(tempDate, "dd/MM/yyyy"));

        } else if (!updateMode) {
            setContentView(R.layout.record_layout_mission);
            eventTitle = findViewById(R.id.eventTitle);
            finalDateTV = findViewById(R.id.finalDateTV);
            titleMissionET = findViewById(R.id.titleMissionET);
            contextMissionET = findViewById(R.id.contextMissionET);
            frequencySpinner = findViewById(R.id.frequencySpinner);

            titleMissionET.setEnabled(false);
            contextMissionET.setEnabled(false);
            frequencySpinner.setEnabled(false);
        }
    }

    private void createChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Missions Channel";
            String description = "Missions for the all 'Green' events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("missionsChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Create recorded mission.
     *
     * @param view the button that clicked
     */
    public void createRecordedMission(View view) {
        isText = isRecording = creationMode = false;
        updateMode = true;

        setContentView(R.layout.record_layout_mission);
        eventTitle = findViewById(R.id.eventTitle);
        frequencySpinner = findViewById(R.id.frequencySpinner);

        Intent gi = getIntent();

        line1 = "הגדרת משימה כשמע לאירוע";
        line2 = gi.getStringExtra("eventTitle");
        eventID = gi.getStringExtra("eventID");
        eventTitle.setText(line1 + System.lineSeparator() + line2);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, frequencyOptions);
        frequencySpinner.setAdapter(adp);
    }

    /**
     * Start record process.
     *
     * @param view the button that clicked with the icon
     */
    public void startRecord(View view) {
        setContentView(R.layout.record_layout_mission);
        eventTitle = findViewById(R.id.eventTitle);
        recordIV = findViewById(R.id.recordIV);
        eventTitle = findViewById(R.id.eventTitle);

        eventTitle.setText(line1 + System.lineSeparator() + line2);

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

            if (checkPermissions(recordPermission)){
                Snackbar.make(eventTitle,"מקליט..", 1000).show();
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
            handler.postDelayed(r,3000);
        }
    }

    private boolean checkPermissions(String permissionType) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissionType) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            ActivityCompat.requestPermissions(this,new String[]{permissionType}, PERMISSION_CODE);
            return false;
        }
    }


    /**
     * Start record process. (the actual work)
     *
     */
    private void startRecording() {
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

    private void stopRecording() {
        eventTitle = findViewById(R.id.eventTitle);
        Snackbar.make(eventTitle,"הקלטה הופסקה", 1000).show();
        mediaRecorder.stop();
        mediaRecorder.release();

        mediaRecorder = null;
    }

    /**
     * Play record process.
     *
     * @param view the view
     */
    public void playRecord(View view) {
        if (!isRecording){
            playIV = findViewById(R.id.playIV);
            playIV.setImageResource(R.drawable.ic_pause);
            if (isPlaying){
                stopAudio();
                playIV.setImageResource(R.drawable.ic_play_arrow);
            } else{
                playIV.setImageResource(R.drawable.ic_pause);
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

                        if(mediaPlayer.getCurrentPosition() == fileDuration){
                            playIV.setImageResource(R.drawable.ic_play_arrow);
                        }
                        else {
                            playIV.setImageResource(R.drawable.ic_pause);
                        }                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        playIV.setImageResource(R.drawable.ic_play_arrow);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        playIV.setImageResource(R.drawable.ic_pause);
                    }
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(mediaPlayer != null && fromUser){
                        mediaPlayer.seekTo(progress * 1000);
                        playIV.setImageResource(R.drawable.ic_play_arrow);
                    }
                }
            });
        }
        else{
            eventTitle = findViewById(R.id.eventTitle);
            Snackbar.make(eventTitle,"לא ניתן לשמוע במהלך הקלטה", 1000).show();
        }
    }


    /**
     * play Audio process (actual work).
     *
     */
    private void playAudio() {
        if (dataFile != null) {
            mediaPlayer = new MediaPlayer();
            try {
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
                setContentView(R.layout.record_layout_mission);
                eventTitle = findViewById(R.id.eventTitle);
                Snackbar.make(eventTitle,"אין תוכן להשמעה. הקלט כדי לשמוע", 1000).show();
            }
        }
    }

    private void stopAudio() {
        mediaPlayer.stop();
        isPlaying = false;
        playIV.setImageResource(R.drawable.ic_pause);
    }

    /**
     * Open date picker for getting the date.
     *
     * @param view the view
     */
    public void openDatePicker(View view) {
        if (updateMode){
            selectedDate = new Date();
            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    c.set(year-1900, month++, dayOfMonth,0,0,0);
                    selectedDate = new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),0,0,0);
                    openTimePicker();
                }
            }, year, month, day);

            dpd.show();
        } else {
            Snackbar.make(eventTitle,"לא ניתן במצב צפיה", 3000).show();
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
                        selectedDate.setHours(hour);
                        selectedDate.setMinutes(minute);
                        selectedDate.setSeconds(0);
                        selectedDate.setTime(selectedDate.getTime());

                        Date eventDate = DateConvertor.stringToDate(eventDateStr, "yyyyMMddHHmm"); // Getting the DATE constructor of the eventDate in order to compare.
                        String dateStr;
                        if (selectedDate.after(new Date()) && selectedDate.before(eventDate)){
                            selectedDate.setTime(selectedDate.getTime());
                            dateStr = DateConvertor.dateToString(selectedDate, "dd/MM/yyyy");
                            finalDateTV = findViewById(R.id.finalDateTV);
                            finalDateStr = DateConvertor.dateToString(selectedDate, "yyyyMMddHHmm");;
                            finalDateTV.setText(dateStr);
                            if (isText){
                                dateForNotificationTV.setText(DateConvertor.dateToString(selectedDate, "HH:mm"));
                            } else{
                                dateStr = DateConvertor.dateToString(selectedDate, "dd/MM/yyyy HH:mm");
                                finalDateTV = findViewById(R.id.finalDateTV);
                                finalDateTV.setText(dateStr);
                            }
                        } else {
                            Snackbar.make(eventTitle,"תאריך לא רלוונטי", 3000).show();
                            finalDateTV = findViewById(R.id.finalDateTV);
                            finalDateTV.setText("dd/MM/yyyy");
                        }
                    }
                }, hour, minutes, true);
        picker.show();
    }

    /**
     * Create mission according its status.
     *
     * @param view the view
     */
    public void createMission(View view) {
        if (isText){
            dateForNotificationTV = findViewById(R.id.dateForNotificationTV);
            titleMissionET = findViewById(R.id.titleMissionET);
            contextMissionET = findViewById(R.id.contextMissionET);
            finalDateTV = findViewById(R.id.finalDateTV);

            finalDateTV.setText(DateConvertor.dateToString(selectedDate, "dd/MM/yyyy"));

            if (checkInputs()){
                title = titleMissionET.getText().toString();
                context = contextMissionET.getText().toString();
                dateOfChange = DateConvertor.dateToString(new Date(), "dd/MM/yyyy");

                tempMission = new Mission(title, true, context,"",DateConvertor.dateToString(new Date(), "yyyyMMddHHmmss"), frequency, finalDateStr);
                // Save the tempMission in the current place of the selected event in the RealTime DataBaseFirebase
                String missionID = finalDateStr+DateConvertor.dateToString(new Date(), "yyyyMMddHHmmss");
                allMissions.put(missionID,tempMission);
                refGreen_Event.child(eventID).child("eventMissions").child(missionID).setValue(tempMission);;

                // Create a notification
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selectedDate.getTime());

                Intent intentToBrod = new Intent(newMissionActivity.this, notificationPublisher.class);
                intentToBrod.putExtra("SubText", tempMission.getTextContent());
                intentToBrod.putExtra("Title", tempMission.getTitle());
                intentToBrod.putExtra("Content", "משימות | ");
                intentToBrod.putExtra("channel", "missionsChannel");
                intentToBrod.putExtra("audioContent", tempMission.getAudioContent());
                intentToBrod.putExtra("index", (int) Math.random());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(newMissionActivity.this,200, intentToBrod,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                Snackbar.make(finalDateTV,"המשימה נוצרה בהצלחה", 3000).show();
                titleMissionET.setText("");
                contextMissionET.setText("");
                dateForNotificationTV.setText("HH:mm");
            }

        } else if (!isText && checkPermissions(recordPermission)) {
            titleMissionET = findViewById(R.id.titleMissionET);
            contextMissionET = findViewById(R.id.contextMissionET);
            finalDateTV = findViewById(R.id.finalDateTV);

            finalDateTV.setText("dd/MM/yyyy HH:mm");

            if (checkInputs()){
                title = titleMissionET.getText().toString();
                String dateForNotification = DateConvertor.dateToString(selectedDate, "yyyyMMddHHmmss");

                // Save the recorded mission in the FireBase Storage and gets the url of the location.
                String urlOfRecord = uploadRecordingFB();

                tempMission = new Mission(title, false, "<קטע קול>",urlOfRecord, dateForNotification, frequency, finalDateStr);
                // Save the tempMission in the current place of the selected event in the RealTime DataBaseFirebase
                String currentDateStr = DateConvertor.dateToString(new Date(), "yyyyMMddHHmmss");
                finalDateTV.setText(DateConvertor.dateToString(selectedDate, "dd/MM/yyyy HH:mm"));
                String missionID = finalDateStr+currentDateStr;
                allMissions.put(missionID,tempMission);
                refGreen_Event.child(eventID).child("eventMissions").child(missionID).setValue(tempMission);

                //Create a notification
                Calendar calendar = Calendar.getInstance();
                if (new Date().getTime() > selectedDate.getTime()){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                // According to the amount of the frequency:
                for (int i = 0; i < frequency; i++) {
                    Intent intentToBrod = new Intent(newMissionActivity.this, notificationPublisher.class);
                    intentToBrod.putExtra("SubText", tempMission.getTextContent());
                    intentToBrod.putExtra("Title", tempMission.getTitle());
                    intentToBrod.putExtra("Content", "משימות | ");
                    intentToBrod.putExtra("channel", "missionsChannel");
                    intentToBrod.putExtra("audioContent", tempMission.getAudioContent());
                    intentToBrod.putExtra("index", (int) Math.random());

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(newMissionActivity.this,200, intentToBrod,PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

                Snackbar.make(finalDateTV,"המשימה נוצרה", 3000).show();
                titleMissionET.setText("");
            }
        }
    }

    private String uploadRecordingFB() {
        String url;
        Uri file = Uri.fromFile(mediaSaverFile);
        StorageReference recordRef = storageRef.child("/records/Missions"+file.getLastPathSegment());
        recordRef.putFile(file);
        url = recordRef.getPath();
        return url;
    }

    private boolean checkInputs() {
        titleMissionET = findViewById(R.id.titleMissionET);
        finalDateTV = findViewById(R.id.finalDateTV);
        dateForNotificationTV = findViewById(R.id.dateForNotificationTV);
        finalDateTV = findViewById(R.id.finalDateTV);
        boolean flag = true;

        if (isText){
            contextMissionET = findViewById(R.id.contextMissionET);
            if (contextMissionET.getText().toString().isEmpty() || dateForNotificationTV.getText().toString().equals("HH:mm")){
                flag = false;
                Snackbar.make(finalDateTV, "שדה לא יהיה ריק", 3000).show();
            }
        }

        if (titleMissionET.getText().toString().isEmpty() || finalDateTV.getText().toString().equals("dd/MM/yyyy")){
            flag = false;
            Snackbar.make(finalDateTV, "שדה לא יהיה ריק", 3000).show();
        }
        else if (selectedDate == null){
            flag = false;
            Snackbar.make(finalDateTV, "נא לבחור את תאריך", 3000).show();
        }
        else if (!isText && mediaSaverFile == null){
            flag = false;
            Snackbar.make(finalDateTV, "נא ליצור הקלטה", 3000).show();
        }
        finalDateTV.setText("dd/MM/yyyy");
        return flag;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        frequency = Integer.parseInt(frequencyOptions[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    /**
     * Move to main of mission (the first layout).
     *
     * @param view the view
     */
    public void moveToMainOfMission(View view) {
        if (updateMode){
           Intent si = new Intent(this, missionsActivity.class);
           startActivity(si);
        } else {
            setContentView(R.layout.activity_new_mission);

            ActionBar actionBar = getSupportActionBar();
            Objects.requireNonNull(actionBar).hide();
            actionBar.setHomeButtonEnabled(true);

            eventTitle = findViewById(R.id.eventTitle);

            Intent gi = getIntent();

            line1 = "הגדרת משימות לאירוע";
            line2 = gi.getStringExtra("eventTitle");
            eventID = gi.getStringExtra("eventID");
            eventTitle.setText(line1 + System.lineSeparator() + line2);
        }
    }
}