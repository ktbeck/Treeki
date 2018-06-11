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
import android.widget.Button;
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

public class PastQoTD extends AppCompatActivity {
    private ListView mListView;
    private String TAG = "PastQoTD";
    private ArrayList<String> entries_;
    private ArrayList<String> ans_;
    private ArrayList<Boolean> priv_;
    private ArrayList<Boolean> fave_;
    String user_id;
    String user;
    Boolean other = true;
    String qotd;
    String date;
    Boolean priv;
    Boolean faved;
    int num = 0;

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        TextView title = findViewById(R.id.title);
        title.setText("QoTD Answers");
        setTitle("QoTD Answers");
        getSupportActionBar().hide();
        mListView = (ListView) findViewById(R.id.listView);
        user_id = getIntent().getStringExtra("user_id");
        user = getIntent().getStringExtra("user");
        if(user != null) {
            title.setText(user+" Answers");
            Button journal_button = findViewById(R.id.qotd);
            journal_button.setText("Journal");
            journal_button.setVisibility(View.VISIBLE);
        }
        if(user_id == null) {
            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            other = false;
        }

    }
    protected void onResume(){
        super.onResume();
        entries_ = new ArrayList<>();
        fave_ = new ArrayList<>();
        priv_ = new ArrayList<>();
        ans_ = new ArrayList<>();
        num = 0;

        //Get datasnapshot at your "users" root node
        ref.child("Users").child(user_id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        String month = null;
                        int index = -1;
                        int cur = 0;
                        for (final DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //for all children
                            date = childSnapshot.getKey();
                            Log.i(TAG,"key: "+date);
                            qotd = childSnapshot.child("QoTD").child("answer").getValue(String.class); //get qotd answer
                            if(other) {
                                Boolean pri = childSnapshot.child("QoTD").child("private").getValue(Boolean.class);
                                if (pri!=null && pri) qotd = null;
                            }

                            if (qotd != null && !date.equals("tags") && !date.equals("Username")) {

                                if (month == null || index == -1) {
                                    month = date.split("-")[0];
                                    index = 0;
                                } else if (!month.equals(date.split("-")[0])) {
                                    month = date.split("-")[0];
                                    index = cur;
                                }
                                String answer = qotd;
                                if (qotd.length() > 40)
                                    qotd = qotd.substring(0, 40) + "...";
                                Log.i(TAG,"QOTD: "+qotd);

                                //                            Log.i(TAG,"qotd answer: "+qotd);
                                String month_ = date.split("-")[0];
                                String day_ = date.split("-")[1];

                                cur += 1;
                                if (date.split("-")[1].length() == 1) {
                                    entries_.add(index, date+"\n"+qotd);
                                    ans_.add(index,answer);
                                    priv_.add(index,childSnapshot.child("Journal").child("private").getValue(Boolean.class));
                                    fave_.add(index,childSnapshot.child("Journal").child("favorite").getValue(Boolean.class));
                                    index += 1;
                                }
                                else {
                                    entries_.add(date+"\n"+qotd);
                                    ans_.add(answer);
                                    priv_.add(childSnapshot.child("Journal").child("private").getValue(Boolean.class));
                                    fave_.add(childSnapshot.child("Journal").child("favorite").getValue(Boolean.class));
                                }
                                ref.child("Questions").child(month_).child(day_).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        final String question = dataSnapshot2.getValue(String.class);
                                        String trun_question = question;
//                                        if (question.length() > 35)
//                                            trun_question = question.substring(0, 35) + "...";
                                        Log.i(TAG, "Question: " + question);

                                        String[] parts = entries_.get(num).split("\n");
                                        Log.i(TAG,"PARTS[0]: "+parts[0]);
                                        Log.i(TAG,"PARTS[1]: "+parts[1]);
                                        entries_.set(num,parts[0]+" | "+trun_question+"\n"+parts[1]);
                                        Log.i(TAG, "entry: " + entries_.get(num));
                                        num += 1;


                                        String[] entries = new String[entries_.size()]; //arraylist -> array
                                        for (int i = 0; i < entries_.size(); i++) {
                                            entries[i] = entries_.get(i);
                                        }
                                        ArrayAdapter adapter = new ArrayAdapter(PastQoTD.this, android.R.layout.simple_list_item_1,entries); //set listview
                                        mListView.setAdapter(adapter);
                                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                    long id) {

                                                String[] line = ((TextView)view).getText().toString().split("\n| \\| ");
                                                String date = line[0];
                                                String question = line[1];
                                                String ans = line[2];
                                                //Log.i(TAG,"date: "+date);

                                                Intent QoTDDetail = new Intent(PastQoTD.this,QoTDDetail.class);
                                                Log.i(TAG,"click: question: "+question);
                                                Log.i(TAG,"click: date: "+date);
                                                Log.i(TAG,"click: ans: "+ans);
                                                Log.i(TAG,"click: checked: "+priv_.get(position));
                                                Log.i(TAG,"click: faved: "+fave_.get(position));
                                                QoTDDetail.putExtra("question",question);
                                                QoTDDetail.putExtra("other",other);
                                                QoTDDetail.putExtra("date",date);
                                                QoTDDetail.putExtra("content",ans_.get(position));
                                                QoTDDetail.putExtra("private",priv_.get(position));
                                                QoTDDetail.putExtra("favorite",fave_.get(position));
                                                QoTDDetail.putExtra("source","PastQoTD");
                                                startActivity(QoTDDetail);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get QoTD answer onCancelled", databaseError.toException());
                    }
                }
        );
    }

    public void swap(View view){
        Intent i = new Intent(PastQoTD.this,PastJournals.class);
        i.putExtra("user",user);
        i.putExtra("user_id",user_id);
        startActivity(i);
        finish();
    }

    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.journal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.journal:
                /*the R.id.action_favorite is the ID of our button (defined in strings.xml).
                Change Activity here (if that's what you're intending to do, which is probably is).
                 */
                Intent i = new Intent(this, PastJournals.class);
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
