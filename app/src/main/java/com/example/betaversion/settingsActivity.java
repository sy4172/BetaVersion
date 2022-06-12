package com.example.betaversion;

import static com.example.betaversion.FBref.refBusinessEqu;
import static com.example.betaversion.FBref.refGreen_Event;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * * @author    Shahar Yani
 * * @version  	10.1
 * * @since		11/12/2021
 *
 * * This settingsActivity.class displays the settings control on the business and all the properties.
 */
public class settingsActivity extends AppCompatActivity{

    ListView generalLV; // the ListView that display the Materials & Shows objects
    TextView efficiencyTV, availableTV, totalTV, showsTV, materialsTV;

    // ArrayLists for Shows
    ArrayList<String> showTitlesList,descriptionsList;
    ArrayList<Integer> costsList, employeesList;

    // ArrayLists for Materials
    ArrayList<String> materialsTitleList;
    ArrayList<Integer> materialsTotalList,materialsUsedList;

    // ArrayLists for General Data of the BusinessEqu
    ArrayList<String> keysList;
    ArrayList<Integer> dataList;
    int efficiency;

    HashMap<String, Material> allMaterials; // Summarize all the Material objects that were created
    HashMap<String, Shows> allShows;// Summarize all the Shows objects that were created

    CustomAdapterMaterials customAdapterMaterial;
    CustomAdapterShows customAdapterShows;

    String option; // The Option that selected. (Material OR Show)
    BusinessEqu businessEqu; // the general object to all the settings properties in order to upload the FireBase DataBase

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        generalLV = findViewById(R.id.generalLV);
        totalTV = findViewById(R.id.totalTV);
        availableTV = findViewById(R.id.availableTV);
        efficiencyTV = findViewById(R.id.efficiencyTV);
        materialsTV = findViewById(R.id.materialsTV);
        showsTV = findViewById(R.id.showsTV);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        generalLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        generalLV.setOnCreateContextMenuListener(this);

        keysList = new ArrayList<>();
        dataList = new ArrayList<>();

        materialsTitleList = new ArrayList<>();
        materialsTotalList = new ArrayList<>();
        materialsUsedList = new ArrayList<>();

        showTitlesList = new ArrayList<>();
        descriptionsList = new ArrayList<>();
        costsList = new ArrayList<>();
        employeesList = new ArrayList<>();

        allMaterials = new HashMap<>();
        allShows = new HashMap<>();

        businessEqu = new BusinessEqu();
        option = " ";
        getAllSysData();
        getAllMaterials();
        getAllShows();

