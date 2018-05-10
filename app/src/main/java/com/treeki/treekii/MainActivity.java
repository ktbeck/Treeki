package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treeki-19818.firebaseio.com/Journals");

        FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
                this,
                String.class,
                android.R.layout.simple_list_item_1,
                ref
        ) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setText(model);
            }
        };
        //final ListView lv = (ListView) findViewById(R.id.listView);
        //lv.setAdapter(firebaseListAdapter);
        mListView.setAdapter(firebaseListAdapter);
    }


    /*
    public void PastJournalsClick (View v) {
        startActivity(new Intent(MainActivity.this, pastJournals.class));
    }
    */



}
