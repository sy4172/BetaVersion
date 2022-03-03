package com.example.betaversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterMissions  extends BaseAdapter {
    Context context;
    ArrayList<String> missionTitlesList, missionContentsList, missionLastDatesList;
    ArrayList<Boolean> missionStatusList;
    LayoutInflater inflter;

    public CustomAdapterMissions(Context applicationContext, ArrayList<String> missionTitlesList, ArrayList<Boolean> missionStatusList, ArrayList<String> missionContentsList, ArrayList<String> missionLastDatesList){
        this.context = context;
        this.missionTitlesList = missionTitlesList;
        this.missionStatusList = missionStatusList;
        this.missionContentsList = missionContentsList;
        this.missionLastDatesList = missionLastDatesList;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_missions,null);

        TextView missionTitle = view.findViewById(R.id.missionTitle);
        ImageView missionStatusIV = view.findViewById(R.id.missionStatusIV);
        TextView missionContent = view.findViewById(R.id.missionContent);
        TextView lastDateMission = view.findViewById(R.id.lastDateMission);

        missionTitle.setText(missionTitlesList.get(i) +" <<");

        if (missionStatusList.get(i)){
            missionStatusIV.setImageResource(R.drawable.ic_text_sign);
        } else {
            missionStatusIV.setImageResource(R.drawable.ic_mic);
        }

       if (missionContentsList.get(i).equals("")){
           missionContent.setText(missionContentsList.get(i));
       }
       else {
           missionContent.setText("<קטע קול>");
       }

        lastDateMission.setText(missionLastDatesList.get(i));


        return view;
    }
}
