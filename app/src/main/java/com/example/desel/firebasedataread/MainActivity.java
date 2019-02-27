package com.example.desel.firebasedataread;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    // VARIABLES
    // Debugging
    private static final String TAG = "MainActivity";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Text Views
    public TextView tvEmail;
    public TextView tvPassword;

    // Buttons
    public Button btnSignin;
    public Button btnSignout;
    public Button btnAddToDatabase;
    public Button btnViewDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TYPE CASTING
        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Text Views
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);

        // Buttons
        btnSignin = findViewById(R.id.btnSignin);
        btnSignout = findViewById(R.id.btnSignout);
        btnAddToDatabase = findViewById(R.id.btnAddToDatabase);
        btnViewDatabase = findViewById(R.id.btnViewData);

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
                    Toast.makeText(MainActivity.this,
                            "signed in with " + user.getEmail()
                            , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Log for debugging
                    Log.d(TAG, "onAuthStateChanged: User signed out");
                }
            }
        };

        btnSignin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Getting Text from Text Views
                String email = tvEmail.getText().toString().trim();
                String password = tvPassword.getText().toString().trim();

                if (!email.equals("") && !password.equals(""))
                {
                    mAuth.signInWithEmailAndPassword(email, password);
                }
                else
                {
                    Toast.makeText(MainActivity.this,
                            "Cant leave blank fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                Toast.makeText(MainActivity.this,
                        "Signing out...", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToDatabase.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AddToDatabaseActivity.class);
                startActivity(intent);
            }
        });

        btnViewDatabase.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, ViewDatabaseActivity.class);
                startActivity(intent);
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
}
