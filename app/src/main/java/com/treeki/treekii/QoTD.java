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

import java.util.Calendar;


public class QoTD extends AppCompatActivity {

    //initialize
    private TextView QoTD;
    private EditText answer_edit;
    private String answer;
    private DatabaseReference mDatabase;
    private static final String TAG = "QoTD_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qotd);
        //get date
        Calendar cal = Calendar.getInstance();
        final String month = Integer.toString(cal.get(Calendar.MONTH)+1);
        final String day = Integer.toString(cal.get(Calendar.DATE));

        QoTD = findViewById(R.id.QoTD);
        answer_edit = findViewById(R.id.answer);

        //get Database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        //Save the answer
        answer = answer_edit.getText().toString();
        
    }
}
