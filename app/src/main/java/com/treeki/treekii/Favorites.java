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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Favorites extends AppCompatActivity {
    private ListView mListView;
    private FirebaseUser user;
    private String TAG = "Favorites";
    private ArrayList<String> entries_ = new ArrayList<>();
    private ArrayList<String> ans_ = new ArrayList<>();
    private ArrayList<String> q_ = new ArrayList<>();
    private ArrayList<Boolean> priv_ = new ArrayList<>();
    private ArrayList<Boolean> fave_ = new ArrayList<>();
    String question;
    String month;
    String day;
    String JorQ;
    String[] parts;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JorQ = getIntent().getStringExtra("JorQ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        TextView title = findViewById(R.id.title);
        if(JorQ.equals("Journal"))
            title.setText("Favorite Journals");
        else if(JorQ.equals("QoTD"))
            title.setText("Favorite Q/A");
        getSupportActionBar().hide();
        setTitle(JorQ+" Favorites");
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
                            Boolean priv = childSnapshot.child(JorQ).child("private").getValue(Boolean.class); //get answer
                            Boolean fav = childSnapshot.child(JorQ).child("favorite").getValue(Boolean.class); //get answer

                            if (fav != null && fav == true) {
                                String answer = childSnapshot.child(JorQ).child("answer").getValue(String.class); //get answer
//                            Log.i(TAG,"answer: "+answer);
                                if (answer != null) { //if answer not null ie valid, truncate if >40
                                    if (answer.length() < 40) {
                                        display = date + "\n" + answer;
                                    } else {
                                        display = date + "\n" + answer.substring(0, 40) + "...";
                                    }
                                    Log.i(TAG, "entry: \n" + display); //add to arraylist
                                    entries_.add(display);
                                    priv_.add(priv);
                                    fave_.add(fav);
                                    ans_.add(answer);
                                }
                            }

                        }

                        if(JorQ.equals("QoTD")){
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("Questions").addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                //get question at /Questions/date

                                                for (i = 0; i < entries_.size(); i++) {
                                                    month = entries_.get(i).split("-")[0];
                                                    day = entries_.get(i).split("-")[1];
                                                    question = dataSnapshot.child(month).child(day).getValue(String.class);

                                                    //error handling
                                                    if (question == null) {
                                                        Log.e(TAG, "Question at " + month + "/" + day + " is unexpectedly null");
                                                        Toast.makeText(getApplicationContext(), "can't fetch question", Toast.LENGTH_SHORT).show();
                                                    }
                                                    //if no err, send question
                                                    else {
                                                        Log.i(TAG, "Question at" + month + "/" + day + " is: " + question);
                                                        parts = entries_.get(i).split("\\n");
                                                        entries_.set(i, parts[0] + " | " + question +"\n"+parts[1]);
                                                        q_.add(question);
                                                    }

                                                    ArrayAdapter adapter = new ArrayAdapter(Favorites.this, android.R.layout.simple_list_item_1, entries_); //set listview
                                                    mListView.setAdapter(adapter);

                                                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                                long id) {

                                                            String date = parts[0];
                                                            //Log.i(TAG,"date: "+date);

                                                            Intent JorQDetail = new Intent(Favorites.this, QoTDDetail.class);

                                                            JorQDetail.putExtra("date", date);
                                                            JorQDetail.putExtra("content", ans_.get(position));
                                                            JorQDetail.putExtra("private", priv_.get(position));
                                                            JorQDetail.putExtra("favorite", fave_.get(position));
                                                            JorQDetail.putExtra("question", q_.get(position));
                                                            startActivity(JorQDetail);
                                                        }
                                                    });

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.w(TAG, "get Question onCancelled", databaseError.toException());
                                            }
                                        }
                                );



                        }

                        else if (JorQ.equals("Journal")) {
                            ArrayAdapter adapter = new ArrayAdapter(Favorites.this, android.R.layout.simple_list_item_1,entries_); //set listview
                            mListView.setAdapter(adapter);

                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position,
                                                        long id) {

                                    String date = ((TextView)view).getText().toString().split("\\n")[0];
                                    //Log.i(TAG,"date: "+date);

                                    Intent JorQDetail = new Intent(Favorites.this,JournalDetail.class);

                                    String content = dataSnapshot.child(date).child(JorQ).child("answer").getValue(String.class);
                                    Boolean checked = dataSnapshot.child(date).child(JorQ).child("private").getValue(Boolean.class);
                                    Boolean faved = dataSnapshot.child(date).child(JorQ).child("favorite").getValue(Boolean.class);
                                    JorQDetail.putExtra("date",date);
                                    JorQDetail.putExtra("content",content);
                                    JorQDetail.putExtra("private",checked);
                                    JorQDetail.putExtra("favorite",faved);
                                    startActivity(JorQDetail);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get JorQ answer onCancelled", databaseError.toException());
                    }
                }
        );
    }    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        if(JorQ == "QoTD") {
            Log.i(TAG, "HAHAHA");
            inflater.inflate(R.menu.journal, menu);
        }
        else {
            Log.i(TAG, "YOYOYO");
            inflater.inflate(R.menu.qotd, menu);
        }
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
                Intent i = new Intent(this, Favorites.class);
                if(JorQ == "QoTD")
                    i.putExtra("JorQ","Journal");
                else
                    i.putExtra("JorQ","QoTD");
                startActivity(i);
                finish();
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}
