package com.example.betaversion;

import static com.example.betaversion.FBref.refBusinessEqu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	4.0
 * * @since		11/12/2021
 *
 * * This settingsActivity.class displays the settings control on the business and all the properties.
 */
public class settingsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    BottomNavigationView bottomNavigationView;
    TabItem showsTab, materialsTab;
    ListView generalLV;
    TextView efficiencyTV, availableTV, totalTV;
    ArrayList<String> keysList, materialsKeyList, showsKeyList, showsDesList, materialsUsedList;
    ArrayList<Integer> dataList, materialsDataList, showsDataList;

    ArrayList<Material> allMaterials; // Summarize all the Material objects that were created
    ArrayList<Shows> allShows;// Summarize all the Shows objects that were created

    CustomAdapterSettings customAdapterSettings2; // For the materialsLV
    CustomAdapterSettings customAdapterSettings3; // For the showsLV

    SwipeListener swipeListener;
    /**
     * The Option that selected.
     */
    String option;

    BusinessEqu businessEqu;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        generalLV = findViewById(R.id.generalLV);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        totalTV = findViewById(R.id.totalTV);
        availableTV = findViewById(R.id.availableTV);
        efficiencyTV = findViewById(R.id.efficiencyTV);
        materialsTab = findViewById(R.id.materialsTab);
        showsTab = findViewById(R.id.showsTab);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        swipeListener = new SwipeListener(generalLV); // Initialize the swipe listener

        generalLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        generalLV.setOnCreateContextMenuListener(this);

        keysList = new ArrayList<>();
        dataList = new ArrayList<>();

        materialsKeyList = new ArrayList<>();
        materialsDataList = new ArrayList<>();
        materialsUsedList = new ArrayList<>();

        showsKeyList = new ArrayList<>();
        showsDataList = new ArrayList<>();
        showsDesList = new ArrayList<>();

        allMaterials = new ArrayList<>();
        allShows = new ArrayList<>();

        businessEqu = new BusinessEqu();

        option = "ציוד";

        getAllSysData();
        getAllMaterials();
    }

    private void getAllShows() {
        refBusinessEqu.child("showsList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                showsKeyList.clear();
                showsDataList.clear();
                showsDesList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Shows tempShow = data.getValue(Shows.class);
                    allShows.add(tempShow);

                    showsKeyList.add(Objects.requireNonNull(tempShow).getShowTitle());
                    showsDataList.add(tempShow.getCost());
                    showsDesList.add(tempShow.getDescription());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAllMaterials() {
        refBusinessEqu.child("materials").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                materialsKeyList.clear();
                materialsDataList.clear();
                materialsUsedList.clear();
                allMaterials.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Material temp = data.getValue(Material.class);
                    allMaterials.add(temp);

                    materialsKeyList.add(Objects.requireNonNull(temp).getTypeOfMaterial());
                    materialsDataList.add(temp.getTotalAmount());
                    materialsUsedList.add(String.valueOf(temp.getUsedAmount()));
                }

                customAdapterSettings2 = new CustomAdapterSettings(getApplicationContext(), materialsKeyList, materialsDataList, materialsUsedList);
                customAdapterSettings2.notifyDataSetChanged();
                generalLV.setAdapter(customAdapterSettings2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAllSysData() {
        refBusinessEqu.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                keysList.clear();
                dataList.clear();

                for(int i = 0; i < 1; i ++) {
                    keysList.add("סה\"כ עובדים");
                    dataList.add(dS.child("totalEmployees").getValue(Integer.class));

                    keysList.add("סה\"כ עובדים זמינים");
                    dataList.add(dS.child("availableEmployees").getValue(Integer.class));

                    keysList.add("נצילות החומרים");
                    dataList.add(dS.child("efficiency").getValue(Integer.class));
                }
                efficiencyTV.setText(dataList.get(2)+"%");
                if (dataList.get(2) > 85) efficiencyTV.setTextColor(Color.RED);
                else efficiencyTV.setTextColor(Color.GRAY);
                availableTV.setText(dataList.get(1)+"");
                totalTV.setText(dataList.get(0)+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Move to previous act. when the back button is pressed.
     *
     * @param view the back button
     */
    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    /**
     * Move to create an event. when the FloatingActionButton is pressed.
     *
     * @param view the FloatingActionButton
     */
    public void moveToCreateAnEvent(View view) {
        Toast.makeText(this, "New Event", Toast.LENGTH_SHORT).show();
//        Intent si = new Intent(this, newEventActivity.class);
//        startActivity(si);
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;

        if (id == R.id.off){
            Logout();
        }
        else if (id == R.id.viewer){
            Toast.makeText(this, "Single Event", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,singleEventActivity.class);
//                startActivity(si);
//                finish();
        }
        else if (id == R.id.events){
            Toast.makeText(this, "All Events", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,eventsActivity.class);
//                startActivity(si);
//                finish();
        }
        else if (id == R.id.newMissions){
            Toast.makeText(this, "New Missions", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,newMissionsActivity.class);
//                startActivity(si);
//                finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;

        if (id == R.id.viewer){
            Toast.makeText(this, "Single Event", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,singleEventActivity.class);
//                startActivity(si);
//                finish();
        }
        else if (id == R.id.events){
            Toast.makeText(this, "All Events", Toast.LENGTH_SHORT).show();
//                si = new Intent(this,eventsActivity.class);
//                startActivity(si);
//                finish();
        }
        else if (id == R.id.newMissions){
            Toast.makeText(this, "New Missions", Toast.LENGTH_SHORT).show();
//            si = new Intent(this, newMissionsActivity.class);
//            startActivity(si);
//            finish();
        }
        else{
            return false;
        }

        return true;
    }

    /**
     * Logout method for logout from the user.
     */
    public void Logout() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("להתנתק?");
        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        Variable.setEmailVer(settings.getString("email",""));
        adb.setMessage(Variable.getEmailVer().substring(0,Variable.emailVer.indexOf("@"))+" יצא מהמערכת");

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent si = new Intent(settingsActivity.this, LoginActivity.class);

                // Changing the preferences to default
                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("email", "");
                editor.putBoolean("stayConnect",false);
                editor.apply();

                startActivity(si);
                finish();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * Create new material to save in the business system.
     */
    public void createNewMaterial() {
        getAllShows();
        getAllMaterials();

        businessEqu.setTotalEmployees(dataList.get(0));
        businessEqu.setAvailableEmployees(dataList.get(1));
        businessEqu.setEfficiency(dataList.get(2));
        businessEqu.setShowsList(allShows);

        Material temp = new Material();

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("חומר חדש");
        final EditText typeET = new EditText(this);
        typeET.setHint("סוג");
        final EditText totalAmountET = new EditText(this);
        totalAmountET.setHint("סה\"כ החומר");
        AdScreen.addView(typeET);
        AdScreen.addView(totalAmountET);
        adb.setView(AdScreen);

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                temp.setTypeOfMaterial(typeET.getText().toString());
                temp.setTotalAmount(Integer.parseInt(totalAmountET.getText().toString()));
                temp.setUsedAmount(0);
                
                allMaterials.add(temp);
                businessEqu.setMaterials(allMaterials);
                refBusinessEqu.setValue(businessEqu);

                materialsKeyList.add(temp.getTypeOfMaterial());
                materialsDataList.add(temp.getTotalAmount());
                materialsUsedList.add(String.valueOf(temp.getUsedAmount()));
            }
        });
        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * Create new show to save in the business system.
     */
    public void createNewShow() {
        getAllShows();
        getAllMaterials();

        businessEqu.setMaterials(allMaterials);
        businessEqu.setTotalEmployees(dataList.get(0));
        businessEqu.setAvailableEmployees(dataList.get(1));
        businessEqu.setEfficiency(dataList.get(2));

        Shows temp = new Shows();

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("מופע חדש");
        final EditText nameET = new EditText(this);
        nameET.setHint("שם המופע");
        final EditText costET = new EditText(this);
        costET.setHint("עלות");
        final EditText desET = new EditText(this);
        desET.setHint("תיאור");
        desET.setMaxLines(5);
        AdScreen.addView(nameET);
        AdScreen.addView(costET);
        AdScreen.addView(desET);
        adb.setView(AdScreen);

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                temp.setShowTitle(nameET.getText().toString());
                temp.setCost(Integer.parseInt(costET.getText().toString()));
                temp.setDescription(desET.getText().toString());

                allShows.add(temp);
                businessEqu.setShowsList(allShows);
                refBusinessEqu.setValue(businessEqu);

                showsKeyList.add(temp.getShowTitle());
                showsDataList.add(temp.getCost());
                showsDesList.add(temp.getDescription());
            }
        });
        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);

        if (option.equals("ציוד")){
            menu.add("הסר חומר");
            menu.add("עדכן חומר");
        }
        else if (option.equals("מופעים")){
            menu.add("הסר מופע");
            menu.add("עדכן מופע");
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String option = item.getTitle().toString();
        int pos = adpInfo.position;


        // Creating different ContextItems according its type
        if (option.equals("הסר חומר")){
            materialsKeyList.remove(pos);
            materialsDataList.remove(pos);
            materialsUsedList.remove(pos);
            allMaterials.remove(pos);
            customAdapterSettings2.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterSettings2);
            refBusinessEqu.child("materials").child(String.valueOf(pos)).removeValue();
        }
        else if (option.equals("עדכן חומר")){
            updateAMaterial(pos);
        }
        else if (option.equals("הסר מופע")){
            showsKeyList.remove(pos);
            showsDataList.remove(pos);
            showsDesList.remove(pos);
            allShows.remove(pos);
            customAdapterSettings3.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterSettings3);
            refBusinessEqu.child("showsList").child(String.valueOf(pos)).removeValue();
        }
        else if (option.equals("עדכן מופע")){
            updateAShow(pos);
        }

        return super.onContextItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void updateAData(int pos) {
        allShows.clear();
        allMaterials.clear();
        getAllShows();
        getAllMaterials();
        Toast.makeText(this, dataList.get(pos)+"", Toast.LENGTH_SHORT).show();
        businessEqu.setMaterials(allMaterials);
        businessEqu.setTotalEmployees(dataList.get(0));
        businessEqu.setAvailableEmployees(dataList.get(1));
        businessEqu.setEfficiency(dataList.get(2));
        businessEqu.setShowsList(allShows);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(keysList.get(pos));
        final EditText valueET = new EditText(this);

        valueET.setHint("ערך");

        adb.setView(valueET);

        valueET.setText(""+dataList.get(pos));
        adb.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dataList.set(pos, Integer.parseInt(valueET.getText().toString()));

                if (pos == 0) {
                    businessEqu.setTotalEmployees(dataList.get(pos));
                } else if (pos == 1) {
                    if (!(dataList.get(pos) > businessEqu.getTotalEmployees()))
                        businessEqu.setAvailableEmployees(dataList.get(pos));
                }
                refBusinessEqu.setValue(businessEqu);

                efficiencyTV.setText(businessEqu.getEfficiency()+"%");
                if (dataList.get(2) > 85) efficiencyTV.setTextColor(Color.RED);
                else efficiencyTV.setTextColor(Color.GRAY);
                availableTV.setText(businessEqu.getAvailableEmployees()+"");
                totalTV.setText(businessEqu.getTotalEmployees()+"");
            }
        }).setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateAShow(int pos) {
        Shows temp = new Shows(allShows.get(pos).getShowTitle(), allShows.get(pos).getDescription(), allShows.get(pos).getCost());

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("עדכון מופע");
        final EditText nameET = new EditText(this);
        nameET.setHint("שם המופע");
        final EditText totalAmountET = new EditText(this);
        totalAmountET.setHint("סה\"כ החומר");
        final EditText desET = new EditText(this);
        desET.setHint("תוכן");
        AdScreen.addView(nameET);
        AdScreen.addView(totalAmountET);
        AdScreen.addView(desET);
        adb.setView(AdScreen);

        nameET.setText(showsKeyList.get(pos));
        totalAmountET.setText(""+showsDataList.get(pos));
        desET.setText(showsDesList.get(pos));

        adb.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ((!nameET.getText().toString().isEmpty() && !nameET.getText().toString().contains("\\d+")) && (!totalAmountET.getText().toString().isEmpty() && totalAmountET.getText().toString().contains("\\d+")) && !desET.getText().toString().isEmpty()){
                    temp.setShowTitle(nameET.getText().toString());
                    temp.setCost(Integer.parseInt(totalAmountET.getText().toString()));
                    temp.setDescription(desET.getText().toString());

                    allShows.set(pos,temp);
                    refBusinessEqu.child("showsList").setValue(allShows);

                    showsKeyList.set(pos, temp.getShowTitle());
                    showsDataList.set(pos, temp.getCost());
                    showsDesList.set(pos, temp.getDescription());

                    customAdapterSettings3 = new CustomAdapterSettings(getApplicationContext(), showsKeyList, showsDataList, showsDesList);
                    customAdapterSettings3.notifyDataSetChanged();
                    generalLV.setAdapter(customAdapterSettings3);
                }
            }
        });
        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateAMaterial(int pos) {
        Material temp = new Material(allMaterials.get(pos).getTypeOfMaterial(), allMaterials.get(pos).getTotalAmount(), allMaterials.get(pos).getUsedAmount());

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("עדכון חומר");
        final EditText typeET = new EditText(this);
        typeET.setHint("סוג");
        final EditText totalAmountET = new EditText(this);
        totalAmountET.setHint("סה\"כ החומר");
        AdScreen.addView(typeET);
        AdScreen.addView(totalAmountET);
        adb.setView(AdScreen);

        typeET.setText(materialsKeyList.get(pos));
        totalAmountET.setText(""+materialsDataList.get(pos));

        adb.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ((!typeET.getText().toString().isEmpty() && !typeET.getText().toString().contains("\\d+")) && (!totalAmountET.getText().toString().isEmpty() && totalAmountET.getText().toString().contains("\\d+"))){
                    temp.setTypeOfMaterial(typeET.getText().toString());
                    temp.setTotalAmount(Integer.parseInt(totalAmountET.getText().toString()));

                    allMaterials.set(pos,temp);
                    refBusinessEqu.child("Materials").setValue(allMaterials);

                    materialsKeyList.set(pos, temp.getTypeOfMaterial());
                    materialsDataList.set(pos, temp.getTotalAmount());

                    customAdapterSettings2 = new CustomAdapterSettings(getApplicationContext(), materialsKeyList, materialsDataList, materialsUsedList);
                    customAdapterSettings2.notifyDataSetChanged();
                    generalLV.setAdapter(customAdapterSettings2);
                }
            }
        });
        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }


    /**
     * Create new item according the selection.
     *
     * @param view the view
     */
    public void createNewItem(View view) {
        if (option.equals("מופעים"))
        {
            createNewShow();
            customAdapterSettings3 = new CustomAdapterSettings(getApplicationContext(), showsKeyList, showsDataList, showsDesList);
            customAdapterSettings3.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterSettings3);

        }
        else if (option.equals("ציוד"))
        {
            createNewMaterial();
            customAdapterSettings2 = new CustomAdapterSettings(getApplicationContext(), materialsKeyList, materialsDataList, materialsUsedList);
            customAdapterSettings2.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterSettings2);
        }
    }

    public void updateTotal(View view) {
        updateAData(0);
    }

    public void updateAvailable(View view) {
        updateAData(1);
    }

    private class SwipeListener implements View.OnTouchListener{
        GestureDetector gestureDetector;

        // The mehtod that finds the direction accordingly the change of the x value
        SwipeListener(View view){
            int threshold = 10;
            int velocityThreshold = 10;

                GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        float xDiff = e2.getX() - e1.getX();

                        try {
                            if (Math.abs(xDiff) > threshold && Math.abs(xDiff) > velocityThreshold) {
                                if (xDiff > 0) {
                                    // Swiped Right
                                    Toast.makeText(settingsActivity.this, "Right", Toast.LENGTH_SHORT).show();
                                    showsTab.setSelected(false);
                                    materialsTab.setSelected(true);
                                    option = "ציוד";
                                    getAllMaterials();

                                } else {
                                    Toast.makeText(settingsActivity.this, "Left", Toast.LENGTH_SHORT).show();
                                    materialsTab.setSelected(false);
                                    showsTab.setSelected(true);
                                    option = "מופעים";
                                    getAllShows();

                                }
                                return true;
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        return false;
                    }
                };
                gestureDetector = new GestureDetector(listener);
                view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }
}
