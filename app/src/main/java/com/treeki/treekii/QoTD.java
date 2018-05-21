package com.treeki.treekii;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    String question;
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

        question = getIntent().getStringExtra("question");
        Log.i(TAG,"QoTD: "+question);
        QoTD.setText(question);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /* Here is the event handler for the menu button that I forgot in class.
    The value returned by item.getItemID() is
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, SignInRegister.class);
                startActivity(i);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

}
