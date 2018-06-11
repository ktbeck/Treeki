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

import java.util.ArrayList;
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
    String source;
    ArrayList<String> dates_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        getSupportActionBar().hide();

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        today = month+"-"+day+"-"+year;

        Intent i = getIntent();
        source = i.getStringExtra("source");
        content = i.getStringExtra("content");
        date = i.getStringExtra("date");
        Boolean other = i.getBooleanExtra("other",false);
        Boolean checked = i.getBooleanExtra("private",false);
        Boolean faved = i.getBooleanExtra("favorite",false);

        setTitle(date);
        save = (Button) findViewById(R.id.save);
        c = (TextView) findViewById(R.id.content);
        c.setText(content);

        priv = (CheckBox) findViewById(R.id.priv);
        fav = (CheckBox) findViewById(R.id.fav);
        if (!other) {
            priv.setChecked(checked);
            priv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                //set onclick handler for checkbox
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("private").setValue(isChecked);
                }
            });


            fav.setChecked(faved);
            fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                //set onclick handler for checkbox
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("favorite").setValue(isChecked);
                }
            });
        }
        else{
            priv.setVisibility(View.GONE);
            fav.setVisibility(View.GONE);
        }



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
        Intent prev;
        Log.i(TAG,"source: "+source);
        if (source.equals("answeredQoTD")) {
            dates_ = getIntent().getStringArrayListExtra("dates");
            dates_.remove(dates_.size() - 1);
            prev = new Intent(JournalDetail.this, answeredQoTD.class);
            prev.putExtra("source","Journal");
            prev.putStringArrayListExtra("dates", dates_);
        }
        else {
            prev = new Intent(JournalDetail.this, PastJournals.class);
        }
        startActivity(prev);
        finish();
    }
}