package com.example.betaversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CustomAdapterMissions  extends BaseAdapter {
    Context context;

    LayoutInflater inflter;

    public CustomAdapterMissions(Context applicationContext){
        this.context = context;

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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_missions,null);

        return view;
    }
}
