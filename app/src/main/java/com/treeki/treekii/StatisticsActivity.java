package com.treeki.treekii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "Stats";
    private FirebaseUser user;
    String date;
    Integer mood;
    String month;
    String day;
    String year;
    double avg;
    private ArrayList<Integer> mood_;
    private ArrayList<Date> dates_;


    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        date = month+"-"+day+"-"+year;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    protected void onResume(){
        super.onResume();
        TextView text = (TextView) findViewById(R.id.avg_mood);
        Log.i(TAG,"fish user: "+user.getUid());
        mood_ = new ArrayList<>();
        Log.i(TAG,"fish 1");
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        Log.i(TAG,"fish 2");
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //for all children
                            Log.i(TAG,"fish 3");
                            date = childSnapshot.getKey();
                            Log.i(TAG,"key: "+date);
                            mood = childSnapshot.child("mood").getValue(Integer.class); //get mood
                            if (mood != null) {
                                Log.i(TAG,"Mood: " + mood);
                                String month_ = date.split("-")[0];
                                String day_ = date.split("-")[1];
                                mood_.add(mood);
//                                dates_.add()

                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "Error", databaseError.toException());
                    }
                }
        );
        if(mood_.size() ==0){
            Log.i(TAG, "mood is empty");
        }
        for(int i = 0; i < mood_.size(); i++){
            Log.i(TAG, "fish i = " +i + " " + mood_.get(i));
        }
        avg = average(mood_);
        text.setText("Monthly Average is currently: "+ Double.toString(avg));
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                for(int i = 0; i < entries_.size(); i++){
//                    new DataPoint(entries_));
//                }
                new DataPoint(0, 6), //has to be in order of x
                new DataPoint(3, 2),
                new DataPoint(6, 7),
                new DataPoint(7, 1),
                new DataPoint(8, 4)

        });
        graph.addSeries(series);
    }
    private double average(ArrayList <Integer> mood) {
        Integer sum = 0;
        if(!mood.isEmpty()) {
            for (Integer i : mood) {
                sum += i;
                Log.e(TAG, "Integer i =" + i);
            }
            return sum.doubleValue() / mood.size();
        }
        return sum;
    }
}
