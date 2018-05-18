package com.treeki.treekii;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseUser user;
    private static final String TAG = "QoTD_Activity";
    private Spinner spinner;
    String month;
    String day;
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qotd);

        spinner = (Spinner) findViewById(R.id.spinner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        //get date
        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));

        QoTD = findViewById(R.id.QoTD);
        answer_edit = findViewById(R.id.answer);

        //get Database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
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

    public void submit(View view) {
        if (month.length() == 1) month = "0"+month;
        if (day.length() == 1) day = "0"+day;
        String date = month+"-"+day+"-"+year;
        String mood = String.valueOf(spinner.getSelectedItem());

        //Save the answer
        answer = answer_edit.getText().toString();
        if (!answer.equals("")){
            mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("answer").setValue(answer);
            mDatabase.child("Users").child(user.getUid()).child(date).child("mood").setValue(mood);
            Toast.makeText(getApplicationContext(), "Answer submitted!", Toast.LENGTH_SHORT).show();
            startJournal();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please input an answer.", Toast.LENGTH_SHORT).show();
        }

    }

    private void startJournal() {
        Intent journal = new Intent(this,Journal.class);
        startActivity(journal);
    }
}
