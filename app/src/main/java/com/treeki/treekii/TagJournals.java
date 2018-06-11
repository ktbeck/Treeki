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

public class TagJournals extends AppCompatActivity {
    private ListView mListView;
    private FirebaseUser user;
    private String TAG = "TagJournals";
    private ArrayList<String> entries_;
    private ArrayList<Boolean> priv_;
    private ArrayList<Boolean> fave_;
    private ArrayList<String> dates_ = new ArrayList<>();
    Boolean checked;
    Boolean faved;
    String display;
    String journal;
    String date;
    String[] entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        getSupportActionBar().hide();

        mListView = (ListView) findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "User: " + user.getUid());
    }
    protected void onResume() {
        super.onResume();
        entries_ = new ArrayList<>();
        fave_ = new ArrayList<>();
        priv_ = new ArrayList<>();
        String tag = getIntent().getStringExtra("tag");
        setTitle(tag);

        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(user.getUid()).child("tags").child(tag).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //for all children
                            date = childSnapshot.getKey();
                            Log.i(TAG,"key: "+date);
                            dates_.add(date);

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //get question at /Questions/date
                                            journal = dataSnapshot.child("answer").getValue(String.class);
                                            checked = dataSnapshot.child("private").getValue(Boolean.class);
                                            faved = dataSnapshot.child("favorite").getValue(Boolean.class);

                                            Log.i(TAG,"journal: "+journal);
                                            if (journal != null) { //if journal not null ie valid, truncate if >40
                                                entries_.add(journal);
                                                if (journal.length() > 39) {
                                                    journal = journal.substring(0, 40)+"...";
                                                }
                                                fave_.add(faved);
                                                priv_.add(checked);
                                            }

                                            String month = null;
                                            int index = -1;

                                            for(int i = 0; i < entries_.size(); i++) {
                                                String d_month = dates_.get(i).split("-")[0];
                                                String d_day = dates_.get(i).split("-")[1];
                                                if (month == null || index == -1) {
                                                    month = d_month;
                                                    index = 0;
                                                } else if (!month.equals(d_month)) {
                                                    month = d_month;
                                                    index = i;
                                                }
                                                if(d_day.length()==1){
                                                    dates_.add(index,dates_.get(i));
                                                    dates_.remove(i+1);
                                                    entries_.add(index,entries_.get(i));
                                                    entries_.remove(i+1);
                                                    priv_.add(index,priv_.get(i));
                                                    priv_.remove(i+1);
                                                    fave_.add(index,fave_.get(i));
                                                    fave_.remove(i+1);
                                                }

                                            }
                                            entries = new String[entries_.size()]; //arraylist -> array
                                            for(int i = 0; i < entries_.size(); i++) {
                                                Log.i(TAG,"entries[i] = "+entries_.get(i));
                                                if (entries_.get(i).length()>39)
                                                    entries[i] = dates_.get(i)+"\n"+entries_.get(i).substring(0,40)+"...";
                                            }

                                            ArrayAdapter adapter = new ArrayAdapter(TagJournals.this, android.R.layout.simple_list_item_1,entries); //set listview
                                            mListView.setAdapter(adapter);

                                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position,
                                                                        long id) {

                                                    String date = ((TextView)view).getText().toString().split("\\n")[0];
                                                    //Log.i(TAG,"date: "+date);

                                                    Intent JournalDetail = new Intent(TagJournals.this,JournalDetail.class);
                                                    JournalDetail.putExtra("date",date);
                                                    JournalDetail.putExtra("content",entries_.get(position));
                                                    JournalDetail.putExtra("private",priv_.get(position));
                                                    JournalDetail.putExtra("favorite",fave_.get(position));
                                                    startActivity(JournalDetail);
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "get Question onCancelled", databaseError.toException());
                                        }
                                    }
                            );

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