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
 * * @version  	5.0
 *
 * * This CustomAdapterEvents.class design an item of an event
 */
public class CustomAdapterEvents extends BaseAdapter {

    Context context;
    ArrayList<String> titleEvents, dateEvents, phonesList,namesList, eventCharacterizeList;
    ArrayList<Integer> employeesList;
    ArrayList<Boolean> isPaidList, hasAcceptedList;

    LayoutInflater inflter;

    /**
     * Instantiates a new Custom adapter events.
     *
     * @param applicationContext    the application context
     * @param titleEvents           the title events
     * @param dateEvents            the date events
     * @param namesList             the names list
     * @param phonesList            the phones list
     * @param employeesList         the employees list
     * @param eventCharacterizeList the event characterize list
     * @param isPaidList            the is paid list
     * @param hasAcceptedList       the has accepted list
     */
    public CustomAdapterEvents(Context applicationContext,ArrayList<String> titleEvents, ArrayList<String> dateEvents, ArrayList<String> namesList, ArrayList<String> phonesList, ArrayList<Integer> employeesList, ArrayList<String> eventCharacterizeList, ArrayList<Boolean> isPaidList, ArrayList<Boolean> hasAcceptedList){
        this.context = context;
        this.titleEvents = titleEvents;
        this.dateEvents = dateEvents;
        this.phonesList = phonesList;
        this.namesList = namesList;
        this.employeesList = employeesList;
        this.eventCharacterizeList = eventCharacterizeList;
        this.isPaidList = isPaidList;
        this.hasAcceptedList = hasAcceptedList;
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
        ImageView isPaidIV = (ImageView) view.findViewById(R.id.isPaidIV);
        ImageView hasAcceptedIV = (ImageView) view.findViewById(R.id.hasAcceptedIV);

        // Setting the selected date in format of 'dd/MM/yyyy HH:mm'
        Date tempSelectedDate  = DateConvertor.stringToDate(dateEvents.get(i),"yyyyMMddHHmm");
        String strDate = DateConvertor.dateToString(tempSelectedDate,"dd/MM/yyyy HH:mm");
        dateOfEventTV.setText(strDate);

        titleTV.setText(titleEvents.get(i));
        eventEmployeesTV.setText(String.valueOf(employeesList.get(i)));
        customerDetailsTV.setText(phonesList.get(i)+"\n"+ namesList.get(i));

        // Set a layout for the flagIV that won't get all the area
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.5f);
        lp.setMargins(5, 0, 0, 0);
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

        // Displaying the isPaid and hasAccepted properties accordingly to the status
        if (isPaidList.get(i)){
            isPaidIV.setVisibility(View.VISIBLE);
            isPaidIV.setImageResource(R.drawable.coin);
            DrawableCompat.setTint(isPaidIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.green_flag));
        } else {
            isPaidIV.setVisibility(View.INVISIBLE);
        }

        if (hasAcceptedList.get(i)){
            hasAcceptedIV.setVisibility(View.VISIBLE);
            hasAcceptedIV.setImageResource(R.drawable.deal);
            DrawableCompat.setTint(hasAcceptedIV.getDrawable(), ContextCompat.getColor(view.getContext(), R.color.green_flag));
        } else hasAcceptedIV.setVisibility(View.INVISIBLE);

        return view;
    }
}
