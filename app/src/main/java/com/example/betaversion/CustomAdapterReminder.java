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
import java.util.Objects;

public class CustomAdapterReminder extends BaseAdapter{
    Context context;
    ArrayList<String> remindsTitleList;
    ArrayList<String> contentTextList;
    ArrayList<String> remindersAudioContentList;
    ArrayList<Boolean> isText;
    ArrayList<String> lastDateToRemindList;
    LayoutInflater inflter;

    public CustomAdapterReminder(Context applicationContext, ArrayList<String> remindsTitleList, ArrayList<String> contentTextList, ArrayList<String> remindersAudioContentList, ArrayList<Boolean> isText, ArrayList<String> lastDateToRemindList) {
        this.context = context;
        this.remindsTitleList = remindsTitleList;
        this.contentTextList = contentTextList;
        this.remindersAudioContentList = remindersAudioContentList;
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
        ImageView statusIV = view.findViewById(R.id.statusIV);

        titleTV.setText(remindsTitleList.get(i));
        // cast the String to date
        Date tempSelectedDate = DateConvertor.stringToDate(lastDateToRemindList.get(i), "yyyyMMddHHmmyyyyMMddHHmmss");

        lastDateTV.setText(Objects.requireNonNull(tempSelectedDate).getDate()+"/"+tempSelectedDate.getMonth()+1+"/"+tempSelectedDate.getYear());

        if (!isText.get(i)) {
            contentTV.setText(contentTextList.get(i));
            statusIV.setImageResource(R.drawable.ic_mic);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
            lp.setMargins(10, 10, 10, 10);
            statusIV.setLayoutParams(lp);
            DrawableCompat.setTint(statusIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.brown_200));
        }
        else {
            contentTV.setText(contentTextList.get(i));
            statusIV.setImageResource(R.drawable.ic_text_sign);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
            lp.setMargins(10, 10, 10, 10);
            statusIV.setLayoutParams(lp);
            DrawableCompat.setTint(statusIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.brown_200));
        }

        return view;
    }
}
