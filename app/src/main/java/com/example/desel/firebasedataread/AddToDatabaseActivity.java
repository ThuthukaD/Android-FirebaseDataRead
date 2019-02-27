package com.example.desel.firebasedataread;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddToDatabaseActivity extends AppCompatActivity
{
    // VARIABLES
    // Debugging
    private static final String TAG = "AddToDatabaseActivity";

    // Edit Text
    public EditText etFood;

    // Buttons
    public Button btnAdd;

    // Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_database);

        // Edit Texts
        etFood = findViewById(R.id.etFood);

        // Buttons
        btnAdd = findViewById(R.id.btnAdd);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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
                    toastMEssage("Signed in with " + user.getEmail());
                }
                else
                {
                    // Log for debugging
                    Log.d(TAG, "onAuthStateChanged: User signed out");
                }
            }
        };

        // Writing to the database
        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
                toastMEssage("Data Added");
            }

            // Failed to read value
            @Override
            public void onCancelled(DatabaseError error)
            {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // For debugging
                Log.d(TAG, "onClick: Attempting to add new item");

                // Getting text from user
                String newFood = etFood.getText().toString().trim();

                if (!newFood.equals(""))
                {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    myRef.child(userID).child("Food").child("Favorite Foods")
                            .child(newFood).setValue(true);

                    toastMEssage("Adding " + newFood + " to database...");

                    //Reset text
                    etFood.setText(null);
                }
                else
                {
                    toastMEssage("Please input something");
                }
            }
        });
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

