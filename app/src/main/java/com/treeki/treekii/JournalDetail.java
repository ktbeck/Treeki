package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

public class JournalDetail extends AppCompatActivity {
    private CheckBox priv;
    private String TAG = "JournalDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        Intent i = getIntent();
        String content = i.getStringExtra("content");
        String date = i.getStringExtra("date");
        Boolean checked = i.getBooleanExtra("private",false);

        TextView c = (TextView) findViewById(R.id.jourContent);
        TextView d = (TextView) findViewById(R.id.dateView);
        c.setText(content);
        d.setText(date);

        priv = (CheckBox) findViewById(R.id.priv);
        priv.setChecked(checked);
        priv.setEnabled(false);
    }
}