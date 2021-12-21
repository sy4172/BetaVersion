package com.example.betaversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterSettings extends BaseAdapter {
    Context context;
    ArrayList<String> keyList;
    ArrayList<Integer> dataList;
    ArrayList<String> expendDataList;
    LayoutInflater inflter;

    public CustomAdapterSettings(Context applicationContext, ArrayList<String> keyList, ArrayList<Integer> dataList, ArrayList<String> expendDataList) {
        this.context = context;
        this.keyList = keyList;
        this.dataList = dataList;
        this.expendDataList = expendDataList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return keyList.size();
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
        view = inflter.inflate(R.layout.custom_lv_settings, null);

        TextView keyTV = (TextView) view.findViewById(R.id.keyTV);
        TextView dataTV = (TextView) view.findViewById(R.id.dataTV);
        TextView expendTV = (TextView) view.findViewById(R.id.expendTV);

        keyTV.setText(keyList.get(i));
        dataTV.setText(""+dataList.get(i));
        expendTV.setText(""+expendDataList.get(i));

        return view;
    }
}

