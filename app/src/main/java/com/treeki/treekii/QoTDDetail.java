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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class QoTDDetail extends AppCompatActivity {
    private CheckBox priv;
    private CheckBox fav;

    private String TAG = "QoTDDetail";
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    ViewSwitcher switcher;
    Button delete;
    Button edit;
    Button save;

    TextView c;
    TextView d;
    TextView q;
    EditText edit_content;

    String question;
    String month;
    String day;
    String year;
    String today;
    String date;
    String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qotd_detail);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH) + 1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        today = month + "-" + day + "-" + year;
    }
    protected void onStart() {
        super.onStart();

        Intent i = getIntent();

        content = i.getStringExtra("content");
        date = i.getStringExtra("date");
        question = i.getStringExtra("question");
        Boolean faved = i.getBooleanExtra("favorite",false);
        Boolean checked = i.getBooleanExtra("private",false);


        setTitle(date);
        save = (Button) findViewById(R.id.save);
        c = (TextView) findViewById(R.id.content);
        q = (TextView) findViewById(R.id.question);
        c.setText(content);
        q.setText(question);

        priv = (CheckBox) findViewById(R.id.priv);
        priv.setChecked(checked);
        priv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            //set onclick handler for checkbox
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked)
            {
                mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("private").setValue(isChecked);
            }
        });


        fav = (CheckBox) findViewById(R.id.fav);
        fav.setChecked(faved);
        fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            //set onclick handler for checkbox
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked)
            {
                mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("favorite").setValue(isChecked);
            }
        });



        edit = (Button) findViewById(R.id.edit);
        delete = (Button) findViewById(R.id.delete);
        //if QoTD isn't from today, don't let them edit/delete
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
                            mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("answer").setValue(new_content);
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
        mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").removeValue();
        Intent PastQoTDs = new Intent(QoTDDetail.this, PastQoTD.class);
        startActivity(PastQoTDs);
        finish();
    }
}