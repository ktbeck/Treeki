package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PastJournals extends AppCompatActivity {
    private ListView mListView;
    Boolean other = true;
    String user_id;
    String user;
    private String TAG = "PastJournals";
    private ArrayList<String> entries_;
    private ArrayList<Boolean> fave_;
    private ArrayList<Boolean> priv_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        setTitle("Past Journals");
        mListView = (ListView) findViewById(R.id.listView);
        Intent i = getIntent();
        user_id = i.getStringExtra("user_id");
        user = getIntent().getStringExtra("user");
        if (user != null)
            setTitle(user+"'s Journals");
        if(user_id == null) {

            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            other = false;
        }
    }
    protected void onResume() {
        super.onResume();
        entries_ = new ArrayList<>();
        fave_ = new ArrayList<>();
        priv_ = new ArrayList<>();
        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(user_id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        String month = null;
                        int index = -1;
                        int cur = 0;
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //for all children

                            String date = childSnapshot.getKey();
                            if(!date.equals("Username") && !date.equals("tags")) {
                                if (month == null || index == -1) {
                                    month = date.split("-")[0];
                                    index = 0;
                                } else if (!month.equals(date.split("-")[0])) {
                                    month = date.split("-")[0];
                                    index = cur;
                                }
                                String display;
//                            Log.i(TAG,"key: "+date);
                                String journal;
                                journal = childSnapshot.child("Journal").child("answer").getValue(String.class); //get journal
                                if (other) {
                                    Boolean pri = childSnapshot.child("Journal").child("private").getValue(Boolean.class);
                                    if (pri != null && pri) journal = null;
                                }
//                            Log.i(TAG,"journal: "+journal);
                                if (journal != null) { //if journal not null ie valid, truncate if >40
                                    if (journal.length() < 40) {
                                        display = date + "\n" + journal;
                                    } else {
                                        display = date + "\n" + journal.substring(0, 40) + "...";
                                    }
                                    Log.i(TAG, "entry: \n" + display); //add to arraylist

                                    cur += 1;
                                    if (date.split("-")[1].length() == 1) {
                                        entries_.add(index, display);
                                        priv_.add(index, childSnapshot.child("Journal").child("private").getValue(Boolean.class));
                                        fave_.add(index, childSnapshot.child("Journal").child("favorite").getValue(Boolean.class));
                                        index += 1;
                                    } else {
                                        entries_.add(display);
                                        priv_.add(childSnapshot.child("Journal").child("private").getValue(Boolean.class));
                                        fave_.add(childSnapshot.child("Journal").child("favorite").getValue(Boolean.class));
                                    }
                                }
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
                                JournalDetail.putExtra("other",other);
                                JournalDetail.putExtra("date",date);
                                JournalDetail.putExtra("content",content);
                                JournalDetail.putExtra("private",priv_.get(position));
                                JournalDetail.putExtra("favorite",fave_.get(position));
                                JournalDetail.putExtra("source","PastJournal");
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
    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.qotd, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.qotd:
                /*the R.id.action_favorite is the ID of our button (defined in strings.xml).
                Change Activity here (if that's what you're intending to do, which is probably is).
                 */
                Intent i = new Intent(this, PastQoTD.class);
                if(other) {
                    i.putExtra("user_id", user_id);
                    i.putExtra("user", user);
                }
                startActivity(i);
                finish();
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

}
