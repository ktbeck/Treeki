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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "Stats";
    private FirebaseUser user;
    String date;
    String mood;
    String month;
    String day;
    String year;
    double avg;
    private ArrayList<Integer> mood_;
    private ArrayList<Date> dates_;
    int high[];
    int low[];
    int moodInt;
    DateFormat df = new SimpleDateFormat("MM/dd/YYYY");

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
//        Log.i(TAG, "MONTH   :" + month);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        date = month+"-"+day+"-"+year;
        user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.i(TAG, "CuRRuSER = " + user.getUid());



    }

    protected void onResume(){
        super.onResume();
//        TextView text = (TextView) findViewById(R.id.avg_mood);
//        Log.i(TAG,"fish user: "+user.getUid());

        mood_ = new ArrayList<>();
        dates_ = new ArrayList<>();
//        Log.i(TAG,"fish 1");
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) { //if child /date
                          date = childSnapshot.getKey();
                            Log.i(TAG,"key:" + date);
                            mood = childSnapshot.child("mood").getValue(String.class); //get mood
                            if(mood != null) {
                                TextView text = (TextView) findViewById(R.id.avg_mood);
                                moodInt = Integer.valueOf(mood);
                                String month_ = date.split("-")[0];
                                String day_ = date.split("-")[1];
                                Log.i(TAG, "GIANT DATE = " + month_ + 1);
                                if(month_.equals(month)) {
                                    mood_.add(moodInt);
                                    Calendar date = Calendar.getInstance();
                                    date.set(Calendar.YEAR, Integer.parseInt(year));
                                    date.set(Calendar.MONTH, Integer.parseInt(month_));
                                    date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day_));
                                    Date utilDate = date.getTime();
                                    dates_.add(utilDate);
                                    Log.i(TAG, "DATES SIZE: " + utilDate);
                                }
                                for(int i = 0; i < dates_.size(); i++){
                                    Log.i(TAG,"DATES" + dates_.get(i));
                                }

                                avg = averageArr(mood_);
                                high = bestDay(mood_);
                                low = worstDay(mood_);
                                text.setText("Monthly Average is currently: "+ Double.toString(avg));
                                GraphView graph = (GraphView) findViewById(R.id.graph);

                                DataPoint[] dp = new DataPoint[mood_.size()];
//                                for(int i=1; i < mood_.size() + 1; i++) {
//                                    if (mood_.get(i-1) != null) {
//                                        dp[i-1] = new DataPoint(i, mood_.get(i-1));
//                                    }
//                                }
                                for(int i=1; i < mood_.size() + 1; i++) {
                                    if (mood_.get(i-1) != null) {
                                        dp[i-1] = new DataPoint(dates_.get(i-1), mood_.get(i-1));
                                        Log.i(TAG, "DP[i]" + dp[i-1]);
                                    }
                                }
                                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
                                graph.getViewport().setMinX(1);
                                graph.getViewport().setMaxX(Integer.parseInt(day));
                                graph.getViewport().setMinY(0);
                                graph.getViewport().setMaxY(10);
                                graph.getGridLabelRenderer().setHumanRounding(true);
                                graph.getGridLabelRenderer().setNumHorizontalLabels(Integer.parseInt(day));
                                graph.getGridLabelRenderer().setNumVerticalLabels(11);
                                graph.getViewport().setYAxisBoundsManual(true);
                                graph.getViewport().setXAxisBoundsManual(true);
                                graph.addSeries(series);

                                }
                            }
                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "Error", databaseError.toException());
                    }
                }
        );

    }
    private double averageArr(ArrayList <Integer> mood) {
        Integer sum = 0;
        if(!mood.isEmpty()) {
            for (Integer i : mood) {
                sum += i;

            }
            return sum.doubleValue() / mood.size();
        }
        else{
        }
        return sum;
    }
    private int[] bestDay(ArrayList <Integer> mood) {
        Integer high = 0;
        int array[] = new int[31];
        int j = 0;
        if(!mood.isEmpty()) {
            for (Integer i : mood) {
                if(i > high) {
                    high = i;
                }
            }
            for(Integer i : mood){
                if(i == high){
                    array[j] = i;
                    j++;
                }
            }
            return array;
        }
        else{
            return array;
        }
    }
    private int[] worstDay(ArrayList <Integer> mood) {
        Integer low = 0;
        int array[] = new int[31];
        int j = 0;
        if(!mood.isEmpty()) {
            for (Integer i : mood) {
                if(i < low) {
                    low = i;
                }
            }
            for(Integer i : mood){
                if(i == low){
                    array[j] = i;
                    j++;
                }
            }
            return array;
        }
        else{
            return array;
        }
    }
}
