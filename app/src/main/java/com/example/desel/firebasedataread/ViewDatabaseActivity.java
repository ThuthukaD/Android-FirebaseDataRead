package com.example.desel.firebasedataread;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewDatabaseActivity extends AppCompatActivity
{
    // VARIABLES
    // Debugging
    private static final String TAG = "ViewDatabaseActivity";

    // Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    // Strings
    private String userID;

    // Other
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_database);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // Strings
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        // ListView
        listView = findViewById(R.id.listView);

        // WORK
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    // User is signed in
                    // Log for debugging
                    Log.d(TAG, "onAuthStateChanged: User "
                            + user.getEmail() + " (" + user.getUid() + ")"
                            + " is signed in");

                    // Toast for user - Successful Sign in
                    toastMEssage("signed in with " + user.getEmail());
                }
                else
                {
                    // Log for debugging
                    Log.d(TAG, "onAuthStateChanged: User signed out");
                    toastMEssage("Not Connected");
                }
            }
        };

        // Writing to the database - Automatically when activity starts
        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                showData(dataSnapshot);
            }

            // Failed to read value
            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // Data snapshot takes snapshot of the database
    private void showData(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            UserInformaation uInfo = new UserInformaation();

            try 
            {
                uInfo.setName(ds.child(userID)
                        .getValue(UserInformaation.class).getName());
                uInfo.setEmail(ds.child(userID)
                        .getValue(UserInformaation.class).getEmail());
                uInfo.setPhone_num(ds.child(userID)
                        .getValue(UserInformaation.class).getPhone_num());
            } 
            catch (Exception e) 
            {
                Log.d(TAG, "showData: Null Issue but ran");
            }

            Log.d(TAG, "showData: name: " + uInfo.getName());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: phone number: " + uInfo.getPhone_num());

            ArrayList<String> array = new ArrayList<>();

            array.add(uInfo.getName());
            array.add(uInfo.getEmail());
            array.add(uInfo.getPhone_num());

            // Changeable
            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, array);
            listView.setAdapter(adapter);
        }
    }

    // Required
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // Required
    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // Toast Method for custom messages
    private void toastMEssage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
