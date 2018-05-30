package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Calendar;

public class JournalDetail extends AppCompatActivity {
    private CheckBox priv;
    private String TAG = "JournalDetail";
    String month;
    String day;
    String year;
    String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        if (month.length() == 1) month = "0"+month;
        if (day.length() == 1) day = "0"+day;
        today = month+"-"+day+"-"+year;

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

        Button edit = (Button) findViewById(R.id.edit);
        Button delete = (Button) findViewById(R.id.delete);
        if (!date.equals(today)){
            edit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }
    }
}