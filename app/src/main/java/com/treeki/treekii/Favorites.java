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
    String month;
    String day;
//    String JorQ = getIntent().getStringExtra("JorQ");
    String JorQ = "QoTD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        setTitle("Favorites");
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
                            Boolean priv = childSnapshot.child(JorQ).child("favorite").getValue(Boolean.class); //get answer
                            if (priv != null && priv == true) {
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
                                }
                            }

                        }
                        String[] entries = new String[entries_.size()]; //arraylist -> array
                        for(int i = 0; i < entries_.size(); i++) {
                            entries[i] = entries_.get(i);
                        }
                        ArrayAdapter adapter = new ArrayAdapter(Favorites.this, android.R.layout.simple_list_item_1,entries); //set listview
                        mListView.setAdapter(adapter);

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {

                                String date = ((TextView)view).getText().toString().split("\\n")[0];
                                //Log.i(TAG,"date: "+date);

                                final Intent JorQDetail;
                                if (JorQ == "Journal")
                                    JorQDetail = new Intent(Favorites.this,JournalDetail.class);
                                else
                                    JorQDetail = new Intent(Favorites.this,QoTDDetail.class);

                                String content = dataSnapshot.child(date).child(JorQ).child("answer").getValue(String.class);
                                Boolean checked = dataSnapshot.child(date).child(JorQ).child("private").getValue(Boolean.class);
                                Boolean faved = dataSnapshot.child(date).child(JorQ).child("favorite").getValue(Boolean.class);
                                JorQDetail.putExtra("date",date);
                                JorQDetail.putExtra("content",content);
                                JorQDetail.putExtra("private",checked);
                                JorQDetail.putExtra("favorite",faved);

                                if (JorQ == "QoTD") {
                                    month = date.split("-")[0];
                                    day = date.split("-")[1];
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("Questions").child(month).child(day).addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    //get question at /Questions/date
                                                    String question = dataSnapshot.getValue(String.class);

                                                    //error handling
                                                    if (question == null) {
                                                        Log.e(TAG, "Question at "+month+"/"+day+" is unexpectedly null");
                                                        Toast.makeText(getApplicationContext(),"can't fetch question",Toast.LENGTH_SHORT).show();
                                                    }
                                                    //if no err, send question
                                                    else {
                                                        Log.i(TAG, "Question at"+month+"/"+day+" is: "+question);
                                                        JorQDetail.putExtra("question",question);
                                                        startActivity(JorQDetail);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.w(TAG, "get Question onCancelled", databaseError.toException());
                                                }
                                            }
                                    );
                                }
                                else
                                    startActivity(JorQDetail);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get JorQ answer onCancelled", databaseError.toException());
                    }
                }
        );
    }
}
