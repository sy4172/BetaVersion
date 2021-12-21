package com.example.betaversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class CustomAdapterReminder extends BaseAdapter{
    Context context;
    ArrayList<String> remindsTitleList;
    ArrayList<String> contentTextList;
    boolean isText;
    ArrayList<Date> lastDateToRemindList;
    LayoutInflater inflter;

    public CustomAdapterReminder(Context applicationContext, ArrayList<String> remindsTitleList, ArrayList<String> contentTextList, boolean isText, ArrayList<Date> lastDateToRemindList) {
        this.context = context;
        this.remindsTitleList = remindsTitleList;
        this.contentTextList = contentTextList;
        this.isText = isText;
        this.lastDateToRemindList = lastDateToRemindList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return remindsTitleList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"SetTextI18n", "ViewHolder"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_remider, null);

        TextView titleTV = view.findViewById(R.id.titleTV);
        TextView lastDateTV = view.findViewById(R.id.lastDateTV);
        TextView contentTV = view.findViewById(R.id.contentTV);

        titleTV.setText(remindsTitleList.get(i));
        lastDateTV.setText(lastDateToRemindList.get(i).getYear()+"/"+lastDateToRemindList.get(i).getMonth()+"/"+lastDateToRemindList.get(i).getDate());
        if (!isText) {
            contentTV.setText("קטע קול");
        }
        else contentTV.setText(contentTextList.get(i));

        return view;
    }
}
