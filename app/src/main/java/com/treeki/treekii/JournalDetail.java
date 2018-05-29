package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class JournalDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        Intent i = getIntent();
        String content = i.getStringExtra("content");
        String date = i.getStringExtra("date");

        TextView c = (TextView) findViewById(R.id.jourContent);
        TextView d = (TextView) findViewById(R.id.dateView);
        c.setText(content);
        d.setText(date);
    }
}