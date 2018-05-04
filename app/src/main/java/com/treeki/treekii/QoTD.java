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
        final String date = "0101";

        QoTD = findViewById(R.id.QoTD);
//        answer = findViewById(R.id.answer);

        //get Database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Questions").child("1").child("1").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/0101
                        String question = dataSnapshot.getValue(String.class);

                        //error handling
                        if (question == null) {
                            Log.e(TAG, "Question at"+date+" is unexpectedly null");
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
