package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Friends extends AppCompatActivity {
    private String TAG = "Friends";
    private EditText username_;
    private Button search_;
    private FirebaseUser user;
    private ListView mListView;
    private ArrayList<String> friends_ = new ArrayList<>();
    private ArrayList<String> friends_id = new ArrayList<>();
    String username;
    String f_user;
    private DatabaseReference mDatabase;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getSupportActionBar().hide();

        username_ = (EditText) findViewById(R.id.Username);
        search_ = (Button) findViewById(R.id.search);
        mListView = (ListView) findViewById(R.id.listView);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"Current: "+user.getUid());
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    protected void onResume() {
        super.onResume();
        friends_.clear();
        friends_id.clear();

        mDatabase.child("Friends").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            String fr_user = childSnapshot.getKey();
                            Log.i(TAG,"id: "+fr_user);
                            String fr_username = childSnapshot.getValue(String.class);
                            Log.i(TAG,"user: "+fr_username);
                            friends_.add(fr_username);
                            friends_id.add(fr_user);
                        }
                        adapter = new ArrayAdapter(Friends.this,android.R.layout.simple_list_item_1,friends_);
                        mListView.setAdapter(adapter);
                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                Intent friend = new Intent(Friends.this,PastJournals.class);
                                friend.putExtra("user_id",friends_id.get(position));
                                friend.putExtra("user",friends_.get(position));
                                startActivity(friend);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG,"checkpast cancelled");
                    }
                }
        );
    }

    public void search(View view) {
        username = username_.getText().toString();
        Log.i(TAG,"searching username: "+username);

        mDatabase.child("Users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean found = false;
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            f_user = childSnapshot.getKey();
                            String f_username = childSnapshot.child("Username").getValue(String.class);
                            Log.i(TAG,"Found user: "+f_username);
                            if (f_username != null && f_username.toLowerCase().equals(username.toLowerCase())) {
                                Log.i(TAG,"Added "+f_username);
                                mDatabase.child("Friends").child(user.getUid()).child(f_user).setValue(f_username);
                                friends_.add(f_username);
                                friends_id.add(f_user);
                                adapter.notifyDataSetChanged();
                                found = true;
                            }
                        }

                        if(!found) {
                            Toast.makeText(Friends.this,"User not found",Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"couldn't find "+username);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG,"checkpast cancelled");
                    }
                }
        );
    }
}
