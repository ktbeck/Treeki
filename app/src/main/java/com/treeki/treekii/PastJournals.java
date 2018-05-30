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
import java.util.Map;

public class PastJournals extends AppCompatActivity {
    private ListView mListView;
    private FirebaseUser user;
    private String TAG = "PastJournals";
    private ArrayList<String> entries_ = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        mListView = (ListView) findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"User: "+user.getUid());

        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //for all children
                            String display;
                            String date = childSnapshot.getKey();
//                            Log.i(TAG,"key: "+date);
                            String journal = childSnapshot.child("Journal").child("answer").getValue(String.class); //get journal
//                            Log.i(TAG,"journal: "+journal);
                            if (journal != null) { //if journal not null ie valid, truncate if >40
                                if (journal.length() < 40) {
                                    display = date + "\n" + journal;
                                } else {
                                    display = date + "\n" + journal.substring(0, 40)+"...";
                                }
                                Log.i(TAG, "entry: \n" + display); //add to arraylist
                                entries_.add(display);
                            }

                        }
                        String[] entries = new String[entries_.size()]; //arraylist -> array
                        for(int i = 0; i < entries_.size(); i++) {
                            entries[i] = entries_.get(i);
                        }
                        ArrayAdapter adapter = new ArrayAdapter(PastJournals.this, android.R.layout.simple_list_item_1,entries); //set listview
                        mListView.setAdapter(adapter);

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {

                                String date = ((TextView)view).getText().toString().split("\\n")[0];
                                //Log.i(TAG,"date: "+date);

                                Intent JournalDetail = new Intent(PastJournals.this,JournalDetail.class);
                                String content = dataSnapshot.child(date).child("Journal").child("answer").getValue(String.class);
                                JournalDetail.putExtra("date",date);
                                JournalDetail.putExtra("content",content);
                                startActivity(JournalDetail);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get Journal answer onCancelled", databaseError.toException());
                    }
                }
        );
    }
}