        // Displaying a guide message to the user
        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        if (!settings.getBoolean("understoodClickTape",false)){
            Snackbar.make(generalLV, "בחר את סוג המידע (ציוד / אירוע)", 4000).setAction("הבנתי", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Writing to the SharedPreferences file
                    SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("understoodClickTape",true);
                    editor.apply();
                }
            }).show();
        }
        // Displaying a guide message to the user after 7 seconds
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                if (!settings.getBoolean("understoodClickEM",false)){
                    Snackbar.make(generalLV, "גע על הערך של העובדים ברוטו כדי לשנות", 5000).setAction("הבנתי", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Writing to the SharedPreferences file
                            SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("understoodClickEM",true);
                            editor.apply();
                        }
                    }).show();
                }
            }
        }, 7000);
    }

    /**
     * getAllShows method gets all the Shows objects that were created to display on the TextView objects from the FireBase DataBase.
     */
    private void getAllShows() {
        refBusinessEqu.child("showsList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                showTitlesList.clear();
                descriptionsList.clear();
                costsList.clear();
                employeesList.clear();
                allShows.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Shows tempShow = data.getValue(Shows.class);
                    allShows.put(Objects.requireNonNull(tempShow).getShowTitle(),tempShow);

                    showTitlesList.add(Objects.requireNonNull(tempShow).getShowTitle());
                    descriptionsList.add(tempShow.getDescription());
                    costsList.add(tempShow.getCost());
                    employeesList.add(tempShow.getEmployees());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * getAllMaterials method gets all the Materials objects that were created to display on the TextView objects from the FireBase DataBase.
     */
    private void getAllMaterials() {
        refBusinessEqu.child("materials").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                materialsTitleList.clear();
                materialsTotalList.clear();
                materialsUsedList.clear();
                allMaterials.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Material temp = data.getValue(Material.class);
                    allMaterials.put(Objects.requireNonNull(temp).getTypeOfMaterial(), temp);

                    materialsTitleList.add(Objects.requireNonNull(temp).getTypeOfMaterial());
                    materialsTotalList.add(temp.getTotalAmount());
                    materialsUsedList.add(temp.getUsedAmount());
                }
                efficiency = setEfficiency();
                efficiencyTV.setText(""+efficiency+"%");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * getAllSysData method gets all the main properties to display on the TextView objects from the FireBase DataBase.
     */
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
                }

                efficiencyTV.setText(efficiency+"%");
                availableTV.setText(dataList.get(1)+"");
                totalTV.setText(dataList.get(0)+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * setEfficiency method is calculating the CURRENT efficiency of the uesd of all materials.
     * @return the efficiency in percentage format
     */
    private int setEfficiency() {
        double usedAmount, totalAmount, efficiency;
        usedAmount = totalAmount = 0;

        if (!materialsTotalList.isEmpty()){
            for (int i = 0; i < materialsTotalList.size(); i++) {
                usedAmount += (double) materialsUsedList.get(i);
                totalAmount += (double) materialsTotalList.get(i);
            }

            efficiency = (usedAmount/totalAmount)*100; // To percentage format

            if (efficiency > 85) efficiencyTV.setTextColor(Color.rgb(178, 34, 34));
            else if (efficiency < 85 && efficiency > 50) efficiencyTV.setTextColor(Color.rgb(115, 115, 115));
            else efficiencyTV.setTextColor(Color.rgb(0, 128, 0));

            return (int) Math.abs(Math.round(efficiency)); // Returns the absolute value of the percentage
        } else return 0;
    }

    /**
     * Create new item according the selection.
     *
     * @param view the button object
     */
    public void createNewItem(View view) {
        if (option.equals("מופעים"))
        {
            createNewShow();
            customAdapterShows = new CustomAdapterShows(getApplicationContext(), showTitlesList, descriptionsList, costsList, employeesList);
            customAdapterShows.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterShows);

        }
        else if (option.equals("ציוד"))
        {
            createNewMaterial();
            customAdapterMaterial  = new CustomAdapterMaterials(this, materialsTitleList, materialsTotalList, materialsUsedList);
            customAdapterMaterial.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterMaterial);
        }
        else{
            Snackbar.make(generalLV, "לא נבחרה אפשרות", 5000).show();
        }
    }

    /**
     * Create new material to save in the business system and to the FireBase DataBase.
     */
    public void createNewMaterial() {
        getAllShows();
        getAllMaterials();

        businessEqu.setTotalEmployees(dataList.get(0));
        businessEqu.setAvailableEmployees(dataList.get(1));
        businessEqu.setEfficiency(efficiency);
        businessEqu.setShowsList(allShows);

        Material temp = new Material();

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AdScreen.setPadding(15,0,15,0);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("ציוד חדש");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);
        final EditText typeET = new EditText(this);
        typeET.setHint("סוג");
        typeET.setTextSize(18);
        typeET.setGravity(Gravity.RIGHT);
        typeET.setPadding(0,15,30,30);
        typeET.setTypeface(ResourcesCompat.getFont(typeET.getContext(), R.font.rubik_medium));
        final EditText totalAmountET = new EditText(this);
        totalAmountET.setHint("סה\"כ כמות הציוד");
        totalAmountET.setTextSize(18);
        totalAmountET.setGravity(Gravity.RIGHT);
        totalAmountET.setPadding(0,15,30,30);
        totalAmountET.setTypeface(ResourcesCompat.getFont(totalAmountET.getContext(), R.font.rubik_medium));
        totalAmountET.setInputType(InputType.TYPE_CLASS_NUMBER);

        AdScreen.addView(typeET);
        AdScreen.addView(totalAmountET);
        adb.setView(AdScreen);

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!totalAmountET.getText().toString().isEmpty()){
                    if (checkMaterialInput(false,typeET.getText().toString(), Integer.parseInt(totalAmountET.getText().toString()))){
                        temp.setTypeOfMaterial(typeET.getText().toString());
                        temp.setTotalAmount(Integer.parseInt(totalAmountET.getText().toString()));
                        temp.setUsedAmount(0);

                        allMaterials.put(temp.getTypeOfMaterial(), temp);
                        businessEqu.setMaterials(allMaterials);
                        refBusinessEqu.setValue(businessEqu);

                        materialsTitleList.add(temp.getTypeOfMaterial());
                        materialsTotalList.add(temp.getTotalAmount());
                        materialsUsedList.add(temp.getUsedAmount());
                        customAdapterMaterial.notifyDataSetChanged();
                        efficiency = setEfficiency();
                        efficiencyTV.setText(""+efficiency+"%");
                        businessEqu.setEfficiency(efficiency);

                    } else dialogInterface.dismiss();
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
     * updateAMaterial method is for updating the material
     * @param  pos the index of the material in the list.
     */
    @SuppressLint("SetTextI18n")
    private void updateAMaterial(int pos) {
        getAllMaterials();
        Material temp = new Material(allMaterials.get(materialsTitleList.get(pos)).getTypeOfMaterial(), allMaterials.get(materialsTitleList.get(pos)).getTotalAmount(), allMaterials.get(materialsTitleList.get(pos)).getUsedAmount());

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AdScreen.setPadding(15,0,15,0);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("עדכן את החומר - "+allMaterials.get(materialsTitleList.get(pos)).getTypeOfMaterial());
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);

        final EditText typeET = new EditText(this);
        typeET.setHint("סוג");
        typeET.setTextSize(18);
        typeET.setGravity(Gravity.RIGHT);
        typeET.setPadding(0,15,30,30);
        typeET.setTypeface(ResourcesCompat.getFont(typeET.getContext(), R.font.rubik_medium));
        final EditText totalAmountET = new EditText(this);
        totalAmountET.setHint("סה\"כ החומר");
        totalAmountET.setTextSize(18);
        totalAmountET.setGravity(Gravity.RIGHT);
        totalAmountET.setPadding(0,15,30,30);
        totalAmountET.setTypeface(ResourcesCompat.getFont(totalAmountET.getContext(), R.font.rubik_medium));
        totalAmountET.setInputType(InputType.TYPE_CLASS_NUMBER);
        AdScreen.addView(typeET);
        AdScreen.addView(totalAmountET);
        adb.setView(AdScreen);

        typeET.setText(materialsTitleList.get(pos));
        totalAmountET.setText(""+materialsTotalList.get(pos));

        adb.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!totalAmountET.getText().toString().isEmpty()){
                    if (checkMaterialInput(true, typeET.getText().toString(), Integer.parseInt(totalAmountET.getText().toString()))){
                        temp.setTypeOfMaterial(typeET.getText().toString());
                        temp.setTotalAmount(Integer.parseInt(totalAmountET.getText().toString()));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            allMaterials.replace(materialsTitleList.get(pos),temp);
                        }
                        refBusinessEqu.child("materials").setValue(allMaterials);

                        materialsTitleList.set(pos, temp.getTypeOfMaterial());
                        materialsTotalList.set(pos, temp.getTotalAmount());

                        customAdapterMaterial = new CustomAdapterMaterials(settingsActivity.this, materialsTitleList, materialsTotalList, materialsUsedList);
                        customAdapterMaterial.notifyDataSetChanged();
                        generalLV.setAdapter(customAdapterMaterial);
                    } else dialogInterface.dismiss();
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
     * Display materials in generalLV ListView object.
     *
     * @param view the materialsTV TextView object is being clicked.
     */
    public void displayMaterials(View view) {
        option = "ציוד";
        customAdapterMaterial = new CustomAdapterMaterials(this, materialsTitleList, materialsTotalList, materialsUsedList);
        customAdapterMaterial.notifyDataSetChanged();
        generalLV.setAdapter(customAdapterMaterial);
        showsTV.setTypeface(ResourcesCompat.getFont(showsTV.getContext(), R.font.rubik_regular));
        materialsTV.setTypeface(ResourcesCompat.getFont(showsTV.getContext(), R.font.rubik_semibold));

        if (materialsTitleList.isEmpty()){
            Snackbar.make(generalLV, "אין ציוד במערכת", 3000).show();
        }
    }

    /**
     * checkMaterialInput is responsible for check all the inputs that are related a Material object
     * @param updateMode (in updateMode or not)
     * @param typeOfMaterial the title of the CURRENT Material
     * @param totalAmount the amount of the CURRENT Material
     *
     * @return True if all the inputs are good. Otherwise, False
     */
    private boolean checkMaterialInput(boolean updateMode,String typeOfMaterial, int totalAmount) {
        boolean flag = true;
        if (typeOfMaterial.isEmpty()){
            flag = false;
            Snackbar.make(generalLV, "שדה לא יהיה ריק", 3000).show();
        } else if (materialsTitleList.contains(typeOfMaterial) && !updateMode){
            flag = false;
            Snackbar.make(generalLV, "חומר לא ירשם פעמיים", 3000).show();
        } else if (!(totalAmount > 0)){
            flag = false;
            Snackbar.make(generalLV, "כמות חומר הינה מספר טבעי", 3000).show();
        } else if (updateMode && totalAmount < materialsUsedList.get(materialsTitleList.indexOf(typeOfMaterial))){
            flag = false;
            Snackbar.make(generalLV, "כמות חומר שהתקבלה אינה הגיונית", 3000).show();
        }

        return flag;
    }

    /**
     * Create new show to save in the business system and to the FireBase DataBase.
     */
    public void createNewShow() {
        getAllShows();
        getAllMaterials();

        businessEqu.setMaterials(allMaterials);
        businessEqu.setTotalEmployees(dataList.get(0));
        businessEqu.setAvailableEmployees(dataList.get(1));
        businessEqu.setEfficiency(efficiency);

        Shows temp = new Shows();

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AdScreen.setPadding(15,0,15,0);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("מופע חדש");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);
        final EditText nameET = new EditText(this);
        nameET.setHint("שם המופע");
        nameET.setTextSize(18);
        nameET.setGravity(Gravity.RIGHT);
        nameET.setPadding(0,15,30,30);
        nameET.setTypeface(ResourcesCompat.getFont(nameET.getContext(), R.font.rubik_medium));
        final EditText costET = new EditText(this);
        costET.setHint("סה\"כ עלות המופע");
        costET.setTextSize(18);
        costET.setGravity(Gravity.RIGHT);
        costET.setPadding(0,15,30,30);
        costET.setTypeface(ResourcesCompat.getFont(costET.getContext(), R.font.rubik_medium));
        costET.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText desET = new EditText(this);
        desET.setHint("תוכן");
        desET.setTextSize(18);
        desET.setGravity(Gravity.RIGHT);
        desET.setPadding(0,15,30,30);
        desET.setTypeface(ResourcesCompat.getFont(desET.getContext(), R.font.rubik_medium));
        final EditText employeesET = new EditText(this);
        employeesET.setHint("כמות עובדים");
        employeesET.setTextSize(18);
        employeesET.setGravity(Gravity.RIGHT);
        employeesET.setPadding(0,15,30,30);
        employeesET.setTypeface(ResourcesCompat.getFont(employeesET.getContext(), R.font.rubik_medium));
        employeesET.setInputType(InputType.TYPE_CLASS_NUMBER);
        AdScreen.addView(nameET);
        AdScreen.addView(costET);
        AdScreen.addView(desET);
        AdScreen.addView(employeesET);
        adb.setView(AdScreen);

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!costET.getText().toString().isEmpty() && !employeesET.getText().toString().isEmpty()){
                    if (checkShowsInput(nameET.getText().toString(), desET.getText().toString(), Integer.parseInt(costET.getText().toString()), Integer.parseInt(employeesET.getText().toString()))){
                        temp.setShowTitle(nameET.getText().toString());
                        temp.setCost(Integer.parseInt(costET.getText().toString()));
                        temp.setDescription(desET.getText().toString());
                        temp.setEmployees(Integer.parseInt(employeesET.getText().toString()));
                        allShows.put(temp.getShowTitle(), temp);
                        businessEqu.setShowsList(allShows);
                        refBusinessEqu.setValue(businessEqu);

                        showTitlesList.add(temp.getShowTitle());
                        costsList.add(temp.getCost());
                        descriptionsList.add(temp.getDescription());
                        employeesList.add(temp.getEmployees());
                    } else dialogInterface.dismiss();
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
     * showInUsed is responsible to check if there is any show that appearing in a GREEN (also has accepted) event.
     * @param pos (the Current position of the show to check)
     *
     * @return True if the CURRENT show is in used. Otherwise, False
     */
    private boolean showInUsed(int pos) {
        boolean[] flag = {true};

        refGreen_Event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {

                for(DataSnapshot data : dS.getChildren()) {
                    if (flag[0]){
                        Event tempEvent = data.getValue(Event.class);

                        if (Objects.requireNonNull(tempEvent).isHasAccepted()){
                            flag[0] = Objects.requireNonNull(tempEvent).getEventShows().get(pos);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return flag[0];
    }

    /**
     * updateAShow method is for updating the shows
     * @param  pos the index of the show in the list.
     */
    @SuppressLint("SetTextI18n")
    private void updateAShow(int pos) {
        getAllShows();
        Shows temp = new Shows(allShows.get(showTitlesList.get(pos)).getShowTitle(), allShows.get(showTitlesList.get(pos)).getDescription(), allShows.get(showTitlesList.get(pos)).getCost(), allShows.get(showTitlesList.get(pos)).getEmployees());
        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AdScreen.setPadding(15,0,15,0);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("עדכון המופע - "+allShows.get(showTitlesList.get(pos)).getShowTitle());
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);
        final EditText nameET = new EditText(this);
        nameET.setHint("שם המופע");
        nameET.setTextSize(18);
        nameET.setGravity(Gravity.RIGHT);
        nameET.setPadding(0,15,30,30);
        nameET.setTypeface(ResourcesCompat.getFont(nameET.getContext(), R.font.rubik_medium));
        final EditText totalAmountET = new EditText(this);
        totalAmountET.setHint("סה\"כ עלות המופע");
        totalAmountET.setTextSize(18);
        totalAmountET.setGravity(Gravity.RIGHT);
        totalAmountET.setPadding(0,15,30,30);
        totalAmountET.setTypeface(ResourcesCompat.getFont(totalAmountET.getContext(), R.font.rubik_medium));
        totalAmountET.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText desET = new EditText(this);
        desET.setHint("תוכן");
        desET.setTextSize(18);
        desET.setGravity(Gravity.RIGHT);
        desET.setPadding(0,15,30,30);
        desET.setTypeface(ResourcesCompat.getFont(desET.getContext(), R.font.rubik_medium));
        final EditText employeesET = new EditText(this);
        employeesET.setHint("כמות עובדים");
        employeesET.setTextSize(18);
        employeesET.setGravity(Gravity.RIGHT);
        employeesET.setPadding(0,15,30,30);
        employeesET.setTypeface(ResourcesCompat.getFont(employeesET.getContext(), R.font.rubik_medium));
        employeesET.setInputType(InputType.TYPE_CLASS_NUMBER);

        AdScreen.addView(nameET);
        AdScreen.addView(totalAmountET);
        AdScreen.addView(desET);
        AdScreen.addView(employeesET);
        adb.setView(AdScreen);

        nameET.setText(showTitlesList.get(pos));
        totalAmountET.setText(""+costsList.get(pos));
        desET.setText(descriptionsList.get(pos));
        employeesET.setText(""+employeesList.get(pos));

        adb.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (checkShowsInput(nameET.getText().toString(), desET.getText().toString(), Integer.parseInt(totalAmountET.getText().toString()), Integer.parseInt(employeesET.getText().toString()))){
                    temp.setShowTitle(nameET.getText().toString());
                    temp.setCost(Integer.parseInt(totalAmountET.getText().toString()));
                    temp.setDescription(desET.getText().toString());
                    temp.setEmployees(Integer.parseInt(employeesET.getText().toString()));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        allShows.replace(showTitlesList.get(pos),temp);
                    }
                    refBusinessEqu.child("showsList").setValue(allShows);

                    showTitlesList.set(pos, temp.getShowTitle());
                    costsList.set(pos, temp.getCost());
                    descriptionsList.set(pos, temp.getDescription());
                    employeesList.set(pos, temp.getEmployees());

                    customAdapterShows = new CustomAdapterShows(getApplicationContext(), showTitlesList, descriptionsList, costsList, employeesList);
                    customAdapterShows.notifyDataSetChanged();
                    generalLV.setAdapter(customAdapterShows);
                } else dialogInterface.dismiss();
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
     * Display shows in generalLV ListView object.
     *
     * @param view the showsTV TextView object is being clicked.
     */
    public void displayShows(View view) {
        option = "מופעים";
        customAdapterShows = new CustomAdapterShows(getApplicationContext(), showTitlesList, descriptionsList, costsList, employeesList);
        customAdapterShows.notifyDataSetChanged();
        generalLV.setAdapter(customAdapterShows);
        showsTV.setTypeface(ResourcesCompat.getFont(showsTV.getContext(), R.font.rubik_semibold));
        materialsTV.setTypeface(ResourcesCompat.getFont(showsTV.getContext(), R.font.rubik_regular));

        if (showTitlesList.isEmpty()){
            Snackbar.make(generalLV, "אין מופעים", 3000).show();
        }
    }

    /**
     * checkShowsInput is responsible for check all the inputs that are related a Show object
     * @param titleShow the title of the CURRENT show
     * @param description the title of the CURRENT show
     * @param cost the cost in (NIS) of the CURRENT show
     * @param employees the amount of employees of the CURRENT show
     *
     * @return True if all the inputs are good. Otherwise, False
     */
    private boolean checkShowsInput(String titleShow, String description, int cost, int employees) {
        boolean flag = true;
        if (titleShow.isEmpty() || description.isEmpty()){
            flag = false;
            Snackbar.make(generalLV, "שדה לא יהיה ריק", 3000).show();
        }
        else if (!(cost > 0) || !(employees > 0)){
            flag = false;
            Snackbar.make(generalLV, "שדה יהיה מספר גדול מאפס", 3000).show();
        }
        return flag;
    }

    /**
     * This method update the total employees amount that exists n the business system and to the FireBase DataBase.
     * @param view the totalTV TextView object that being clicked.
     */
    @SuppressLint("SetTextI18n")
    public void updateTotalEmployees(View view) {
        allShows.clear();
        allMaterials.clear();
        getAllShows();
        getAllMaterials();

        businessEqu.setMaterials(allMaterials);
        businessEqu.setTotalEmployees(dataList.get(0));
        businessEqu.setAvailableEmployees(dataList.get(1));
        businessEqu.setEfficiency(setEfficiency());
        businessEqu.setShowsList(allShows);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("סה\"כ עובדים ברוטו");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);

        final EditText valueET = new EditText(this);
        valueET.setHint("ערך");
        valueET.setTextSize(18);
        valueET.setGravity(Gravity.RIGHT);
        valueET.setPadding(0,15,30,30);
        valueET.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        valueET.setInputType(InputType.TYPE_CLASS_NUMBER);
        adb.setTitle(keysList.get(0));

        adb.setView(valueET);

        valueET.setText(""+dataList.get(0));
        adb.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Updating also the available employees in the FireBase and to dispaly
                int previousTotalEmployees = dataList.get(0);
                int currentTotalEmployees = Integer.parseInt(valueET.getText().toString());
                if (currentTotalEmployees > businessEqu.getAvailableEmployees() || previousTotalEmployees - businessEqu.getAvailableEmployees() == 0){
                    businessEqu.setAvailableEmployees(previousTotalEmployees + (currentTotalEmployees - previousTotalEmployees)); // Adding the change between the previous and the CURRENT.
                    dataList.set(0, Integer.parseInt(valueET.getText().toString()));
                    businessEqu.setTotalEmployees(dataList.get(0));

                    refBusinessEqu.setValue(businessEqu);

                    efficiencyTV.setText(businessEqu.getEfficiency()+"%");
                    efficiency = setEfficiency();
                    availableTV.setText(businessEqu.getAvailableEmployees()+"");
                    totalTV.setText(businessEqu.getTotalEmployees()+"");
                } else Snackbar.make(generalLV, "לא ניתן להפחית מכמות עובדים אשר משומשת", 3000).show();
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

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
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

        // Making an action its type that is saved in the option variable
        if (option.equals("הסר חומר")){
            if (materialsUsedList.get(pos) == 0){
                refBusinessEqu.child("materials").child(materialsTitleList.get(pos)).removeValue();
                allMaterials.remove(materialsTitleList.get(pos));
                materialsTitleList.remove(pos);
                materialsTotalList.remove(pos);
                materialsUsedList.remove(pos);
                customAdapterMaterial.notifyDataSetChanged();
                generalLV.setAdapter(customAdapterMaterial);
                Snackbar.make(generalLV, "הציוד נמחק בהצלחה", 3000).show();
            } else Snackbar.make(generalLV, "הציוד המבוקש משומש", 3000).show();
        }
        else if (option.equals("עדכן חומר")){
            updateAMaterial(pos);
        }
        else if (option.equals("הסר מופע")){
            if(!showInUsed(pos)){
                refBusinessEqu.child("showsList").child(showTitlesList.get(pos)).removeValue();
                allShows.remove(showTitlesList.get(pos));
                showTitlesList.remove(pos);
                costsList.remove(pos);
                descriptionsList.remove(pos);
                employeesList.remove(pos);
                customAdapterShows.notifyDataSetChanged();
                generalLV.setAdapter(customAdapterShows);
                Snackbar.make(generalLV, "המופע נמחק בהצלחה", 3000).show();
            }
            else Snackbar.make(generalLV, "המופע המבוקש משומש", 3000).show();
        }
        else if (option.equals("עדכן מופע")){
            updateAShow(pos);
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Move to previous activity *only* when the back button is pressed.
     */
    public void moveToPreviousAct() {
        super.onBackPressed();
    }

    /**
     * Move to create an event. when the FloatingActionButton is pressed.
     * @param view the FloatingActionButton
     */
    public void moveToCreateAnEvent(View view) {
        Intent si = new Intent(this, newEventActivity.class);
        startActivity(si);
    }

    /**
     * Logout method for logout from the user.
     * @param item the item in the layout_top_bar.xml
     */
    public void Logout(MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("יציאה ממערכת");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);

        final TextView messageTV = new TextView(this);
        messageTV.setText("החשבון "+FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))+" ינותק.");
        messageTV.setTextSize(18);
        messageTV.setGravity(Gravity.RIGHT);
        messageTV.setPadding(0,15,30,0);
        messageTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        adb.setView(messageTV);

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("צא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent si = new Intent(settingsActivity.this, LoginActivity.class);

                // Changing the preferences to default
                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("stayConnect",false);
                editor.apply();

                startActivity(si);
                finish();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }
}