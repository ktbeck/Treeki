package com.treeki.treekii;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SplashActivity extends Activity {
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "SplashActivity";

    String month;
    String day;
    String year;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        if (month.length() == 1) month = "0"+month;
        if (day.length() == 1) day = "0"+day;
        date = month+"-"+day+"-"+year;

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goToNextActivity();
        }
    }
    private void startQoTD() {
        Intent intent = new Intent(SplashActivity.this,QoTD.class);
        startActivity(intent);
    }
    private void startJournal() {
        Intent intent = new Intent(SplashActivity.this,Journal.class);
        startActivity(intent);
    }
    private void startMain() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void goToNextActivity() {
        Log.i(TAG,"Signed in: "+user.getUid());

        mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("answer").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/0101
                        String answer = dataSnapshot.getValue(String.class);

                        //error handling
                        if (answer == null) {
                            Log.i(TAG,"User signed in but has not filled in QoTD.");
                            startQoTD();
                        }

                        else {

                            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //get question at /Questions/0101
                                            String answer = dataSnapshot.getValue(String.class);

                                            //error handling
                                            if (answer == null) {
                                                Log.i(TAG,"User signed in and finished QoTD but not journal");
                                                startJournal();
                                            }
                                            //if no err, change the question
                                            else {
                                                //GOTO MAIN MENU
                                                //TODO: MAIN MENU INTENT GOES HERE

                                                startMain();
                                                                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "get Journal answer onCancelled", databaseError.toException());
                                        }
                                    }
                            );


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get QoTD answer onCancelled", databaseError.toException());
                    }
                }
        );


    }
}
