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
 * * This CustomAdapterMaterials.class design an item of a material
 */
public class CustomAdapterMaterials extends BaseAdapter {

    Context context;
    ArrayList<String> materialsTitleList;

    ArrayList<Integer> materialsTotalList, materialsUsedList;
    LayoutInflater inflter;

    /**
     * Instantiates a new Custom adapter materials.
     *
     * @param applicationContext the application context
     * @param materialsTitleList the materials title list
     * @param materialsTotalList the materials total list
     * @param materialsUsedList  the materials used list
     */
    public CustomAdapterMaterials(Context applicationContext, ArrayList<String> materialsTitleList, ArrayList<Integer> materialsTotalList, ArrayList<Integer> materialsUsedList){
        this.context = context;
        this.materialsTitleList = materialsTitleList;
        this.materialsTotalList = materialsTotalList;
        this.materialsUsedList = materialsUsedList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return materialsTitleList.size();
    }

    @Override
    public Object getItem(int i) {
        return materialsTitleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_lv_material, null);

        TextView materialTitleTV = (TextView) view.findViewById(R.id.materialTitleTV);
        TextView dataTV = (TextView) view.findViewById(R.id.dataTV);

        materialTitleTV.setText(materialsTitleList.get(i));
        dataTV.setText(materialsUsedList.get(i)+"/"+materialsTotalList.get(i));

        return view;
    }
}
