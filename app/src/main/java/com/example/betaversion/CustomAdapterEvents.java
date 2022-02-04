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
import java.util.Objects;

public class CustomAdapterEvents extends BaseAdapter {
    Context context;
    ArrayList<String> titleEvents, dateEvents, phonesList;
    ArrayList<Integer> employeesList;
    ArrayList<Character> eventsCharacterizeList;
    LayoutInflater inflter;

    public CustomAdapterEvents(Context applicationContext,ArrayList<String> titleEvents, ArrayList<String> dateEvents, ArrayList<String> phonesList, ArrayList<Integer> employeesList, ArrayList<Character> eventsCharacterizeList){
        this.context = applicationContext;
        this.titleEvents = titleEvents;
        this.dateEvents = dateEvents;
        this.phonesList = phonesList;
        this.employeesList = employeesList;
        this.eventsCharacterizeList = eventsCharacterizeList;

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

    @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_close_events,null);

        ImageView flagIV = (ImageView) view.findViewById(R.id.flagIV);
        TextView dateOfEventTV = (TextView) view.findViewById(R.id.dateOfEventTV);
        TextView titleTV = (TextView) view.findViewById(R.id.titleTV);
        TextView eventEmployeesTV = (TextView) view.findViewById(R.id.eventEmployeesTV);
        TextView customerPhoneTV = (TextView) view.findViewById(R.id.customerPhoneTV);

        DateFormat format = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);
        Date tempSelectedDate  = null;
        try {
            tempSelectedDate = format.parse(dateEvents.get(i));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateOfEventTV.setText(Objects.requireNonNull(tempSelectedDate).getDate()+"/"+tempSelectedDate.getMonth()+1+"/"+tempSelectedDate.getYear());
        titleTV.setText(titleEvents.get(i));
        eventEmployeesTV.setText(employeesList.get(i));
        customerPhoneTV.setText(phonesList.get(i));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
        lp.setMargins(10, 10, 10, 10);
        flagIV.setLayoutParams(lp);
        DrawableCompat.setTint(flagIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.green_flag));


        return view;
    }
}
