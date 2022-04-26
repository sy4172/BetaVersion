package com.example.betaversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * * @author    Shahar Yani
 * * @version  	1.0
 *
 * * This CustomAdapterShows.class design an item of a show
 */
public class CustomAdapterShows extends BaseAdapter {

    Context context;
    ArrayList<String> showTitlesList, descriptionsList;
    ArrayList<Integer> costsList, employeesList;
    LayoutInflater inflter;

    /**
     * Instantiates a new Custom adapter shows.
     *
     * @param applicationContext the application context
     * @param showTitlesList     the show titles list
     * @param descriptionsList   the descriptions list
     * @param costsList          the costs list
     * @param employeesList      the employees list
     */
    public CustomAdapterShows(Context applicationContext, ArrayList<String> showTitlesList, ArrayList<String> descriptionsList, ArrayList<Integer> costsList, ArrayList<Integer> employeesList) {
        this.context = context;
        this.showTitlesList = showTitlesList;
        this.descriptionsList = descriptionsList;
        this.costsList = costsList;
        this.employeesList = employeesList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return showTitlesList.size();
    }

    @Override
    public Object getItem(int i) {
        return showTitlesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_shows, null);

        TextView titleShowTV = (TextView) view.findViewById(R.id.titleShowTV);
        TextView costTV = (TextView) view.findViewById(R.id.costTV);
        TextView descriptionTV = (TextView) view.findViewById(R.id.descriptionTV);
        TextView employeesTV = (TextView) view.findViewById(R.id.employeesTV);

        titleShowTV.setText(showTitlesList.get(i));
        costTV.setText(""+costsList.get(i)+"₪");
        descriptionTV.setText(descriptionsList.get(i));
        employeesTV.setText(""+employeesList.get(i)+" עובדים");

        return view;
    }
}
