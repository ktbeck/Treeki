package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class JournalDetail extends AppCompatActivity {
    private CheckBox priv;
    private String TAG = "JournalDetail";
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    String month;
    String day;
    String year;
    String today;
    String date;
    String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        if (month.length() == 1) month = "0"+month;
        if (day.length() == 1) day = "0"+day;
        today = month+"-"+day+"-"+year;

        Intent i = getIntent();
        content = i.getStringExtra("content");
        date = i.getStringExtra("date");
        Boolean checked = i.getBooleanExtra("private",false);

        TextView c = (TextView) findViewById(R.id.jourContent);
        TextView d = (TextView) findViewById(R.id.dateView);
        c.setText(content);
        d.setText(date);

        priv = (CheckBox) findViewById(R.id.priv);
        priv.setChecked(checked);
        priv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            //set onclick handler for checkbox
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked)
            {
                Button save = (Button) findViewById(R.id.save);
                save.setVisibility(View.VISIBLE);

                //onclick handler for save button
                save.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.setVisibility(View.INVISIBLE);
                    mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("private").setValue(isChecked);
                }
            });

            }
        });

        Button edit = (Button) findViewById(R.id.edit);
        Button delete = (Button) findViewById(R.id.delete);
        if (!date.equals(today)){
            edit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }

        //TODO: Add edit and delete button funcitnoality

        //Todo: Make it so private/public is editable whenever.
    }
}