package com.example.betaversion;

import static com.example.betaversion.FBref.refGreen_Event;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

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

public class newMissionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView eventTitle, finalDateTV;
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
    String eventID, line1, line2, title, context, dateOfChange, finalDateStr;
    String [] frequencyOptions;
    int frequency;
    Date selectedDate;

    Mission tempMission;
    HashMap <String, Mission> allMissions;

    private static final int PERMISSION_CODE = 12;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;

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
        mediaPlayer = new MediaPlayer();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, frequencyOptions);

        if (isText && !updateMode){
            setContentView(R.layout.text_layout_mission);
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
            finalDateTV.setText(DateConvertor.dateToString(tempDate, "dd/mm/yyyy"));

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

    public void createRecordedMission(View view) {
        isText = isRecording = creationMode = false;
        updateMode = true;

        setContentView(R.layout.record_layout_mission);
        eventTitle = findViewById(R.id.eventTitle);

        Intent gi = getIntent();

        line1 = "הגדרת משימה כשמע לאירוע";
        line2 = gi.getStringExtra("eventTitle");
        eventID = gi.getStringExtra("eventID");
        eventTitle.setText(line1 + System.lineSeparator() + line2);
    }

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

            if (checkPermissions()){
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

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            ActivityCompat.requestPermissions(this,new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

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

    public void playRecord(View view) {
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
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
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
            eventTitle = findViewById(R.id.eventTitle);
            Snackbar.make(eventTitle,"לא ניתן לשמוע במהלך הקלטה", 1000).show();
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
                eventTitle = findViewById(R.id.eventTitle);
                Snackbar.make(eventTitle,"אין תוכן להשמעה. הקלט כדי לשמוע", 1000).show();
            }
        }
    }

    private void stopAudio() {
        mediaPlayer.stop();
        isPlaying = false;
    }

    public void createTextedMission(View view) {
        isText = true;
        updateMode = true;

        setContentView(R.layout.text_layout_mission);
        eventTitle = findViewById(R.id.eventTitle);
        finalDateTV = findViewById(R.id.finalDateTV);
        titleMissionET = findViewById(R.id.titleMissionET);
        contextMissionET = findViewById(R.id.contextMissionET);
        frequencySpinner = findViewById(R.id.frequencySpinner);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, frequencyOptions);
        frequencySpinner.setAdapter(adp);

        frequencySpinner.setOnItemSelectedListener(this);

        line1 = "הגדרת משימה כטקסט לאירוע";
        eventTitle.setText(line1 + System.lineSeparator() + line2);
    }

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
                    c.set(year, month++, dayOfMonth);
                    selectedDate = new Date(c.get(Calendar.YEAR) - 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    String dateStr = DateConvertor.dateToString(selectedDate, "dd/MM/yyyy");
                    finalDateTV = findViewById(R.id.finalDateTV);
                    finalDateTV.setText(dateStr);

                    finalDateStr = DateConvertor.dateToString(selectedDate, "yyyymmdd"); // The format in the FB
                }
            }, year, month, day);

            dpd.show();
        } else {
            Snackbar.make(eventTitle,"לא ניתן במצב צפיה", 3000).show();
        }
    }

    public void createMission(View view) {
        if (isText){
            titleMissionET = findViewById(R.id.titleMissionET);
            contextMissionET = findViewById(R.id.contextMissionET);
            finalDateTV = findViewById(R.id.finalDateTV);

            finalDateTV.setText(finalDateStr);

            if (checkInputs()){
                title = titleMissionET.getText().toString();
                context = contextMissionET.getText().toString();
                dateOfChange = DateConvertor.dateToString(new Date(), "dd/mm/yyyy HH:mm");

                tempMission = new Mission(title, true, context,"",DateConvertor.dateToString(new Date(), "yyyyMMddHHmmss"), frequency, finalDateStr);
                // Save the tempMission in the current place of the selected event in the RealTime DataBaseFirebase
                String missionID = finalDateStr+DateConvertor.dateToString(new Date(), "yyyyMMddHHmmss");
                allMissions.put(missionID,tempMission);
                refGreen_Event.child(eventID).child("eventMissions").setValue(allMissions);

                // TODO: Implementation of the notifications
                Snackbar.make(finalDateTV,"המשימה נוצרה", 3000).show();
            }

        } else {
            titleMissionET = findViewById(R.id.titleMissionET);
            contextMissionET = findViewById(R.id.contextMissionET);
            finalDateTV = findViewById(R.id.finalDateTV);

            if (finalDateStr.isEmpty()){
                finalDateTV.setText("dd/mm/yyyy HH:mm");
            } else finalDateTV.setText(finalDateStr);

            if (checkInputs()){
                title = titleMissionET.getText().toString();
                context = contextMissionET.getText().toString();
                dateOfChange = DateConvertor.dateToString(new Date(), "yyyyMMddHHmmss");

                tempMission = new Mission(title, true, "<קטע קול>",mediaSaverFile.getAbsolutePath(),dateOfChange, frequency, finalDateStr);
                // Save the tempMission in the current place of the selected event in the RealTime DataBaseFirebase
                String missionID = finalDateStr+dateOfChange;
                allMissions.put(missionID,tempMission);
                refGreen_Event.child(eventID).child("eventMissions").setValue(allMissions);

                // TODO: Implementation of the notifications
                Snackbar.make(finalDateTV,"המשימה נוצרה", 3000).show();
            }
        }
    }

    private boolean checkInputs() {
        boolean flag = true;
        finalDateTV = findViewById(R.id.finalDateTV);
        if (titleMissionET.getText().toString().isEmpty() || contextMissionET.getText().toString().isEmpty() || finalDateStr.isEmpty()){
            flag = false;
            Snackbar.make(finalDateTV, "שדה לא יהיה ריק", 3000).show();
        }
        else if (frequency == 0){
            flag = false;
            Snackbar.make(finalDateTV, "נא לבחור את התדירות", 3000).show();
        }
        else if (!isText && mediaSaverFile.getName().equals("")){
            flag = false;
            Snackbar.make(finalDateTV, "נא ליצור הקלטה", 3000).show();
        }

        return flag;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        frequency = Integer.parseInt(frequencyOptions[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    public void moveToMainOfMission(View view) {
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