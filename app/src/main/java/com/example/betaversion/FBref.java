package com.example.betaversion;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {

    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refReminders = FBDB.getReference("Reminder");
    public static DatabaseReference refBusinessEqu = FBDB.getReference("BusinessEqu");
    public static DatabaseReference refEnd_Event = FBDB.getReference("End_Event");
    public static DatabaseReference reflive_Event = FBDB.getReference("Live_Event");
}