package com.treeki.treekii;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class QoTD extends AppCompatActivity {

    //initialize
    private TextView QoTD;
//    private EditText answer;
    private DatabaseReference mDatabase;
    private static final String TAG = "QoTD_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qotd);
        //get date

        QoTD = findViewById(R.id.QoTD);
//        answer = findViewById(R.id.answer);

        //get Database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        Date date = null; // your date
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        int month_ = cal.get(Calendar.MONTH);
//        final String month = Integer.toString(month_);
//        int day_ = cal.get(Calendar.DAY_OF_MONTH);
//        final String day = Integer.toString(day_);
        final String month = "2";
        final String day = "5";
        mDatabase.child("Questions").child(month).child(day).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/0101
                        String question = dataSnapshot.getValue(String.class);

                        //error handling
                        if (question == null) {
                            Log.e(TAG, "Question at"+month+"/"+day+" is unexpectedly null");
                            Toast.makeText(getApplicationContext(),"can't fetch question",Toast.LENGTH_SHORT).show();
                        }
                        //if no err, change the question
                        else {
                            //change question
                            QoTD.setText(question);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get Question onCancelled", databaseError.toException());
                    }
                }
        );
    }
}
