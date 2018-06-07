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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class answeredQoTD extends AppCompatActivity {
    DatabaseReference ref;
    private String TAG = "AnsweredQoTD";
    private ListView mListView;
    private FirebaseUser user;
    private TextView QoTD;
    String date;
    String[] dates;
    String question;
    ArrayList<String> entries_;
    private ArrayList<Boolean> priv_;
    private ArrayList<Boolean> fave_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "in answered QOTD");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_qotd);
        setTitle("Past Answers");

        QoTD = findViewById(R.id.QoTD);
        question = getIntent().getStringExtra("question");
        Log.i(TAG, "QoTD: " + question);
        QoTD.setText(question);

        ArrayList<String> dates_ = getIntent().getStringArrayListExtra("dates");
        dates = new String[dates_.size()];
        for (int i = 0; i < dates_.size(); i++) {
            dates[i] = dates_.get(i);
        }
        mListView = (ListView) findViewById(R.id.listView);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
    protected void onResume() {
        super.onResume();
        entries_ = new ArrayList<>();
        fave_ = new ArrayList<>();
        priv_ = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (int i = 0; i < dates.length; i++) {
                            date = dates[i];
                            String answer = dataSnapshot.child(date).child("QoTD").child("answer").getValue(String.class); //get answer
                            Log.i(TAG, "answer: " + answer);
                            String display;
                            if (answer != null) { //if answer not null ie valid, truncate if >40
                                display = date + "\n" + answer;
                                Log.i(TAG, "entry: \n" + display); //add to arraylist
                                entries_.add(display);
                            }
                            priv_.add(dataSnapshot.child(date).child("QoTD").child("private").getValue(Boolean.class));
                            fave_.add(dataSnapshot.child(date).child("QoTD").child("favorite").getValue(Boolean.class));
                            String[] entries = new String[entries_.size()]; //arraylist -> array
                            for (int j = 0; j < entries_.size(); j++) {
                                entries[j] = entries_.get(j);
                                Log.i(TAG,"entries["+j+"]="+entries[j]);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(answeredQoTD.this, android.R.layout.simple_list_item_1, entries); //set listview
                            mListView.setAdapter(adapter);

                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //listview onclick handler
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position,
                                                        long id) {

                                    Intent QoTDDetail = new Intent(answeredQoTD.this, QoTDDetail.class);
                                    String content = dataSnapshot.child(dates[position]).child("QoTD").child("answer").getValue(String.class);
                                    QoTDDetail.putExtra("question",question);
                                    QoTDDetail.putExtra("date", dates[position]);
                                    QoTDDetail.putExtra("content", content);
                                    QoTDDetail.putExtra("private",priv_.get(position));
                                    QoTDDetail.putExtra("favorite",fave_.get(position));
                                    startActivity(QoTDDetail);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get Journal answer onCancelled", databaseError.toException());
                    }
                }
        );

    }

    private void startJournal() {
        Intent journal = new Intent(this,Journal.class);
        startActivity(journal);
    }

    private void goToNextActivity() {
        ref.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/0101
                        String answer = dataSnapshot.getValue(String.class);

                        //error handling
                        if (answer == null) {
                            Log.i(TAG,"User did not journal");
                            startJournal();
                        }
                        //if no err, change the question
                        else {
                            //GOTO MAIN MENU
                            Log.i(TAG,"User did journal, going to main");
                            Intent mainmenuIntent = new Intent(answeredQoTD.this,MainMenuTest.class);
                            startActivity(mainmenuIntent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get Journal answer onCancelled", databaseError.toException());
                    }
                }
        );}


    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.past, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.contin:
                /*the R.id.action_favorite is the ID of our button (defined in strings.xml).
                Change Activity here (if that's what you're intending to do, which is probably is).
                 */
                goToNextActivity();
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}
