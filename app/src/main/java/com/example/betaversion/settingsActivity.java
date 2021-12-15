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
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
public class settingsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener, AdapterView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    Button buttonSelection;
    ListView materialsLV, showsLV, generalLV;

    Spinner spinner; // The Spinner to sort the display
    TextView titleChoose;

    ArrayList<String> keysList, materialsKeyList, showsKeyList;

    ArrayList<Integer> dataList, materialsDataList, showsDataList;


    ArrayList<Material> allMaterials; // Summarize all the Material objects that were created
    ArrayList<Shows> allShows;// Summarize all the Shows objects that were created

    CustomAdapterSettings customAdapterSettings1; // For the general properties
    CustomAdapterSettings customAdapterSettings2; // For the materialsLV
    CustomAdapterSettings customAdapterSettings3; // For the showsLV

    /**
     * The Generate list for the selection.
     */
    String [] generateList = new String[]{"נתונים כללים","ציוד","מופעים"};
    /**
     * The Option that selected.
     */
    String option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        generalLV = findViewById(R.id.generalLV);
        spinner = findViewById(R.id.spinner);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        titleChoose = findViewById(R.id.titleChoose);
        buttonSelection = findViewById(R.id.buttonSelection);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        generalLV.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        generalLV.setOnCreateContextMenuListener(this);

        keysList = new ArrayList<>();
        dataList = new ArrayList<>();

        materialsKeyList = new ArrayList<>();
        materialsDataList = new ArrayList<>();

        showsKeyList = new ArrayList<>();
        showsDataList = new ArrayList<>();

        allMaterials = new ArrayList<>();
        allShows = new ArrayList<>();

        ArrayAdapter<String> adpSpinner = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, generateList);
        spinner.setAdapter(adpSpinner);
        buttonSelection.setVisibility(View.INVISIBLE);
    }

    private void getAllShows() {
        refBusinessEqu.child("Shows").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                showsKeyList.clear();
                showsDataList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Shows tempShow = data.getValue(Shows.class);
                    allShows.add(tempShow);

                    showsKeyList.add(Objects.requireNonNull(tempShow).getShowTitle());
                    showsDataList.add(tempShow.getCost());
                }
                customAdapterSettings1 = new CustomAdapterSettings(getApplicationContext(), showsKeyList, showsDataList);
                customAdapterSettings1.notifyDataSetChanged();
                generalLV.setAdapter(customAdapterSettings1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAllMaterials() {
        refBusinessEqu.child("Materials").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                materialsKeyList.clear();
                materialsDataList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    Material temp = data.getValue(Material.class);
                    allMaterials.add(temp);

                    materialsKeyList.add(Objects.requireNonNull(temp).getTypeOfMaterial());
                    materialsDataList.add(temp.getTotalAmount());
                }

                customAdapterSettings2 = new CustomAdapterSettings(getApplicationContext(), materialsKeyList, materialsDataList);
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
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                dataList.clear();
                keysList.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    keysList.add("סה\"כ עובדים");
                    dataList.add(dS.child("totalEmployees").getValue(Integer.class));

                    keysList.add("סה\"כ עובדים זמינים");
                    dataList.add(dS.child("availableEmployees").getValue(Integer.class));

                    keysList.add("נצילות החומרים");
                    dataList.add(dS.child("efficiency").getValue(Integer.class));
                }

                customAdapterSettings3 = new CustomAdapterSettings(getApplicationContext(), keysList, dataList);
                customAdapterSettings3.notifyDataSetChanged();
                generalLV.setAdapter(customAdapterSettings3);
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
                if (!typeET.getText().toString().isEmpty() && !totalAmountET.getText().toString().isEmpty()){
                    temp.setTypeOfMaterial(typeET.getText().toString());
                    temp.setTotalAmount(Integer.parseInt(totalAmountET.getText().toString()));

                    allMaterials.add(temp);
                    refBusinessEqu.child("Materials").setValue(allMaterials);

                    materialsKeyList.add(temp.getTypeOfMaterial());
                    materialsDataList.add(temp.getTotalAmount());

                    customAdapterSettings2 = new CustomAdapterSettings(getApplicationContext(), materialsKeyList, materialsDataList);
                    customAdapterSettings2.notifyDataSetChanged();
                    materialsLV.setAdapter(customAdapterSettings2);
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
     * Create new show to save in the business system.
     */
    public void createNewShow() {
        Shows temp = new Shows();

        LinearLayout AdScreen = new LinearLayout(this);
        AdScreen.setOrientation(LinearLayout.VERTICAL);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("מופע חדש");
        final EditText nameET = new EditText(this);
        nameET.setHint("שם המופע");
        final EditText costET = new EditText(this);
        costET.setHint("עלות");
        AdScreen.addView(nameET);
        AdScreen.addView(costET);
        adb.setView(AdScreen);

        adb.setPositiveButton("אשר", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameET.getText().toString().isEmpty() && !costET.getText().toString().isEmpty()){
                    temp.setShowTitle(nameET.getText().toString());
                    temp.setCost(Integer.parseInt(costET.getText().toString()));

                    allShows.add(temp);
                    refBusinessEqu.child("Shows").setValue(allShows);

                    showsKeyList.add(temp.getShowTitle());
                    showsDataList.add(temp.getCost());

                    customAdapterSettings3 = new CustomAdapterSettings(getApplicationContext(), showsKeyList, showsDataList);
                    customAdapterSettings3.notifyDataSetChanged();
                    showsLV.setAdapter(customAdapterSettings3);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);

        if (v == materialsLV){
            menu.add("הסר חומר");
            menu.add("עדכן חומר");
        }
        else if (v == showsLV){
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
            allMaterials.remove(pos);
            customAdapterSettings2.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterSettings2);
            refBusinessEqu.child("Materials").child(String.valueOf(pos)).removeValue();
        }
        else if (option.equals("עדכן חומר")){
            updateAMaterial(pos);
        }
        else if (option.equals("הסר מופע")){
            showsKeyList.remove(pos);
            showsDataList.remove(pos);
            allShows.remove(pos);
            customAdapterSettings3.notifyDataSetChanged();
            generalLV.setAdapter(customAdapterSettings3);
            refBusinessEqu.child("Shows").child(String.valueOf(pos)).removeValue();
        }
        else if (option.equals("עדכן מופע")){
            updateAShow(pos);
        }

        return super.onContextItemSelected(item);
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
        AdScreen.addView(nameET);
        AdScreen.addView(totalAmountET);
        adb.setView(AdScreen);

        nameET.setText(showsKeyList.get(pos));
        totalAmountET.setText(""+showsDataList.get(pos));

        adb.setPositiveButton("עדכן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameET.getText().toString().isEmpty() && !totalAmountET.getText().toString().isEmpty()){
                    temp.setShowTitle(nameET.getText().toString());
                    temp.setCost(Integer.parseInt(totalAmountET.getText().toString()));

                    allShows.set(pos,temp);
                    refBusinessEqu.child("Shows").setValue(allShows);

                    showsKeyList.set(pos, temp.getShowTitle());
                    showsDataList.set(pos, temp.getCost());

                    customAdapterSettings3 = new CustomAdapterSettings(getApplicationContext(), showsKeyList, showsDataList);
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
                if (!typeET.getText().toString().isEmpty() && !totalAmountET.getText().toString().isEmpty()){
                    temp.setTypeOfMaterial(typeET.getText().toString());
                    temp.setTotalAmount(Integer.parseInt(totalAmountET.getText().toString()));

                    allMaterials.set(pos,temp);
                    refBusinessEqu.child("Materials").setValue(allMaterials);

                    materialsKeyList.set(pos, temp.getTypeOfMaterial());
                    materialsDataList.set(pos, temp.getTotalAmount());

                    customAdapterSettings2 = new CustomAdapterSettings(getApplicationContext(), materialsKeyList, materialsDataList);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        option = generateList[i];

        if (option.equals("נתונים כללים")){
            buttonSelection.setVisibility(View.INVISIBLE);
            titleChoose.setText("נתונים כללים");
            getAllSysData();
        }
        else if (option.equals("ציוד")){
            buttonSelection.setVisibility(View.VISIBLE);
            titleChoose.setText("ציוד");
            getAllMaterials();
        }
        else if (option.equals("מופעים")){
            buttonSelection.setVisibility(View.VISIBLE);
            titleChoose.setText("מופעים");
            getAllShows();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Create new item according the selection.
     *
     * @param view the view
     */
    public void createNewItem(View view) {
        if (option.equals("מופעים")) createNewShow();
        else if (option.equals("ציוד")) createNewMaterial();
    }
}
