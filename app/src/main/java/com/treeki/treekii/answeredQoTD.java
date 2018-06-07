package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class answeredQoTD extends AppCompatActivity {
    private String TAG = "AnsweredQoTD";
    private ListView mListView;
    private FirebaseUser user;
    String date;
    String[] dates;
    ArrayList<String> entries_ = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"in answered QOTD");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        setTitle("Past Answers");
        ArrayList<String> dates_ = getIntent().getStringArrayListExtra("dates");
        dates = new String[dates_.size()];
        for (int i = 0; i < dates_.size(); i++){
            dates[i] = dates_.get(i);
        }
        mListView = (ListView) findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (int i = 0; i < dates.length; i++) {
                            date = dates[i];
                            String journal = dataSnapshot.child(date).child("QoTD").child("answer").getValue(String.class); //get journal
                            Log.i(TAG, "journal: " + journal);
                            String display;
                            if (journal != null) { //if journal not null ie valid, truncate if >40
                                display = date + "\n" + journal;
                                Log.i(TAG, "entry: \n" + display); //add to arraylist
                                entries_.add(display);
                            }
                            String[] entries = new String[entries_.size()]; //arraylist -> array
                            for (int j = 0; j < entries_.size(); j++) {
                                entries[j] = entries_.get(j);
                                Log.i(TAG,"entries["+j+"]="+entries[j]);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(answeredQoTD.this, android.R.layout.simple_list_item_1, entries); //set listview
                            mListView.setAdapter(adapter);

                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position,
                                                        long id) {

                                    Intent JournalDetail = new Intent(answeredQoTD.this, JournalDetail.class);
                                    String content = dataSnapshot.child(dates[position]).child("QoTD").child("answer").getValue(String.class);
                                    Boolean checked = dataSnapshot.child(dates[position]).child("QoTD").child("private").getValue(Boolean.class);
                                    Boolean faved = dataSnapshot.child(dates[position]).child("QoTD").child("favorite").getValue(Boolean.class);
                                    JournalDetail.putExtra("date", dates[position]);
                                    JournalDetail.putExtra("content", content);
                                    JournalDetail.putExtra("private", checked);
                                    JournalDetail.putExtra("favorite", faved);
                                    startActivity(JournalDetail);
                                }
                            });
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
