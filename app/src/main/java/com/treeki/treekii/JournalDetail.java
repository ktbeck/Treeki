package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class JournalDetail extends AppCompatActivity {
    private CheckBox priv;
    private CheckBox fav;

    private String TAG = "JournalDetail";
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    ViewSwitcher switcher;
    Button delete;
    Button edit;
    Button save;

    TextView c;
    TextView d;
    EditText edit_content;

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

        save = (Button) findViewById(R.id.save);
        c = (TextView) findViewById(R.id.content);
        d = (TextView) findViewById(R.id.dateView);
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
                mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("private").setValue(isChecked);
            }
        });


        fav = (CheckBox) findViewById(R.id.fav);
        fav.setChecked(checked);
        fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            //set onclick handler for checkbox
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked)
            {
                mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("favorite").setValue(isChecked);
            }
        });



        edit = (Button) findViewById(R.id.edit);
        delete = (Button) findViewById(R.id.delete);
        //if journal isn't from today, don't let them edit/delete
        if (!date.equals(today)){
            edit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }
        else {
            //edit onclick listener
            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //make buttons disappear, make save appear
                    v.setVisibility(View.INVISIBLE);
                    delete.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.VISIBLE);
                    //switch viewText to editText, prepopulate
                    switcher = (ViewSwitcher) findViewById(R.id.switcher);
                    switcher.showNext();
                    edit_content = (EditText) switcher.findViewById(R.id.editContent);
                    edit_content.setText(content);

                    //save onclick handler
                    save.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //make save button disappear, make others reappear
                            v.setVisibility(View.INVISIBLE);
                            delete.setVisibility(View.VISIBLE);
                            edit.setVisibility(View.VISIBLE);
                            //switch editText to textView, with proper info
                            switcher.showNext();
                            String new_content = edit_content.getText().toString();
                            c.setText(new_content);
                            //save to db
                            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").setValue(new_content);
                            content = new_content;
                        }
                    });
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    back();
                }
            });
        }

    }
    private void back() {
        mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").removeValue();
        Intent PastJournals = new Intent(JournalDetail.this, PastJournals.class);
        startActivity(PastJournals);
        finish();
    }
}