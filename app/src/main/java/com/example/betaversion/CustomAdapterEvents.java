package com.example.betaversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class CustomAdapterEvents extends BaseAdapter {
    Context context;
    ArrayList<String> titleEvents;
    ArrayList<Date> dateEvents;
    LayoutInflater inflter;

    public CustomAdapterEvents(Context applicationContext,ArrayList<String> titleEvents, ArrayList<Date> dateEvents){
        this.context = applicationContext;
        this.titleEvents = titleEvents;
        this.dateEvents = dateEvents;
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

        TextView titleEvent = view.findViewById(R.id.titleEvent);
        TextView wholeDate = view.findViewById(R.id.wholeDate);
        TextView time = view.findViewById(R.id.time);

        titleEvent.setText(titleEvents.get(i));
        wholeDate.setText(dateEvents.get(i).getDay() + "/"+ dateEvents.get(i).getMonth() + "/"+ dateEvents.get(i).getYear());
        time.setText(dateEvents.get(i).getHours() + ":"+ dateEvents.get(i).getMinutes());
        return view;
    }
}
