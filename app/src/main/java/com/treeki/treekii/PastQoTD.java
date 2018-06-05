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

public class PastQoTD extends AppCompatActivity {
    private ListView mListView;
    private FirebaseUser user;
    private String TAG = "PastQoTD";
    private ArrayList<String> entries_ = new ArrayList<>();
    String qotd;
    String date;
    int num = 0;

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_qotd);
        mListView = (ListView) findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"User: "+user.getUid());

        //Get datasnapshot at your "users" root node
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (final DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //for all children
                            date = childSnapshot.getKey();
                            Log.i(TAG,"key: "+date);
                            qotd = childSnapshot.child("QoTD").child("answer").getValue(String.class); //get qotd answer
                            if (qotd != null && !date.equals("tags") && !date.equals("favorites")) {
                                if (qotd.length() > 40)
                                    qotd = qotd.substring(0, 40) + "...";
                                Log.i(TAG,"QOTD: "+qotd);

                                //                            Log.i(TAG,"qotd answer: "+qotd);
                                String month_ = date.split("-")[0];
                                String day_ = date.split("-")[1];

                                entries_.add(date+"\n"+qotd);
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

                                                String[] line = ((TextView)view).getText().toString().split("\n|\\| ");
                                                String date = line[0];
                                                String question = line[1];
                                                String ans = line[2];
                                                //Log.i(TAG,"date: "+date);

                                                Intent QoTDDetail = new Intent(PastQoTD.this,QoTDDetail.class);
                                                Boolean checked = childSnapshot.child("QoTD").child("private").getValue(Boolean.class);
                                                Boolean faved = childSnapshot.child("QoTD").child("favorite").getValue(Boolean.class);
                                                Log.i(TAG,"click: question: "+question);
                                                Log.i(TAG,"click: date: "+date);
                                                Log.i(TAG,"click: ans: "+ans);
                                                Log.i(TAG,"click: checked: "+checked);
                                                Log.i(TAG,"click: faved: "+faved);
//                                                QoTDDetail.putExtra("question",question);
//                                                QoTDDetail.putExtra("date",date);
//                                                QoTDDetail.putExtra("content",ans);
//                                                QoTDDetail.putExtra("private",checked);
//                                                QoTDDetail.putExtra("favorite",faved);
//                                                startActivity(QoTDDetail);
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
}
