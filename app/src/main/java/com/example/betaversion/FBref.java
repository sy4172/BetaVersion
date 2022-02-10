package com.example.betaversion;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;


public class FBref {

    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final StorageReference storageRef = storage.getReference();

    public static DatabaseReference refReminders = FBDB.getReference("Reminder");
    public static DatabaseReference refBusinessEqu = FBDB.getReference("BusinessEqu");
    public static DatabaseReference refEnd_Event = FBDB.getReference("End_Event");
    public static DatabaseReference reflive_Event = FBDB.getReference("Live_Event");

    public static DatabaseReference refGreen_Event = reflive_Event.child("greenEvent");
    public static DatabaseReference refOrange_Event = reflive_Event.child("orangeEvent");

}