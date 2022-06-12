package com.example.betaversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.Date;

/**
 * * @author    Shahar Yani
 * * @version  	2.0
 *
 * * This CustomAdapterMissions.class design an item of a mission
 */
public class CustomAdapterMissions extends BaseAdapter {

    Context context;
    String eventTitle;
    ArrayList<String> missionTitlesList,missionContentsList, missionLastDatesList;
    ArrayList<Boolean> missionStatusList;
    ArrayList<Integer> frequencyList;
    LayoutInflater inflter;

    /**
     * Instantiates a new Custom adapter missions.
     *
     * @param applicationContext   the application context
     * @param eventTitle           the event title
     * @param missionTitlesList    the mission titles list
     * @param missionStatusList    the mission status list
     * @param missionContentsList  the mission contents list
     * @param missionLastDatesList the mission last dates list
     * @param frequencyList        the frequency list
     */
    public CustomAdapterMissions(Context applicationContext,String eventTitle, ArrayList<String> missionTitlesList, ArrayList<Boolean> missionStatusList, ArrayList<String> missionContentsList, ArrayList<String> missionLastDatesList, ArrayList<Integer> frequencyList){
        this.context = context;
        this.eventTitle = eventTitle;
        this.missionTitlesList = missionTitlesList;
        this.missionStatusList = missionStatusList;
        this.missionContentsList = missionContentsList;
        this.missionLastDatesList = missionLastDatesList;
        this.frequencyList = frequencyList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return missionTitlesList.size();
    }

    @Override
    public Object getItem(int i) {
        return missionTitlesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_missions,null);

        TextView missionTitle = (TextView) view.findViewById(R.id.missionTitle);
        ImageView missionStatusIV = (ImageView) view.findViewById(R.id.missionStatusIV);
        TextView missionContent = (TextView) view.findViewById(R.id.missionContent);
        TextView lastDateMission = (TextView) view.findViewById(R.id.lastDateMission);

        missionTitle.setText(eventTitle+" >> "+missionTitlesList.get(i));

        // Set a layout for the missionStatusIV that won't get all the area
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);
        lp.setMargins(0, 0, 3, 0);
        missionStatusIV.setLayoutParams(lp);
        if (missionStatusList.get(i)){
            missionStatusIV.setImageResource(R.drawable.ic_text_sign);
        } else {
            missionStatusIV.setImageResource(R.drawable.ic_mic);
        }
        DrawableCompat.setTint(missionStatusIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.brown_200));

        // Displaying the missionContent accordingly to the status
        if (!missionContentsList.get(i).isEmpty()){
           missionContent.setText(missionContentsList.get(i));
       }
       else {
           missionContent.setText("<קטע קול>");
       }

       // Setting the selected date in format of 'dd/MM/yyyy'
        Date tempSelectedDate  = DateConvertor.stringToDate(missionLastDatesList.get(i), "yyyyMMddHHmm");
        String strDate = DateConvertor.dateToString(tempSelectedDate,"dd/MM/yyyy");
        lastDateMission.setText("עד ה־"+strDate+" | תדירות של "+frequencyList.get(i)+" פעמים");

        return view;
    }
}
