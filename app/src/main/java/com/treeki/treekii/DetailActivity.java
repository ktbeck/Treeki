package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        String title = i.getStringExtra("first");
        String description = i.getStringExtra("second");

        TextView t = (TextView)findViewById(R.id.textView3);
        TextView d = (TextView)findViewById(R.id.textView4);

        t.setText(title);
        d.setText(description);
    }
}
