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
 * * @version  	4.2
 *
 * * This CustomAdapterReminder.class design an item of a reminder
 */
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
        Date tempSelectedDate = DateConvertor.stringToDate(lastDateToRemindList.get(i), "yyyyMMddHHmmyyyyMMddHHmmss"); // Cast the String to date
        // Setting the selected date in format of 'dd/MM/yyyy'
        lastDateTV.setText(DateConvertor.dateToString(tempSelectedDate, "dd/MM/yyyy"));

        // Displaying the statusIV (ImageView) accordingly to the status
        if (!isText.get(i)) {
            contentTV.setText(contentTextList.get(i));
            statusIV.setImageResource(R.drawable.ic_mic);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
            lp.setMargins(10, 10, 10, 10); // Set a layout for the statusIV that won't get all the area
            statusIV.setLayoutParams(lp);
            DrawableCompat.setTint(statusIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.brown_200));
        }
        else {
            contentTV.setText(contentTextList.get(i));
            statusIV.setImageResource(R.drawable.ic_text_sign);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
            lp.setMargins(10, 10, 10, 10); // Set a layout for the statusIV that won't get all the area
            statusIV.setLayoutParams(lp);
            DrawableCompat.setTint(statusIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.brown_200));
        }

        return view;
    }
}
