package com.treeki.treekii;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        final String[] content = new String[1];
        final String[] ok = new String[1];
        //String ok;

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treeki-19818.firebaseio.com/Users/EWjIw3tshHgDiOtNlmDTK5JET922/05-18-2018/Journal");
        //final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treeki-19818.firebaseio.com/Users/EWjIw3tshHgDiOtNlmDTK5JET922/05-18-2018/QoTD");

        /*
        String url = "https://treeki-19818.firebaseio.com/Users/EWjIw3tshHgDiOtNlmDTK5JET922/";
        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl(url);
        String w = ref2.getKey();
        ref2.child("Journal");
        */

        FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
                this,
                String.class,
                android.R.layout.simple_list_item_1,
                ref
        ) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                String sub = model.substring(0, Math.min(model.length(), 40));
                content[0] = model;
                ok[0] = ref.getParent().getKey();
                //String ok = ref.getParent().getKey();
                String show;
                if (model.length() <= 40){
                    show = ok[0] + "\n" + sub;
                }
                else {
                    show = ok[0] + "\n" + sub + "...";
                }
                textView.setText(show);
            }
        };
        mListView.setAdapter(firebaseListAdapter);

        final Context context = this;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ListData selected = ref.get(position);
                Intent journalDetail = new Intent(context, JournalDetail.class);
                journalDetail.putExtra("content", content[0]);
                journalDetail.putExtra("date", ok[0]);
                startActivity(journalDetail);
            }

        });
    }


    /*
    public void PastJournalsClick (View v) {
        startActivity(new Intent(MainActivity.this, pastJournals.class));
    }
    */



}