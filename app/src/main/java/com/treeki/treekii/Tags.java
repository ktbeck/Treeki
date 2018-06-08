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

public class Tags extends AppCompatActivity {
    private ListView mListView;
    private FirebaseUser user;
    private String TAG = "Tags";
    private ArrayList<String> entries_ = new ArrayList<>();
    String[] entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        setTitle("Tags");
        mListView = (ListView) findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"User: "+user.getUid());

        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(user.getUid()).child("tags").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //for all children
                            String tag = childSnapshot.getKey();
                            entries_.add(tag);
                        }
                        entries = new String[entries_.size()]; //arraylist -> array
                        for(int i = 0; i < entries_.size(); i++) {
                            entries[i] = entries_.get(i);
                        }
                        ArrayAdapter adapter = new ArrayAdapter(Tags.this, android.R.layout.simple_list_item_1,entries); //set listview
                        mListView.setAdapter(adapter);

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                Intent TagJournals = new Intent(Tags.this,TagJournals.class);
                                TagJournals.putExtra("tag",entries[position]);
                                Log.i(TAG,"Tag: "+entries[position]);
                                startActivity(TagJournals);
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
