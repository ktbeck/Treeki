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
    private ArrayList<String> entries_ = new ArrayList<>();
    private ArrayList<String> dates_ = new ArrayList<>();
    Boolean checked;
    Boolean faved;
    String display;
    String journal;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_journals);
        mListView = (ListView) findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "User: " + user.getUid());
    }
    protected void onResume() {
        super.onResume();
        entries_.clear();
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
                                                if (journal.length() > 39) {
                                                    journal = journal.substring(0, 40)+"...";
                                                }
                                                entries_.add(journal);
                                            }
                                            String[] entries = new String[entries_.size()]; //arraylist -> array
                                            for(int i = 0; i < entries_.size(); i++) {
                                                Log.i(TAG,"entries[i] = "+entries_.get(i));
                                                entries[i] = dates_.get(i)+"\n"+entries_.get(i);
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
                                                    JournalDetail.putExtra("content",journal);
                                                    JournalDetail.putExtra("private",checked);
                                                    JournalDetail.putExtra("favorite",faved);
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