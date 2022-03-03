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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomAdapterEvents extends BaseAdapter {
    Context context;
    ArrayList<String> titleEvents, dateEvents, phonesList, namesList, eventCharacterizeList;
    ArrayList<Integer> employeesList;
    LayoutInflater inflter;

    public CustomAdapterEvents(Context applicationContext,ArrayList<String> titleEvents, ArrayList<String> dateEvents, ArrayList<String> namesList, ArrayList<String> phonesList, ArrayList<Integer> employeesList, ArrayList<String> eventCharacterizeList){
        this.context = context;
        this.titleEvents = titleEvents;
        this.dateEvents = dateEvents;
        this.phonesList = phonesList;
        this.namesList = namesList;
        this.employeesList = employeesList;
        this.eventCharacterizeList = eventCharacterizeList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return titleEvents.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_close_events,null);

        ImageView flagIV = (ImageView) view.findViewById(R.id.flagIV);
        TextView dateOfEventTV = (TextView) view.findViewById(R.id.dateOfEventTV);
        TextView titleTV = (TextView) view.findViewById(R.id.titleTV);
        TextView eventEmployeesTV = (TextView) view.findViewById(R.id.eventEmployeesTV);
        TextView customerDetailsTV = (TextView) view.findViewById(R.id.customerDetailsTV);

        DateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);
        Date tempSelectedDate  = null;
        try {
            tempSelectedDate = format.parse(dateEvents.get(i));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String strDate = DateConvertor.dateToString(tempSelectedDate,"dd/MM/yyyy HH:mm");
        dateOfEventTV.setText(strDate);

        titleTV.setText(titleEvents.get(i));
        eventEmployeesTV.setText(String.valueOf(employeesList.get(i)));
        customerDetailsTV.setText(phonesList.get(i)+" | "+ namesList.get(i));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.5f);
        lp.setMargins(20, 5, 0, 0);
        flagIV.setLayoutParams(lp);
        flagIV.setImageResource(R.drawable.ic_baseline_flag);
        String flagID = eventCharacterizeList.get(i);
        if (flagID.equals("G")){
            DrawableCompat.setTint(flagIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.green_flag));
        }
        else if (flagID.equals("O")){
            DrawableCompat.setTint(flagIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.orange_flag));
        }
        else if (flagID.equals("R")){
            DrawableCompat.setTint(flagIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.red_flag));
        }
        else{
            DrawableCompat.setTint(flagIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.gray_700));
        }


        return view;
    }
}
