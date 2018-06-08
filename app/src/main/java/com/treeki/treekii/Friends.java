package com.treeki.treekii;

import android.content.Intent;
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

import java.util.ArrayList;

public class Friends extends AppCompatActivity {
    private String TAG = "Friends";
    private EditText username_;
    private Button search_;
    private FirebaseUser user;
    String username;
    String f_user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        username_ = (EditText) findViewById(R.id.Username);
        search_ = (Button) findViewById(R.id.search);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void search(View view) {
        username = username_.getText().toString();
//        Log.i(TAG,"Username: "+username);

        mDatabase.child("Users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            f_user = childSnapshot.getKey();
                            String f_username = childSnapshot.child("Username").getValue(String.class);
                            if (f_username != null && f_username.equals(username)) {
                                mDatabase.child("Friends").child(user.getUid()).child(f_user).setValue(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG,"checkpast cancelled");
                    }
                }
        );
    }
}
